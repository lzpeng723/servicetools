package kd.lzp.servicetools.util;

import kd.lzp.servicetools.util.decompiler.AbstractDecompiler;
import kd.lzp.servicetools.util.decompiler.Decompiler;
import kd.lzp.servicetools.util.decompiler.jd.JdDecompiler;


/**
 * 反编译工具
 * 暂时无法反编译cglib代理的类,lambda表达式
 * 可以反编译普通类,内部类,匿名内部类
 *
 * @author lzpeng
 */
@SuppressWarnings("serial")
public final class DecompilerUtils {

    /**
     * 反编译器
     */
    private static final ThreadLocal<Decompiler> DECOMPILER_THREAD_LOCAL = ThreadLocal.withInitial(() -> AbstractDecompiler.getInstance(JdDecompiler.class));


    /**
     * 设置反编译器
     *
     * @param decompiler 反编译器类对象
     */
    public static void setDecompiler(Decompiler decompiler) {
        DECOMPILER_THREAD_LOCAL.set(decompiler);
    }


    /**
     * 设置反编译器
     *
     * @param decompilerClassName 反编译器类名
     */
    public static void setDecompilerClassName(String decompilerClassName) {
        DECOMPILER_THREAD_LOCAL.set(AbstractDecompiler.getInstance(decompilerClassName));
    }


    /**
     * 删除类反编译器
     */
    public static void removeDecompiler() {
        DECOMPILER_THREAD_LOCAL.remove();
    }

    /**
     * 反编译对象的类的源码
     *
     * @param obj 对象
     * @return 源码
     */
    public static String decompilerClass(Object obj) {
        return decompilerClass(obj.getClass());
    }

    /**
     * 反编译类的源码
     *
     * @param clazz 类
     * @return 源码
     */
    public static String decompilerClass(Class<?> clazz) {
        return decompilerClass(clazz.getName());
    }

    /**
     * 反编译类的源码
     *
     * @param className 类全名
     * @return 源码
     */
    public static String decompilerClass(String className) {
        Decompiler decompiler = DECOMPILER_THREAD_LOCAL.get();
        return decompiler.decompilerClass(className);
    }

}
