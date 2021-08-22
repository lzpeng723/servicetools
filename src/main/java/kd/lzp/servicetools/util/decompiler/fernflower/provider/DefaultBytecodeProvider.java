package kd.lzp.servicetools.util.decompiler.fernflower.provider;

import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class DefaultBytecodeProvider implements IBytecodeProvider {

    private final Map<String, byte[]> map = new ConcurrentHashMap<>();


    @Override
    public byte[] getBytecode(String externalPath, String internalPath) {
        return this.map.computeIfAbsent(String.join("_", externalPath, internalPath), k -> {
            try {
                return getBytecode0(externalPath, internalPath);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }


    private byte[] getBytecode0(String externalPath, String internalPath) throws IOException {
        File file = new File(externalPath);
        if (internalPath == null) {
            if (!file.exists() && externalPath.contains(".jar!")) {

                if (externalPath.startsWith("/")) {
                    externalPath = externalPath.substring(1);
                }
                URL url = new URL("jar:file:/" + externalPath.replace(File.separatorChar, '/'));
                InputStream inputStream = url.openStream();
                int available = inputStream.available();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(available);
                InterpreterUtil.copyStream(inputStream, baos);
                return baos.toByteArray();
            }
            return InterpreterUtil.getBytes(file);
        }
        try (ZipFile archive = new ZipFile(file)) {
            ZipEntry entry = archive.getEntry(internalPath);
            if (entry == null) {
                throw new IOException("Entry not found: " + internalPath);
            }
            return InterpreterUtil.getBytes(archive, entry);
        }
    }
}