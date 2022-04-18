package com.zrpc.extension;

import com.zrpc.annotation.SPI;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author: Zjw
 * @Description: this class can be used to load target class by reading the contents of the specified file
 * @Create 2022-04-14 17:35
 * @Modifier:
 */
@Slf4j
public class ExtensionLoader<T>
{
    /**
     * target interface, must be annotated by SPI
     * */
    private final Class<T> type;
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADER_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE_MAP = new ConcurrentHashMap<>();
    private final Map<String, Holder<T>> extensionMap = new ConcurrentHashMap<>();
    private final Map<String, Class<T>> extensionClassMap = new ConcurrentHashMap<>();
    /**
     * target directory
     * */
    private static final String EXTENSION_DIRECTORY = "META-INF/extensions";

    private ExtensionLoader(Class<T> clazz) {
        this.type = clazz;
        SPI spi = type.getAnnotation(SPI.class);
        this.defaultExtensionInstanceName = spi.value();
    }
    private String defaultExtensionInstanceName = "";



    /**
     * @param clazz
     * @return com.zrpc.extension.ExtensionLoader<T>
     * @Author Zjw
     * @Description get corresponding extensionLoader of target clazz
     * @Date 2022/4/14 17:53
     **/
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        if (clazz == null) throw new IllegalArgumentException("the clazz of extensionLoader can't be null...");
        if (!clazz.isInterface()) throw new IllegalArgumentException("the clazz of extensionLoader must be interface" + clazz.getName());
        if (clazz.getAnnotation(SPI.class) == null)
            throw new IllegalArgumentException("the clazz of extensionLoader must have SPI annotation..." + clazz.getName());
        ExtensionLoader<?> extensionLoader = EXTENSION_LOADER_MAP.get(clazz);
        if (extensionLoader == null) {
            synchronized (ExtensionLoader.class) {
                extensionLoader = EXTENSION_LOADER_MAP.get(clazz);
                if (extensionLoader == null) {
                    extensionLoader = new ExtensionLoader<>(clazz);
                    EXTENSION_LOADER_MAP.put(clazz, new ExtensionLoader<>(clazz));
                }
            }
        }
        return (ExtensionLoader<T>) extensionLoader;
    }

    /**
     *
     * @Author Zjw
     * @Description get default instance
     * @Date  2022/4/15 9:18
     * @return T
     **/
    public T getDefaultExtension(){
        return getExtension(defaultExtensionInstanceName);
    }

    /**
     *
     * @Author Zjw
     * @Description get target extension instance by extensionName
     * @Date  2022/4/14 18:22
     * @param extensionName
     * @return T
     **/
    public T getExtension(String extensionName) {
        Holder<T> holder = extensionMap.get(extensionName);
        if (holder == null) {
            holder = new Holder<>();
            extensionMap.put(extensionName, holder);
        }
        T instance = holder.getValue();
        if (instance == null) {
            synchronized (ExtensionLoader.class) {
                instance = holder.getValue();
                if (instance == null) {
                    instance = createExtension(extensionName);
                    if(instance == null) throw new RuntimeException("create target extension instance error..." + extensionName);
                    holder.setValue(instance);
                }
            }
        }
        return instance;
    }

    /**
     * the method to create target extension instance
     * */
    private T createExtension(String extensionName) {
        try {
            Class<T> clazz = getExtensionClasses(extensionName);
            Object instance = EXTENSION_INSTANCE_MAP.get(clazz);
            if(clazz == null) throw new RuntimeException("no such target clazz in directory");
            if(instance == null){
                instance = clazz.newInstance();
                EXTENSION_INSTANCE_MAP.put(clazz, instance);
            }
            return (T) instance;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    private Class<T> getExtensionClasses(String extensionName) {
        //从缓存中获取所有已经加载过的实例对象
        Class<T> clazz = extensionClassMap.get(extensionName);
        if(clazz == null){
            loadDirectory();
        }
        return extensionClassMap.get(extensionName);
    }

    /**
     * load all extension resource from target directory
     * */
    private void loadDirectory() {
        String fileName = ExtensionLoader.EXTENSION_DIRECTORY + "/" + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(classLoader, resourceUrl);
                }
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * load resource from target directory, and add cache
     * */
    private void loadResource(ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // read every line
            while ((line = reader.readLine()) != null) {
                // get index of comment
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // string after # is comment so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // our SPI use key-value pair so both of them must not be empty
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClassMap.put(name, (Class<T>) clazz);
                        }
                    }
                    catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
