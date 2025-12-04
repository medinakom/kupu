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
@Named(value = "audioUploader")
@Dependent
public class AudioUploader implements Serializable {

    @Inject
    private MediaService mediaService;

    public void process(VideoKajian videoKajian, Object videoSrc) {

        mediaService.registerAudioProcess(videoKajian);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch endStoreAudioLatch = new CountDownLatch(1);

        // Extract audio and save the result
        executor.submit(() -> {
            try {
                mediaService.extractAudio(videoKajian);
                mediaService.sliceMedia(videoKajian, MediaService.AUDIO);
                mediaService.storeMediaChunks(videoKajian, MediaService.AUDIO);
            } catch (Exception ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                endStoreAudioLatch.countDown();
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

    public void clearAudio(VideoKajian videoKajian) {
        
        mediaService.registerAudioProcess(videoKajian);

        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(() -> {
            try {
                mediaService.clearAudio(videoKajian);
            } catch (IOException ex) {
                System.getLogger(AudioUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                mediaService.unregisterAudioProcess(videoKajian);
            }
        });

        executor.shutdown();
    }

}
