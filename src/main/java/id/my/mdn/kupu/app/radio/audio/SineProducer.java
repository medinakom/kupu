/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.radio.audio;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class SineProducer {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private Mp3Encoder encoder;
    private ExecutorService executor;

    @Inject
    private AudioBuffer buffer;

    public AudioBuffer getBuffer() {
        return buffer;
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            System.out.println("SineProducer already started, skipping");
            return;
        }

        System.out.println("SineProducer started");

        executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            try {
                startEncoderAndProduce();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @PreDestroy
    public void stop() {
        System.out.println("Stopping SineProducer");

        started.set(false);

        if (executor != null) {
            executor.shutdownNow();
        }

        if (encoder != null) {
            encoder.stop();
        }
    }

    private void startEncoderAndProduce() throws Exception {

        encoder = new Mp3Encoder();

        SineWaveGenerator sine = new SineWaveGenerator();

        byte[] mp3 = new byte[8192];

        while (started.get()) {
            byte[] pcm = sine.nextPcmChunk(1152);
            encoder.writePcm(pcm);

            int read;
            while ((read = encoder.readMp3(mp3)) > 0) {
                buffer.write(Arrays.copyOf(mp3, read));
            }

            Thread.sleep(26);
        }
    }
}
