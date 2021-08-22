package kd.lzp.servicetools.servicehelper;


import cn.hutool.core.io.ManifestUtil;
import kd.bos.instance.Instance;
import kd.lzp.servicetools.util.AttachmentUtils;
import kd.lzp.servicetools.util.ClassUtils;
import kd.lzp.servicetools.util.DecompilerUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * 类相关服务的实现
 *
 * @author lzpeng
 * @since 2020-12-17 8:30
 */
public class ClassServiceImpl implements ClassService {

    /**
     * 反编译类
     *
     * @param className 类路径
     * @return 类源码
     */
    @Override
    public Map<String, Object> decompilerClass(String className, String decompilerClassName) {
        if (decompilerClassName != null) {
            DecompilerUtils.setDecompilerClassName(decompilerClassName);
        }
        Map<String, Object> returnMap = getReturnMap();
        returnMap.put("sourceCode", DecompilerUtils.decompilerClass(className));
        DecompilerUtils.removeDecompiler();
        return returnMap;
    }

    /**
     * 获取类信息
     *
     * @param className 类路径
     * @return 类信息
     */
    @Override
    public Map<String, Object> getClassInfo(String className, String decompilerClassName) {
        if (decompilerClassName != null) {
            DecompilerUtils.setDecompilerClassName(decompilerClassName);
        }
        Map<String, Object> classInfoMap = ClassUtils.getClassInfo(className);
        Map<String, Object> returnMap = getReturnMap();
        returnMap.putAll(classInfoMap);
        DecompilerUtils.removeDecompiler();
        return returnMap;
    }

    /**
     * 下载类所在jar包
     *
     * @param className 类全路径
     * @return 下载类所在jar包的url
     */
    @Override
    public Map<String, Object> downloadJarFile(String className) {
        // 下载jar包
        Map<String, Object> returnMap = getReturnMap();
        URL locationUrl = ClassUtils.getLocationUrl(className);
        if (locationUrl != null) {
            String url = AttachmentUtils.download(null, locationUrl);
            returnMap.put("url", url);
        }
        return returnMap;
    }

    /**
     * 获取类所在jar包的Manifest文件信息
     *
     * @param className 类全路径
     * @return 类所在jar包的Manifest文件信息
     * @see ManifestUtil#getManifest(java.lang.Class)
     */
    @Override
    public Map<String, Object> getManifestInfo(String className) {
        Map<String, Object> returnMap = getReturnMap();
        try {
            // 读取Manifest文件
            URL locationUrl = ClassUtils.getLocationUrl(className);
            if (locationUrl != null) {
                returnMap.put("locationUrl", URLDecoder.decode(locationUrl.toString(), "UTF-8"));
                File file = new File(URLDecoder.decode(locationUrl.getFile(), "UTF-8"));
                if (file.exists()) {
                    if (file.isDirectory()) {
                        // 未在 jar 包内
                        File manifestFile = new File(file, JarFile.MANIFEST_NAME);
                        if (manifestFile.exists()) {
                            String manifestStr = Files.readAllLines(Paths.get(manifestFile.getPath())).stream().collect(Collectors.joining(System.lineSeparator()));
                            returnMap.put("manifestStr", manifestStr);
                        }
                    } else if (file.getName().endsWith(".jar")) {
                        // 在 jar 包内
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Manifest manifest = new JarFile(file).getManifest();
                        manifest.write(baos);
                        String manifestStr = baos.toString();
                        returnMap.put("manifestStr", manifestStr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 下载类的class文件
     *
     * @param className 类全路径
     * @return 下载类的class文件的url
     */
    @Override
    public Map<String, Object> downloadClassFile(String className) {
        // 下载class文件
        Map<String, Object> returnMap = getReturnMap();
        URL resourceUrl = ClassUtils.getResourceUrl(className);
        if (resourceUrl != null) {
            String url = AttachmentUtils.download(null, resourceUrl);
            returnMap.put("url", url);
        }
        return returnMap;
    }

    /**
     * 获得需要返回给页面的map
     *
     * @return
     */
    private Map<String, Object> getReturnMap() {
        Map<String, Object> returnMap = new HashMap<>(4);
        returnMap.put("APP_NAME", Instance.getAppName());
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            returnMap.put("HOST_ADDRESS", hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return returnMap;
    }

}
