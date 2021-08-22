package kd.lzp.servicetools.util.decompiler.fernflower;

import kd.lzp.servicetools.util.ClassUtils;
import kd.lzp.servicetools.util.decompiler.AbstractDecompiler;
import kd.lzp.servicetools.util.decompiler.fernflower.provider.DefaultBytecodeProvider;
import kd.lzp.servicetools.util.decompiler.fernflower.saver.PlainTextResultSaver;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * fernflower 反编译器
 *
 * @author lzpeng
 * @since 2021-05-17 8:30
 */
public class FernflowerDecompiler extends AbstractDecompiler {

    /**
     * 反编译类
     *
     * @param className 类名
     * @return 反编译后的代码
     */
    @Override
    public String decompilerClass(String className) {
        try {
            DefaultBytecodeProvider defaultBytecodeProvider = new DefaultBytecodeProvider();
            PlainTextResultSaver saver = new PlainTextResultSaver();
            Map<String, Object> options = new HashMap<String, Object>(4);
            options.put("log", "ERROR");
            options.put("__dump_original_lines__", "1");
            options.put("rsy", "1");
            PrintStreamLogger printStreamLogger = new PrintStreamLogger(System.out);
            BaseDecompiler decompiler = new BaseDecompiler(defaultBytecodeProvider, saver, options, printStreamLogger);
            addSpace(decompiler, className);
            decompiler.decompileContext();
            return saver.getSource(className.replace('.', '/'));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void addSpace(BaseDecompiler decompiler, String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        URL locationUrl = ClassUtils.getLocationUrl(className);
        String filePath = locationUrl.getFile();
        filePath = URLDecoder.decode(filePath, Charset.defaultCharset().name());
        File file = new File(filePath);
        String path = file.getAbsolutePath();
        if (file.isDirectory()) {
            String packagePath = clazz.getPackage().getName().replace('.', '/');
            File parentFile = new File(file, packagePath);
            File[] files = parentFile.listFiles();
            for (File f : files) {

                if (f.getName().equals(clazz.getName().substring(packagePath.length() + 1) + ".class")) {
                    decompiler.addSpace(f, true);
                } else if (f.getName().startsWith(clazz.getSimpleName() + "$") && f.getName().endsWith(".class")) {
                    decompiler.addSpace(f, true);
                }
            }
        } else if (path.endsWith(".zip") || path.endsWith(".jar")) {

            ZipFile zipFile = path.endsWith(".jar") ? new JarFile(file) : new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                String zipEntryName = zipEntry.getName().replace('/', '.');
                if (zipEntryName.equals(clazz.getName() + ".class")) {
                    decompiler.addSpace(new File(path + "!/" + zipEntry.getName()), true);
                    continue;
                }
                if (zipEntryName.startsWith(clazz.getName() + "$") && zipEntryName.endsWith(".class"))
                    decompiler.addSpace(new File(path + "!/" + zipEntry.getName()), true);
            }
        }
    }
}