package kd.lzp.servicetools.util;

import cn.hutool.core.util.ClassLoaderUtil;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 类工具
 *
 * @author lzpeng
 */
public class ClassUtils {

    /**
     * 获取类信息
     *
     * @param className 类名
     * @return 类信息
     */
    public static Map<String, Object> getClassInfo(String className) {
        if (className != null && ClassLoaderUtil.isPresent(className)) {
            return getClassInfo(ClassLoaderUtil.loadClass(className, false));
        }
        // 没有找到此类
        return Collections.emptyMap();
    }

    /**
     * 获取类信息
     *
     * @param clazz 类
     * @return 类信息
     */
    public static Map<String, Object> getClassInfo(Class<?> clazz) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("className", clazz.getName());
        map.put("simpleName", clazz.getSimpleName());
        map.put("packageName", clazz.getPackage().getName());
        map.put("sourceCode", DecompilerUtils.decompilerClass(clazz));
        try {
            URL locationUrl = getLocationUrl(clazz);
            URL resourceUrl = getResourceUrl(clazz);
            map.put("locationUrl", URLDecoder.decode(locationUrl.toString(), "UTF-8"));
            map.put("resourceUrl", URLDecoder.decode(resourceUrl.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            map.put("classLoaderName", classLoader.getClass().getName());
            StringBuilder builder = new StringBuilder();
            while (classLoader != null) {
                builder
                        .insert(0, classLoader)
                        .insert(0, " -> ");
                classLoader = classLoader.getParent();
            }
            map.put("classLoaderTree", builder.toString());
        } else {
            map.put("classLoaderName", "BootStrapClassloader");
            map.put("classLoaderTree", "BootStrapClassloader");
        }
        return map;
    }

    /**
     * 获取类所在的classpath
     *
     * @param className 类全名
     * @return 类所在的classpath
     */
    public static URL getLocationUrl(String className) {
        if (className != null && ClassLoaderUtil.isPresent(className)) {
            return getLocationUrl(ClassLoaderUtil.loadClass(className, false));
        }
        return null;
    }

    /**
     * 获取类所在的classpath
     *
     * @param clazz 类
     * @return 类所在的classpath
     */
    public static URL getLocationUrl(Class<?> clazz) {
        String classPath = clazz.getName().replace('.', '/') + ".class";
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            if (codeSource != null) {
                return codeSource.getLocation();
            } else {
                URL resourceUrl = classLoader.getResource(classPath);
                return resourceUrlToLocationUrl(resourceUrl, classPath);
            }
        } else {
            URL resourceUrl = ClassLoader.getSystemResource(classPath);
            return resourceUrlToLocationUrl(resourceUrl, classPath);
        }
    }

    /**
     * 获取类所在位置
     *
     * @param className 类全名
     * @return 类所在位置
     */
    public static URL getResourceUrl(String className) {
        if (className != null && ClassLoaderUtil.isPresent(className)) {
            return getResourceUrl(ClassLoaderUtil.loadClass(className, false));
        }
        return null;
    }

    /**
     * 获取类所在位置
     *
     * @param clazz 类
     * @return 类所在位置
     */
    public static URL getResourceUrl(Class<?> clazz) {
        String classPath = clazz.getName().replace('.', '/') + ".class";
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            if (codeSource != null) {
                URL locationUrl = codeSource.getLocation();
                return locationUrlToResourceUrl(locationUrl, classPath);
            } else {
                return classLoader.getResource(classPath);
            }
        } else {
            return ClassLoader.getSystemResource(classPath);
        }
    }

    /**
     * locationUrl 转 resourceUrl
     *
     * @param locationUrl locationUrl
     * @param classPath   类路径
     * @return resourceUrl
     */
    public static URL locationUrlToResourceUrl(URL locationUrl, String classPath) {
        try {
            if ("jar".equals(locationUrl.getProtocol())) {
                return new URL(locationUrl + "!/" + classPath);
            } else if ("file".equals(locationUrl.getProtocol()) && locationUrl.getPath().endsWith(".jar")) {
                return new URL("jar:" + locationUrl + "!/" + classPath);
            } else {
                return new URL(locationUrl, classPath);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return locationUrl;
        }
    }

    /**
     * resourceUrl 转 locationUrl
     *
     * @param resourceUrl resourceUrl
     * @param classPath   类路径
     * @return locationUrl
     */
    public static URL resourceUrlToLocationUrl(URL resourceUrl, String classPath) {
        try {
            String protocol = resourceUrl.getProtocol();
            String urlStr = String.valueOf(resourceUrl);
            urlStr = urlStr.substring(0, urlStr.length() - classPath.length());
            if (urlStr.endsWith("!/")) {
                urlStr = urlStr.substring("jar".equals(protocol) ? "jar:".length() : 0, urlStr.length() - "!/".length());
            }
            return new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return resourceUrl;
        }
    }
}
