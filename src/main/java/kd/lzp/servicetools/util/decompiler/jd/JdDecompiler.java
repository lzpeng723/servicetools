package kd.lzp.servicetools.util.decompiler.jd;

import kd.lzp.servicetools.util.decompiler.AbstractDecompiler;
import kd.lzp.servicetools.util.decompiler.jd.loader.ClassPathLoader;
import kd.lzp.servicetools.util.decompiler.jd.printer.PlainTextPrinter;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;

/**
 * jd-gui 反编译器
 *
 * @author lzpeng
 * @since 2021-05-17 8:30
 */
public class JdDecompiler extends AbstractDecompiler {

    /**
     * 反编译类
     *
     * @param className 类名
     * @return 反编译后的代码
     */
    @Override
    public String decompilerClass(String className) {
        try {
            ClassPathLoader classPathLoader = new ClassPathLoader();
            PlainTextPrinter plainTextPrinter = new PlainTextPrinter();
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(classPathLoader, plainTextPrinter, className.replace('.', '/'));
            return plainTextPrinter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}