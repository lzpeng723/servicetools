package kd.lzp.servicetools.util.decompiler.jd.loader;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;


public class NopLoader implements Loader {

    @Override
    public byte[] load(String internalName) {
        return null;
    }


    @Override
    public boolean canLoad(String internalName) {
        return false;
    }

}