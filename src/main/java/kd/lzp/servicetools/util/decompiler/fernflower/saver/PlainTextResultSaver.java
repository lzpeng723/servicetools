package kd.lzp.servicetools.util.decompiler.fernflower.saver;

import java.util.HashMap;
import java.util.Map;


public class PlainTextResultSaver implements NoOpResultSaver {
    private final Map<String, String> map = new HashMap<>();


    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        this.map.put(qualifiedName, content);
    }

    public String getSource(String qualifiedName) {
        return this.map.get(qualifiedName);
    }

}