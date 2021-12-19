package kd.lzp.servicetools.util.decompiler;

import kd.bos.exception.KDBizException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象反编译器
 *
 * @author lzpeng
 * @since 2021-05-17 8:30
 */
public abstract class AbstractDecompiler implements Decompiler {

    /**
     * 缓存反编译器
     */
    private static final Map<String, Decompiler> DECOMPILER_MAP = new ConcurrentHashMap<>();

    /**
     * 构造函数
     */
    public AbstractDecompiler() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callClassName = stackTrace[3].getClassName();
        if (!"sun.reflect.NativeConstructorAccessorImpl".equals(callClassName)) {
            throw new KDBizException(String.format("不允许直接 new 对象, 请使用 %s.getInstance(\"%s\") 获取单例对象 !!!", new Object[]{AbstractDecompiler.class.getName(), getClass().getName()}));
        }
    }


    /**
     * 获得单例对象
     *
     * @param className 反编译器类名
     * @return 反编译器
     */
    public static Decompiler getInstance(String className) {
        return DECOMPILER_MAP.computeIfAbsent(className, key -> {
            try {
                return (Decompiler) Class.forName(key).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * 获得单例对象
     *
     * @param clazz 反编译器类
     * @return 反编译器
     */
    public static Decompiler getInstance(Class<? extends Decompiler> clazz) {
        return getInstance(clazz.getName());
    }
}