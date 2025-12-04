/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration2.Configuration;

/**
 *
 * @author aphasan
 */
@Named(value = "fs")
@ApplicationScoped
public class FileSystem {

    @Inject
    private Configuration config;

    public Path getAudioDir() {
        Path dir = null;
        try {
            dir = createDirectory(Path.of(config.getString("web.audioDir")));
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dir;
    }

    public Path getVideoDir() {
        Path dir = null;
        try {
            dir = createDirectory(Path.of(config.getString("web.videoDir")));
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dir;
    }

    public Path getTmpAudioDir() {
        Path dir = null;
        try {
            dir = createDirectory(Path.of(config.getString("web.tmpAudio")));
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dir;
    }

    public Path getTmpVideoDir() {
        Path dir = null;
        try {
            dir = createDirectory(Path.of(config.getString("web.tmpVideo")));
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dir;
    }

    public Path getAutoloadDir() {
        Path dir = null;
        try {
            dir = createDirectory(Path.of(config.getString("web.autoloadDir")));
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dir;
    }

    public Path createDirectory(Path parent, String... dirs) throws IOException {
        Path dirPath = Path.of(parent.toString(), dirs);
        return Files.exists(dirPath) ? dirPath : Files.createDirectories(dirPath);
    }

    public Path createFile(Path parent, String... paths) throws IOException {
        Path filePath = Path.of(parent.toString(), paths);
        return Files.exists(filePath) ? filePath : Files.createFile(filePath);

    }

    public boolean deleteFile(Path fileToDelete) throws IOException {
        return Files.deleteIfExists(fileToDelete);
    }

    public boolean deleteDir(Path dirToDelete, boolean cancelIfNotEmpty) throws IOException {
        if (Files.exists(dirToDelete)) {
            if (!cancelIfNotEmpty) {
                Files.walkFileTree(dirToDelete, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path t, BasicFileAttributes bfa) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path t, BasicFileAttributes bfa) throws IOException {
                        Files.delete(t);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path t, IOException ioe) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path t, IOException ioe) throws IOException {
                        Files.delete(t);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                try {
                    Files.delete(dirToDelete);
                } catch (DirectoryNotEmptyException ex) {
                    return false;
                }
            }
        }
        return true;
    }

    @FunctionalInterface
    public static interface ContentProcessor {

        public void process(Path path);
    }

    public InputStream getInputStream(Path filePath) throws IOException {
        return Files.newInputStream(filePath);
    }

    public void walk(Path dirPath, ContentProcessor processor) throws IOException {
        Files.newDirectoryStream(dirPath).forEach(processor::process);
    }

}
