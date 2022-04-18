package com.zrpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zrpc.config.enums.RpcConfigEnum;
import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLParser;
import com.zrpc.remoting.constants.consts.RpcConstants;
import com.zrpc.utils.PropertiesFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-18 9:58
 * @Modifier:
 */
@Slf4j
public class NacosRegister extends  AbstractRegister {

    private  final NamingService client;
    private static final String DEFAULT_NACOS_ADDRESS = "127.0.0.1:8848";


    public NacosRegister(){
        NamingService namingService = null;
        try {
            Properties properties = PropertiesFileUtils.readPropertiesFile(RpcConfigEnum.PROPERTIES_FILE_PATH.getValue());
            String nacosAddress = null;
            if(properties != null){
                nacosAddress = properties.getProperty(RpcConfigEnum.NACOS_ADDRESS.getValue());
            }
            if(nacosAddress == null) nacosAddress = DEFAULT_NACOS_ADDRESS;
            namingService = NamingFactory.createNamingService(nacosAddress);
        }catch (Exception e){
            log.error("get nacos client error...[{}]", e.getMessage());
            e.printStackTrace();
        }
        client = namingService;
    }

    @Override
    public void doRegisterService(URL url) {
        String serviceName = getServiceNameFromUrl(url);
        try {
            Instance instance = new Instance();
            instance.setServiceName(serviceName);
            instance.setIp(url.getHost());
            instance.setPort(url.getPort());
            instance.addMetadata(serviceName, url.toFullString());
            client.registerInstance(serviceName, instance);
        }catch (Exception e){
            log.error("nacos register service fail...[{}]", serviceName);
            e.printStackTrace();
        }
    }

    @Override
    public List<URL> doLookupServices(URL condition) {
        List<URL> urls = null;
        try {
            String serviceName = getServiceNameFromUrl(condition);
            List<Instance> instanceList = client.getAllInstances(serviceName);
            urls = instanceList.stream().map(instance -> {
                Map<String, String> metadata = instance.getMetadata();
                String urlStr = metadata.get(serviceName);
                return URLParser.toURL(urlStr);
            }).collect(Collectors.toList());
        } catch (NacosException e) {
            log.error("no such service...[{}]", e.getErrMsg());
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    public void doRemoveService(URL url) {
        String serviceName = getServiceNameFromUrl(url);
        try {
            client.deregisterInstance(serviceName, url.getHost(), url.getPort());
        }catch (Exception e){
            log.error("remove service fail...[{}]", url.toFullString());
            e.printStackTrace();
        }
    }

}
