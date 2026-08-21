/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.dao.AudioSegmentFacade;
import id.my.mdn.kupu.app.web.dao.VideoSegmentFacade;
import id.my.mdn.kupu.app.web.model.AudioSegment;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.model.VideoSegment;
import id.my.mdn.kupu.app.web.util.FileSystem;
import id.my.mdn.kupu.core.base.util.Result;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * @author aphasan
 */
@ApplicationScoped
public class MediaService {

    public static final int VIDEO = 0;
    public static final int AUDIO = 1;

    // public static final int BEGIN_VIDEO_PROC = 1;
    // public static final int SAVE_VIDEO_PROC = 2;
    // public static final int SLICE_VIDEO_PROC = 3;
    // public static final int STORE_VIDEO_PROC = 4;
    // public static final int END_VIDEO_PROC = 5;
    // public static final int BEGIN_AUDIO_PROC = 6;
    // public static final int SAVE_AUDIO_PROC = 7;
    // public static final int SLICE_AUDIO_PROC = 8;
    // public static final int STORE_AUDIO_PROC = 9;
    // public static final int END_AUDIO_PROC = 10;
    // public static final int IDLE = 100;
    // public static final int AUDIO_FAILED = 101;
    // public static final int VIDEO_FAILED = 102;
    // public static final int FAILED = 103;
    // public static final int PROCESS_EXISTS = 104;
    @Inject
    private FileSystem fs;

    @Inject
    private VideoSlicer slicer;

    @Inject
    private VideoSegmentFacade videoSegmentFacade;

    @Inject
    private AudioSegmentFacade audioSegmentFacade;

    public Path extractAudio(VideoKajian videoKajian) throws Exception {

        Path srcVideoPath = getMediaPath(videoKajian, VIDEO);
        Path dstAudioPath = getMediaPath(videoKajian, AUDIO);

        boolean isSuccess = false;

        if (srcVideoPath != null && dstAudioPath != null) {
            // Extracting audio
            isSuccess = slicer.extractAudio(srcVideoPath, dstAudioPath);
        }

        return isSuccess ? dstAudioPath : null;
    }

    public int sliceMedia(VideoKajian videoKajian, int type) throws Exception {

        Path srcPath = getMediaPath(videoKajian, type);
        Path tmpSlicedPath = getTmpDir(videoKajian, type);

        boolean success = false;

        if (srcPath != null && tmpSlicedPath != null) {
            success = (type == VIDEO
                    ? slicer.sliceVideo(srcPath, tmpSlicedPath)
                    : slicer.sliceAudio(srcPath, tmpSlicedPath));
        }

        return success ? 0 : -1;
    }

    public void storeMediaChunks(VideoKajian videoKajian, int type) throws IOException {

        Path chunkedVideoDir = getTmpDir(videoKajian, type);
        Path storageDir = getStorageDir(videoKajian, type);

        if (type == VIDEO) {
            videoSegmentFacade.removeVideo(videoKajian);
        } else if (type == AUDIO) {
            audioSegmentFacade.removeAudio(videoKajian);
        }

        Result<IOException> storeChunkException = new Result<>(true, null);

        fs.walk(chunkedVideoDir, mediaPath -> {
            try {
                Path dest = storageDir.resolve(mediaPath.getFileName());
                Files.move(mediaPath, dest, StandardCopyOption.REPLACE_EXISTING);

                if (type == VIDEO) {
                    storeVideoChunk(videoKajian, mediaPath.getFileName().toString(), dest);
                } else if (type == AUDIO) {
                    storeAudioChunk(videoKajian, mediaPath.getFileName().toString(), dest);
                }
            } catch (IOException ex) {
                storeChunkException.success = false;
                storeChunkException.payload = ex;
            }
        });

        if (storeChunkException.isNotSuccess()) {
            throw storeChunkException.payload;
        }

        removeTmp(videoKajian, type);
    }

    private Result<String> storeVideoChunk(VideoKajian videoKajian,
            String filename, Path filePath) throws IOException {
        VideoSegment segment = new VideoSegment();
        segment.setFilePath(filePath.toAbsolutePath().toString());
        segment.setContent(null);

        segment.setName(filename);
        segment.setVideo(videoKajian);
        return videoSegmentFacade.create(segment);
    }

    private Result<String> storeAudioChunk(VideoKajian videoKajian,
            String filename, Path filePath) throws IOException {
        AudioSegment segment = new AudioSegment();
        segment.setFilePath(filePath.toAbsolutePath().toString());
        segment.setContent(null);

        segment.setName(filename);
        segment.setVideo(videoKajian);
        return audioSegmentFacade.create(segment);
    }

    public void clearVideo(VideoKajian videoKajian) throws IOException {
        removeTmp(videoKajian, VIDEO);
        videoSegmentFacade.removeVideo(videoKajian);
    }

    public void clearAudio(VideoKajian videoKajian) throws IOException {
        removeTmp(videoKajian, AUDIO);
        audioSegmentFacade.removeAudio(videoKajian);
    }

    public Path getMediaDir(VideoKajian videoKajian, int type) throws IOException {
        Path parent = type == VIDEO ? fs.getVideoDir() : fs.getAudioDir();
        Path path = fs.createDirectory(parent, videoKajian.getKajian().getId());
        return path;
    }

    public Path getMediaPath(VideoKajian videoKajian, int type) throws IOException {

        Path path = fs.createFile(
                fs.createDirectory(type == VIDEO ? fs.getVideoDir() : fs.getAudioDir(),
                        videoKajian.getKajian().getId()),
                videoKajian.getId() + (type == VIDEO ? ".mp4" : ".mp3"));
        return path;
    }

    private Path getTmpDir(VideoKajian videoKajian, int type) throws IOException {

        Path path = fs.createDirectory(type == VIDEO ? fs.getTmpVideoDir() : fs.getTmpAudioDir(),
                videoKajian.getKajian().getId(),
                videoKajian.getId());
        return path;
    }

    public Path getStorageDir(VideoKajian videoKajian, int type) throws IOException {
        Path path = fs.createDirectory(type == VIDEO ? fs.getVideoDir() : fs.getAudioDir(),
                videoKajian.getKajian().getId(),
                videoKajian.getId());
        return path;
    }

    private InputStream getAsStream(Path pathToFile) throws IOException {
        return fs.getInputStream(pathToFile);
    }

    private void removeTmp(VideoKajian videoKajian, int type) throws IOException {
        Path tmpDir = getTmpDir(videoKajian, type);

        Path parentDir = tmpDir.getParent();

        fs.deleteDir(tmpDir, false);
        fs.deleteDir(parentDir, true);
    }

    public void deleteMedia(VideoKajian videoKajian) throws IOException {
        Path videoPath = getMediaPath(videoKajian, VIDEO);
        Path audioPath = getMediaPath(videoKajian, AUDIO);

        fs.deleteFile(videoPath);
        fs.deleteFile(audioPath);

        Path videoStorageDir = getStorageDir(videoKajian, VIDEO);
        Path audioStorageDir = getStorageDir(videoKajian, AUDIO);

        fs.deleteDir(videoStorageDir, false);
        fs.deleteDir(audioStorageDir, false);
    }

    public static final class ProcessMessage {
        public static final int INFO = 0;
        public static final int WARNING = 1;
        public static final int ERROR = 2;

        public final int type;
        public final String message;

        public ProcessMessage(int type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    private final Set<String> videoProcesses = new HashSet<>();

    public boolean isProcessingVideo(VideoKajian vk) {
        return videoProcesses.contains(vk.getId());
    }

    public boolean registerVideoProcess(VideoKajian vk) {
        return isProcessingVideo(vk) || videoProcesses.add(vk.getId());
    }

    public void unregisterVideoProcess(VideoKajian vk) {
        videoProcesses.remove(vk.getId());
    }

    private final Set<String> audioProcesses = Collections.synchronizedSet(new HashSet<>());

    private final Map<String, Deque<ProcessMessage>> audioProcessMessage = Collections.synchronizedMap(new HashMap<>());

    public boolean isProcessingAudio(VideoKajian vk) {
        return audioProcesses.contains(vk.getId());
    }

    public void postAudioMessage(VideoKajian videoKajian, ProcessMessage message) {
        Deque<ProcessMessage> deque = audioProcessMessage.computeIfAbsent(videoKajian.getId(),
                k -> new ConcurrentLinkedDeque<>());
        deque.offer(message);
    }

    public ProcessMessage popAudioMessage(VideoKajian videoKajian) {
        Deque<ProcessMessage> deque = audioProcessMessage.computeIfAbsent(videoKajian.getId(),
                k -> new ConcurrentLinkedDeque<>());
        return deque != null ? deque.poll() : null;
    }

    public boolean registerAudioProcess(VideoKajian vk) {
        return isProcessingAudio(vk) || audioProcesses.add(vk.getId());
    }

    public void unregisterAudioProcess(VideoKajian vk) {
        Deque<ProcessMessage> deque = audioProcessMessage.get(vk.getId());
        long i = 0;
        while (i < 1000000) {
            i++;
        }
        audioProcesses.remove(vk.getId());
        audioProcessMessage.remove(vk.getId());
    }
}
