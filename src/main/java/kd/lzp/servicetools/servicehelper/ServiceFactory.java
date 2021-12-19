package kd.lzp.servicetools.servicehelper;

import kd.bos.dataentity.TypesContainer;
import kd.bos.exception.KDBizException;
import kd.bos.instance.Instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 二开微服务工厂
 * 服务接收的参数必须可序列化
 *
 * @author : Lzpeng
 * @link https://vip.kingdee.com/article/82065157714706944
 */
public class ServiceFactory {

    /**
     * Service Map
     */
    private static Map<String, String> serviceMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    static {
        serviceMap.put(ClassService.class.getSimpleName(), ClassServiceImpl.class.getName());
    }

    /**
     * 获取 Service
     *
     * @param clazz Service接口类
     * @param <T>   Service接口类型
     * @return Service
     */
    public static <T> T getService(Class<T> clazz) {
        return getService(clazz.getSimpleName());
    }

    /**
     * 添加 Service
     *
     * @param serviceName Service名称
     * @param serviceImpl Service实现类
     */
    public static void putService(String serviceName, String serviceImpl) {
        serviceMap.put(serviceName, serviceImpl);
    }

    /**
     * 获取 Service
     *
     * @param serviceName Service名称
     * @param <T>         Service接口类型
     * @return Service
     */
    public static <T> T getService(String serviceName) {
        String className = serviceMap.get(serviceName);
        if (className == null) {
            String appName = Instance.getAppName();
            throw new KDBizException(String.format("%s对应的服务实现在%s未找到", serviceName, appName));
        } else {
            return (T) TypesContainer.getOrRegisterSingletonInstance(className);
        }
    }

}
