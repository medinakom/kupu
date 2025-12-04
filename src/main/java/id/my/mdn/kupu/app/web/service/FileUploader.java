/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.service.MediaService.ProcessMessage;
import static id.my.mdn.kupu.app.web.service.MediaService.VIDEO;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author aphasan
 */
@Named(value = "fileUploader")
@Dependent
public class FileUploader implements Serializable {

    @Inject
    private MediaService mediaService;

    private boolean videoNoError = true;

    // File upload handler
    private void handleFileUpload(InputStream src, VideoKajian videoKajian) throws IOException {

        int BUFF_LEN = 1024;
        byte[] buffer = new byte[BUFF_LEN];
        int numBytesRead;

        Path path = mediaService.getMediaPath(videoKajian, VIDEO);

        try (OutputStream os = new FileOutputStream(path.toFile())) {

            while ((numBytesRead = src.read(buffer, 0, BUFF_LEN)) != -1) {
                os.write(buffer, 0, numBytesRead);
            }

        } catch (IOException ex) {
            System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }

    public void process(final VideoKajian videoKajian, Object videoSrc) {

        mediaService.registerVideoProcess(videoKajian);

        mediaService.registerAudioProcess(videoKajian);

        InputStream is = (InputStream) videoSrc;

        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch saveVideoLatch = new CountDownLatch(1);
        CountDownLatch endStoreVideoLatch = new CountDownLatch(1);
        CountDownLatch endStoreAudioLatch = new CountDownLatch(1);

        // Save original video
        executor.submit(() -> {

            try {
                handleFileUpload(is, videoKajian);
            } catch (IOException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                videoNoError = false;
            }
            saveVideoLatch.countDown();
        });

        // Slicing video
        executor.submit(() -> {
            try {
                saveVideoLatch.await();
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

            try {
                if (videoNoError) {
                    int success = mediaService.sliceMedia(videoKajian, MediaService.VIDEO);
                    if (success == 0) {
                        mediaService.storeMediaChunks(videoKajian, MediaService.VIDEO);
                    }
                }
            } catch (Exception ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                endStoreVideoLatch.countDown();
            }

        });

        // Wait for Video procedures finished
        executor.submit(() -> {
            try {
                endStoreVideoLatch.await();
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                mediaService.unregisterVideoProcess(videoKajian);
            }

        });

        // Extract audio and save the result
        executor.submit(() -> {
            try {
                saveVideoLatch.await();
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                endStoreAudioLatch.countDown();
            }

            if (videoNoError) {
                try {
                    Path extractAudio = mediaService.extractAudio(videoKajian);
                    if (extractAudio != null) {
                        int success = mediaService.sliceMedia(videoKajian, MediaService.AUDIO);
                        if (success == 0) {
                            mediaService.storeMediaChunks(videoKajian, MediaService.AUDIO);
                        } else {
                            mediaService.postAudioMessage(
                                    videoKajian,
                                    new ProcessMessage(
                                            ProcessMessage.ERROR,
                                            "Gagal memproses audio!"
                                    )
                            );
                        }
                    } else {
                        mediaService.postAudioMessage(
                                videoKajian,
                                new ProcessMessage(
                                        ProcessMessage.ERROR,
                                        "Gagal meng-ekstrak audio!"
                                )
                        );
                    }
                } catch (Exception ex) {
                    System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    mediaService.postAudioMessage(
                            videoKajian,
                            new ProcessMessage(
                                    ProcessMessage.ERROR,
                                    ex.getMessage()
                            )
                    );
                } finally {
                    endStoreAudioLatch.countDown();
                }
            }
        });

        // Wait for Audio procedures finished
        executor.submit(() -> {
            try {
                endStoreAudioLatch.await();
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                mediaService.unregisterAudioProcess(videoKajian);
            }
        });

        executor.shutdown();

    }

}
