package kd.lzp.servicetools.util.decompiler.jd.loader;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class DirectoryLoader implements Loader {

    protected File base;

    public DirectoryLoader(File base) {
        this.base = base;
    }


    @Override
    public byte[] load(String internalName) throws LoaderException {
        File file = newFile(internalName);

        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);

                while (read > 0) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }

                return out.toByteArray();
            } catch (Exception e) {
                throw new LoaderException(e);
            }
        }
        return null;
    }


    @Override
    public boolean canLoad(String internalName) {
        return newFile(internalName).exists();
    }


    protected File newFile(String internalName) {
        return new File(this.base, internalName.replace('/', File.separatorChar) + ".class");
    }
}


/* Location:              D:\Lzpeng723\Desktop\servicetools.jar!\kd\lzp\servicetool\\util\decompiler\jd\loader\DirectoryLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */