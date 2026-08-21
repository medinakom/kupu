/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.radio.audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Mp3Encoder {

    private final Process process;
    private final OutputStream pcmIn;
    private final InputStream mp3Out;

    public Mp3Encoder() throws IOException {
        process = new ProcessBuilder(
                "lame",
                "-r",
                "-s", "44.1",
                "--bitwidth", "16",
                "--signed",
                "--little-endian",
                "-m", "m",
                "-b", "128",
                "-",
                "-"
        ).start();

        // write sine PCM here for 5 seconds
        System.err.println("LAME started, pid = " + process.pid());

        // VERY IMPORTANT: drain stderr
        new Thread(() -> {
            try (InputStream err = process.getErrorStream()) {
                err.transferTo(OutputStream.nullOutputStream());
            } catch (IOException ignored) {
            }
        }, "lame-stderr").start();

        pcmIn = process.getOutputStream();
        mp3Out = process.getInputStream();
    }

    public void writePcm(byte[] pcm) throws IOException {
        pcmIn.write(pcm);
        pcmIn.flush();
    }

    public int readMp3(byte[] buffer) throws IOException {
        if (mp3Out.available() == 0) {
            return 0;
        }
        return mp3Out.read(buffer);
    }

    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }
}
