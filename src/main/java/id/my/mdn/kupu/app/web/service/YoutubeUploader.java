/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import static id.my.mdn.kupu.app.web.service.MediaService.VIDEO;
import id.my.mdn.kupu.app.web.util.FileSystem;
import id.my.mdn.kupu.app.web.util.ytdlp.YoutubeService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.bramp.ffmpeg.FFprobe;
import org.apache.commons.configuration2.Configuration;

/**
 *
 * @author aphasan
 */
@Named(value = "youtubeUploader")
@Dependent
public class YoutubeUploader implements Serializable {

    @Inject
    private MediaService mediaService;
    
    @Inject
    private YoutubeService ytService;
    
    @Inject
    private FileSystem fs;

    @Inject
    private Configuration config;

    private volatile boolean videoNoError = true;

    private volatile boolean audioNoError = true;

    public void download(Path dir, String id, String videoUrl) throws Exception {

//        download(
//                "/usr/local/bin/yt-dlp",
//                "-v",
//                "--cookies", config.getString("web.ytCookies"),
//                "--no-part",
//                "-t", "mp4",
//                "--no-playlist",
//                "--max-downloads", "1",
//                "--force-overwrites",
//                "-P", dir.toString(),
//                "-o", id + ".mp4",
//                videoUrl
//        );
        
        ytService.downloadBestFormatToTemp(videoUrl, fs.createFile(dir, id + ".mp4"));
        
    }

//    private void download(String... command) throws Exception {
//
//        ProcessBuilder processBuilder = new ProcessBuilder()
//                .command(command);
//
//        processBuilder.redirectErrorStream(true);
//        Process process = processBuilder.start();
//        try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = r.readLine()) != null) {
//                System.out.println("YT-DLP: " + line);
//            }
//        }
//        int waitFor = process.waitFor();
////        System.out.println("WET FOR: " + waitFor);
//    }

    public void process(VideoKajian videoKajian, Object videoSrc) {

//        diag();
        mediaService.registerVideoProcess(videoKajian);

        mediaService.registerAudioProcess(videoKajian);

        String youtubeUrl = (String) videoSrc;

        ExecutorService executor = Executors.newFixedThreadPool(5);

        CountDownLatch saveVideoLatch = new CountDownLatch(1);
        CountDownLatch endStoreVideoLatch = new CountDownLatch(1);
        CountDownLatch endStoreAudioLatch = new CountDownLatch(1);

        // Save original video
        executor.submit(() -> {
            try {
                Path dir = mediaService.getMediaDir(videoKajian, VIDEO);
//                System.err.printf("YT DONLOT: %s", dir.toAbsolutePath().toString());

                download(dir, videoKajian.getId(), youtubeUrl);

            } catch (Exception ex) {
                System.getLogger(YoutubeUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                videoNoError = false;
            } finally {
                saveVideoLatch.countDown();
            }

        });

        // Slicing video
        executor.submit(() -> {
            try {
                saveVideoLatch.await();
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

            if (videoNoError) {
                try {
//                    System.out.println("YT PIDIO SLIS & STOR");
//                    System.out.println("YT PIDIO SLIS & STOR NO ELOR");
                    mediaService.sliceMedia(videoKajian, MediaService.VIDEO);
                    mediaService.storeMediaChunks(videoKajian, MediaService.VIDEO);
                } catch (Exception ex) {
                    videoNoError = false;
                    System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                } finally {
                    endStoreVideoLatch.countDown();
                }
            } else {
                endStoreVideoLatch.countDown();
            }

        });

        // Wait for Video procedures finished
        executor.submit(() -> {
            try {
                endStoreVideoLatch.await();
//                if (videoNoError) {
//                    System.out.println("VIDEO SAKSES");
//                } else {
//                    System.out.println("VIDEO PAIL");
//                }
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
//                System.out.println("YT AUDIO WET PIDIO SEP");
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

            if (videoNoError) {
                try {

//                    System.out.println("YT AUDIO PROCES STAT");
                    mediaService.extractAudio(videoKajian);
                    mediaService.sliceMedia(videoKajian, MediaService.AUDIO);
                    mediaService.storeMediaChunks(videoKajian, MediaService.AUDIO);
                } catch (Exception ex) {
                    audioNoError = false;
                    System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }

            endStoreAudioLatch.countDown();
        });

        // Wait for Audio procedures finished
        executor.submit(() -> {
            try {
                endStoreAudioLatch.await();
//                if (audioNoError) {
//                    System.out.println("AUDIO SAKSES");
//                } else {
//                    System.out.println("AUDIO PAIL");
//                }
            } catch (InterruptedException ex) {
                System.getLogger(FileUploader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } finally {
                mediaService.unregisterAudioProcess(videoKajian);
            }
        });

        executor.shutdown();

    }

    public static void diag() {
        System.out.println("=== ENV INFO ===");
        System.out.println("java.home=" + System.getProperty("java.home"));
        System.out.println("java.class.path=" + System.getProperty("java.class.path"));
        System.out.println("user.dir=" + System.getProperty("user.dir"));
        System.out.println("PATH=" + System.getenv("PATH"));
        System.out.println("LD_LIBRARY_PATH=" + System.getenv("LD_LIBRARY_PATH"));
        System.out.println("All env keys (sorted):");
        System.getenv().entrySet().stream().sorted(java.util.Map.Entry.comparingByKey())
                .forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
        System.out.println("================");

        String file = "/home/aphasan/tmp/video/test.mp4";
        System.out.println("TEST FILE: " + file);
        File f = new File(file);
        System.out.println("exists=" + f.exists() + " canRead=" + f.canRead() + " len=" + (f.exists() ? f.length() : -1));

        // 1) safeCreateFFprobe test (constructor with timeout)
        System.out.println("\n-- safeCreateFFprobe (5s) --");
        try {
            ExecutorService single = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "ffprobe-init");
                t.setDaemon(true);
                return t;
            });
            Future<FFprobe> fut = single.submit(() -> new FFprobe("/usr/bin/ffprobe"));
            long t0 = System.currentTimeMillis();
            FFprobe ff = fut.get(5, TimeUnit.SECONDS);
            long t1 = System.currentTimeMillis();
            single.shutdownNow();
            System.out.println("FFprobe constructor returned in " + (t1 - t0) + "ms -> " + ff);
        } catch (TimeoutException te) {
            System.err.println("FFprobe constructor timed out after 5s");
            te.printStackTrace(System.err);
        } catch (ExecutionException ee) {
            System.err.println("FFprobe constructor threw:");
            ee.getCause().printStackTrace(System.err);
        } catch (Throwable t) {
            System.err.println("FFprobe constructor failed:");
            t.printStackTrace(System.err);
        }

        // 2) wrapper probe call test with a timeout
        System.out.println("\n-- wrapper probe call (8s) --");
        try {
            FFprobe ff = new FFprobe("/usr/bin/ffprobe");
            ExecutorService ex = Executors.newSingleThreadExecutor(r -> new Thread(r, "ffprobe-probe"));
            Future<?> fut = ex.submit(() -> {
                try {
                    System.out.println("Calling ffprobe.probe() on thread " + Thread.currentThread().getName());
                    net.bramp.ffmpeg.probe.FFmpegProbeResult r1 = ff.probe(file);
                    System.out.println("probe returned: format=" + (r1.getFormat() == null ? null : r1.getFormat().format_name));
                } catch (Throwable t) {
                    System.err.println("probe threw in callable:");
                    t.printStackTrace(System.err);
                    throw new RuntimeException(t);
                }
            });
            long t0 = System.currentTimeMillis();
            fut.get(8, TimeUnit.SECONDS);
            long t1 = System.currentTimeMillis();
            ex.shutdownNow();
            System.out.println("wrapper probe completed in " + (t1 - t0) + "ms");
        } catch (TimeoutException te) {
            System.err.println("wrapper probe timed out after 8s");
            te.printStackTrace(System.err);
        } catch (ExecutionException ee) {
            System.err.println("wrapper probe execution threw:");
            ee.getCause().printStackTrace(System.err);
        } catch (Throwable t) {
            System.err.println("wrapper probe failed:");
            t.printStackTrace(System.err);
        }

        // 3) ffprobe binary via ProcessBuilder
        System.out.println("\n-- process ffprobe (8s) --");
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "/usr/bin/ffprobe", "-v", "error", "-print_format", "json", "-show_format", "-show_streams", file
            );
            pb.redirectErrorStream(true);
            if ("null".equals(pb.environment().get("LD_LIBRARY_PATH"))) {
                pb.environment().remove("LD_LIBRARY_PATH");
            }
            long t0 = System.currentTimeMillis();
            Process p = pb.start();

            ExecutorService re = Executors.newSingleThreadExecutor();
            Future<String> outF = re.submit(() -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String ln;
                    while ((ln = br.readLine()) != null) {
                        sb.append(ln).append('\n');
                    }
                    return sb.toString();
                }
            });

            boolean finished = p.waitFor(8, TimeUnit.SECONDS);
            long t1 = System.currentTimeMillis();
            String out = "";
            try {
                out = outF.get(2, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
            re.shutdownNow();

            System.out.println("process exit=" + p.exitValue() + " finished=" + finished + " time=" + (t1 - t0) + "ms");
            System.out.println("process output (first 400 chars):\n" + (out.length() > 400 ? out.substring(0, 400) + "..." : out));
        } catch (Throwable t) {
            System.err.println("process ffprobe failed:");
            t.printStackTrace(System.err);
        }

        System.out.println("\nDIAG END");
    }

}
