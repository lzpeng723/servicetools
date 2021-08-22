package kd.lzp.servicetools.util.decompiler.fernflower.saver;

import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.util.jar.Manifest;

public interface NoOpResultSaver extends IResultSaver {
    @Override
    default void saveFolder(String path) {
    }

    @Override
    default void copyFile(String source, String path, String entryName) {
    }
    @Override
    default void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
    }
    @Override
    default void createArchive(String path, String archiveName, Manifest manifest) {
    }
    @Override
    default void saveDirEntry(String path, String archiveName, String entryName) {
    }
    @Override
    default void copyEntry(String source, String path, String archiveName, String entry) {
    }
    @Override
    default void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
    }
    @Override
    default void closeArchive(String path, String archiveName) {
    }
}