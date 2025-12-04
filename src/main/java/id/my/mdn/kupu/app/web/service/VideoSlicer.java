/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import jakarta.ejb.Stateless;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.job.FFmpegJob.State;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

/**
 *
 * @author aphasan
 */
@Stateless
public class VideoSlicer {

    public boolean extractAudio(Path videoFilePath, Path audioFilePath) throws Exception {

        String input = videoFilePath.toAbsolutePath().toString();

        if (!isInputExist(input) || !isFormatValid(input)) {
            return false;
        }

        String output = audioFilePath.toAbsolutePath().toString();

        FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(output)
                .setFormat("mp3") // or "aac", "wav", etc.
                .setAudioCodec("libmp3lame") // or "aac"
                .addExtraArgs("-fflags", "+genpts")
                .addExtraArgs("-use_wallclock_as_timestamps", "1")
                .addExtraArgs("-vn")
                .done();

        builder.setVerbosity(FFmpegBuilder.Verbosity.ERROR);

        CountDownLatch latch = new CountDownLatch(1);

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegJob job = executor.createJob(builder);

        job.run();

        while (job.getState() == FFmpegJob.State.RUNNING) {
            Thread.sleep(1000);
        }

        return job.getState().equals(State.FINISHED);

    }

    public boolean sliceVideo(Path videoFile, Path toDir) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
//            System.out.println("UNCAUGHT in thread " + t.getName() + ":");
            e.printStackTrace();
        });

        String input = videoFile.toAbsolutePath().toString();

//        System.out.println("SLIS PIDIO INPUT: " + input);

        if (!isInputExist(input) || !isFormatValid(input)) {
//            System.out.println("SLIS PIDIO INPUT/FORMAT INVALID");
            return false;
        }

        String output = Path.of(toDir.toAbsolutePath().toString(), "stream.m3u8").toString();

//        System.out.println("SLIS PIDIO OUTPUT: " + output);

//        return true;
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(output)
                .setFormat("hls")
                .setVideoCodec("libx264")
                .setAudioCodec("aac")
                .setVideoResolution(640, 360)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                // Common HLS settings
                .addExtraArgs("-hls_segment_type", "mpegts")
                .addExtraArgs("-hls_list_size", "0")
                .addExtraArgs("-hls_time", "2")
                // Timestamp fix
                .addExtraArgs("-fflags", "+genpts")
                .addExtraArgs("-use_wallclock_as_timestamps", "1")
                .addExtraArgs("-profile:v", "main")
                .addExtraArgs("-level:v", "3.1")
                .addExtraArgs("-preset", "veryfast")
                .addExtraArgs("-b:v", "500k")
                .addExtraArgs("-maxrate", "550k")
                .addExtraArgs("-bufsize", "800k")
                .addExtraArgs("-b:a", "64k")
                .addExtraArgs("-ar", "44100")
                .done();

        builder.setVerbosity(FFmpegBuilder.Verbosity.DEBUG);

        FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegJob job = executor.createJob(builder);

        job.run();

        while (job.getState() == FFmpegJob.State.RUNNING) {
            Thread.sleep(1000);
        }

        return job.getState().equals(State.FINISHED);
    }

    public boolean sliceAudio(Path audioFile, Path toDir) throws Exception {

        String input = audioFile.toAbsolutePath().toString();

        if (!isInputExist(input) || !isFormatValid(input)) {
            return false;
        }

        String output = Path.of(toDir.toAbsolutePath().toString(), "stream.m3u8").toString();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(output)
                .setFormat("hls")
                .setAudioCodec("libmp3lame")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                // Common HLS settings
                .addExtraArgs("-hls_segment_type", "mpegts")
                .addExtraArgs("-hls_list_size", "0")
                .addExtraArgs("-hls_time", "2")
                // Timestamp fix
                .addExtraArgs("-fflags", "+genpts")
                .addExtraArgs("-use_wallclock_as_timestamps", "1")
                .addExtraArgs("-b:a", "64k")
                .addExtraArgs("-ar", "44100")
                .done();

        builder.setVerbosity(FFmpegBuilder.Verbosity.ERROR);

        CountDownLatch latch = new CountDownLatch(1);

        FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegJob job = executor.createJob(builder);

        job.run();

        while (job.getState() == FFmpegJob.State.RUNNING) {
            Thread.sleep(1000);
        }

        return job.getState().equals(State.FINISHED);
    }

    private boolean isInputExist(String filePath) {
//        System.out.println("CEK INPUT " + filePath);
        boolean ret = true;
        File inputFile = new File(filePath);
        if (!inputFile.exists() || inputFile.length() == 0) {
            ret = false;
        }
//        System.out.println("CEK INPUT  " + filePath + " : " + ret);

        return ret;
    }

    private boolean isFormatValid(String filePath) throws Exception {
//        System.out.println("CEK FORMAT " + filePath);

        boolean valid = false;

//        System.out.println("CEK FORMAT " + filePath + " : check existence");
        File mediaFile = new File(filePath);
        if (mediaFile.exists() && mediaFile.isFile() && mediaFile.length() > 0) {
//            System.out.println("CEK FORMAT  " + filePath + " : file exist");
            FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
            Thread.currentThread().sleep(10);
//            System.out.println("CEK FFPROBE :" + ffprobe.toString());
            FFmpegProbeResult probeResult = ffprobe.probe(filePath);
            if (probeResult.hasError()) {
//                System.out.println("CEK FORMAT  " + filePath + " : ERROR -> probeResult.getError().string");
                throw new Exception(probeResult.getError().string);
            } else {
                String formatName = probeResult.getFormat().format_name;
                if (formatName != null && formatName.contains(formatName)) {
                    valid = true;
                }
            }
        } else {
//            System.out.println("CEK FORMAT  " + filePath + " : file invalid");
            throw new Exception("Invalid input format !");
        }

//        System.out.println("CEK FORMAT  " + filePath + " : " + valid);

        return valid;
    }

}
