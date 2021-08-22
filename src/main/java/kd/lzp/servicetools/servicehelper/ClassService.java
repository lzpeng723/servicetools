package kd.lzp.servicetools.servicehelper;


import java.util.Map;

/**
 * 类相关信息的接口
 *
 * @author lzpeng
 * @since 2020-12-17 8:22
 */
public interface ClassService {

    /**
     * 反编译类
     *
     * @param className 类路径
     * @return 类源码
     */
    default Map<String, Object> decompilerClass(String className) {
        return decompilerClass(className, null);
    }


    Map<String, Object> decompilerClass(String className, String decompilerClassName);


    /**
     * 获取类信息
     *
     * @param className 类路径
     * @return 类信息
     */
    Map<String, Object> getClassInfo(String className, String decompilerClassName);

    /**
     * 获取类信息
     *
     * @param className 类路径
     * @return 类信息
     */
    default Map<String, Object> getClassInfo(String className) {
        return getClassInfo(className, null);
    }

    /**
     * 下载类所在jar包
     *
     * @param className 类全路径
     * @return 下载类所在jar包的url
     */
    Map<String, Object> downloadJarFile(String className);

    /**
     * 获取类所在jar包的Manifest文件信息
     *
     * @param className 类全路径
     * @return 下类所在jar包的Manifest文件信息
     */
    Map<String, Object> getManifestInfo(String className);

    /**
     * 下载类的class文件
     *
     * @param className 类全路径
     * @return 下载类的class文件的url
     */
    Map<String, Object> downloadClassFile(String className);

}
