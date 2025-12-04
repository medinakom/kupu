/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author aphasan
 */
@Named(value = "videoUploader")
@Dependent
public class VideoUploader implements Serializable {

    @Inject
    private MediaService mediaService;

    private boolean noError = true;

    public void process(VideoKajian videoKajian, Object videoSrc) {

        mediaService.registerVideoProcess(videoKajian);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch endStoreVideoLatch = new CountDownLatch(1);

        // Slicing video
        executor.submit(() -> {

            try {
                mediaService.sliceMedia(videoKajian, MediaService.VIDEO);
                mediaService.storeMediaChunks(videoKajian, MediaService.VIDEO);
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

        executor.shutdown();

    }

    public void clearVideo(VideoKajian videoKajian) {
        mediaService.registerVideoProcess(videoKajian);

        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(() -> {
            try {
                mediaService.clearVideo(videoKajian);
            } catch (IOException ex) {
                System.getLogger(AudioUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                mediaService.unregisterVideoProcess(videoKajian);
            }
        });

        executor.shutdown();
    }

}
