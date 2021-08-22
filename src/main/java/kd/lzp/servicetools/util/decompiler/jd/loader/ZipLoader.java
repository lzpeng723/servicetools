package kd.lzp.servicetools.util.decompiler.jd.loader;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ZipLoader implements Loader {

    protected HashMap<String, byte[]> map = new HashMap<>();

    public ZipLoader(InputStream is) throws LoaderException {
        byte[] buffer = new byte[2048];

        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                if (!ze.isDirectory()) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int read = zis.read(buffer);

                    while (read > 0) {
                        out.write(buffer, 0, read);
                        read = zis.read(buffer);
                    }

                    this.map.put(ze.getName(), out.toByteArray());
                }

                ze = zis.getNextEntry();
            }

            zis.closeEntry();
        } catch (IOException e) {
            throw new LoaderException(e);
        }
    }

    public HashMap<String, byte[]> getMap() {
        return this.map;
    }


    @Override
    public byte[] load(String internalName) {
        return this.map.get(internalName + ".class");
    }


    @Override
    public boolean canLoad(String internalName) {
        return this.map.containsKey(internalName + ".class");
    }
}