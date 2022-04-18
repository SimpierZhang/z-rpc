package com.zrpc.registry.utils;

import com.zrpc.config.enums.RpcConfigEnum;
import com.zrpc.utils.PropertiesFileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Properties;

/**
 * @Author: Zjw
 * @Description: a tool class to operate zookeeper registration center
 * @Create 2022-04-15 19:21
 * @Modifier:
 */
@Slf4j
public class CuratorUtils {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static final int CONNECT_TIME = 30;
    private static final String ROOT_PATH = "/z-rpc";
    private static CuratorFramework zkClient = null;
    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";

    /**
     *
     * @Author Zjw
     * @Description get the zk client
     * @Date  2022/4/15 20:26
     * @return org.apache.curator.framework.CuratorFramework
     **/
    public static CuratorFramework getZkClient(){
        try {
            if(zkClient == null){
                synchronized (CuratorUtils.class){
                    if(zkClient == null){
                        Properties properties = PropertiesFileUtils.readPropertiesFile(RpcConfigEnum.PROPERTIES_FILE_PATH.getValue());
                        String zkAddress = DEFAULT_ZK_ADDRESS;
                        if(properties != null){
                            String propertyZkAddress = properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getValue());
                            if(propertyZkAddress != null) zkAddress = propertyZkAddress;
                        }
                        zkClient = CuratorFrameworkFactory.builder()
                                .connectString(zkAddress)
                                .retryPolicy(new RetryNTimes(MAX_RETRIES, BASE_SLEEP_TIME))
                                .connectionTimeoutMs(CONNECT_TIME * 1000)
                                .build();
                        zkClient.start();
                    }
                }
            }
            return zkClient;
        }catch (Exception e){
            throw new RuntimeException("create zk client failed..." + e.getMessage(), e);
        }
    }

    /**
     *
     * @Author Zjw
     * @Description create ephemeral zk node
     * @Date  2022/4/15 20:26
     * @param path
     **/
    public static void createEphemeralNode(String path){
        createNode(path, CreateMode.EPHEMERAL);
    }

    /**
     *
     * @Author Zjw
     * @Description create persistent zk node
     * @Date  2022/4/15 20:27
     * @param path
     **/
    public static void createPersistentNode(String path){
        createNode(path, CreateMode.PERSISTENT);
    }

    private static void createNode(String path, CreateMode mode){
        try {
            zkClient = getZkClient();
            path = buildPath(path);
            if(zkClient.checkExists().forPath(path) != null){
                log.info("this node had existed...[{}]", path);
                return;
            }
            zkClient.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
            log.info("create zk node success...[{}]", path);
        }catch (Exception e){
            log.error("create zk node failed ：" + e.getMessage());
        }
    }

    /**
     *
     * @Author Zjw
     * @Description remove zk node, if this node has children, this method will remove it's children node, then delete this node
     * @Date  2022/4/15 20:27
     * @param path
     **/
    public static void removeTargetNode(String path){
        try {
            zkClient = getZkClient();
            List<String> childrenPath = getChildren(buildPath(path));
            childrenPath.parallelStream().forEach(cp -> removeTargetNode(path + "/" + cp));
            zkClient.delete().forPath(path);
            log.info("remove zk node success...[{}]", path);
        }catch (Exception e){
            log.error("remove zk node fail...[{}]", e.getMessage(), e);
        }
    }

    /**
     *
     * @Author Zjw
     * @Description get the node collection that meets the conditions
     * @Date  2022/4/15 20:28
     * @param path
     * @return java.util.List<java.lang.String>
     **/
    @SneakyThrows
    public static List<String> getChildren(String path){
        path = buildPath(path);
        List<String> childrenList = null;
        try {
            zkClient = getZkClient();
            childrenList = zkClient.getChildren().forPath(path);
            registerWatcher(path);
        }catch (Exception e){
            log.error("获取某个节点的子节点失败：" + e.getMessage());
            throw e;
        }
        return childrenList;
    }


    private static String buildPath(String path){
        if(path.startsWith(ROOT_PATH)) return path;
        if(path.startsWith("/")) return ROOT_PATH + path;
        return ROOT_PATH + "/" + path;
    }


    /**
     *
     * @Author Zjw
     * @Description register watcher for node
     * @Date  2022/4/15 20:28
     * @param path
     **/
    public static void registerWatcher(String path) throws Exception {
        String finalPath = buildPath(path);
        zkClient = getZkClient();
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, finalPath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(finalPath);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }


}
