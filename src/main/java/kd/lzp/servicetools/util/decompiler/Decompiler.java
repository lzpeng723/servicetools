package kd.lzp.servicetools.util.decompiler;

/**
 * 反编译器接口
 *
 * @author lzpeng
 * @since 2021-05-17 8:30
 */
public interface Decompiler {

    /**
     * 反编译类
     *
     * @param className 类名
     * @return 反编译源码
     */
    String decompilerClass(String className);


    /**
     * 反编译类
     *
     * @param clazz 类
     * @return 反编译源码
     */
    default String decompilerClass(Class clazz) {
        return decompilerClass(clazz.getName());
    }


    /**
     * 反编译类
     *
     * @param obj 对象
     * @return 反编译源码
     */
    default String decompilerClass(Object obj) {
        return decompilerClass(obj.getClass());
    }
}