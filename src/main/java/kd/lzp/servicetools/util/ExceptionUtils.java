package kd.lzp.servicetools.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * 异常工具
 *
 * @author lzpeng
 */
public class ExceptionUtils {

    /**
     * 获得详细异常堆栈信息
     *
     * @param t
     * @return
     */
    public static String getDetailMessage(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        t.printStackTrace(writer);
        writer.flush();
        if (t instanceof InvocationTargetException) {
            // 是反射异常,得到真正的异常
            InvocationTargetException ite = (InvocationTargetException) t;
            return getDetailMessage(ite.getTargetException()) + System.lineSeparator() + sw;
        }
        return sw.toString();
    }

}
