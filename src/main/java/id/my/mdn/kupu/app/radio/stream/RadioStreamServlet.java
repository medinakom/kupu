package id.my.mdn.kupu.app.radio.stream;

import id.my.mdn.kupu.app.radio.audio.AudioBuffer;
import id.my.mdn.kupu.app.radio.broadcast.BroadcastEngine;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(urlPatterns = "/radio.mp3", asyncSupported = false)
public class RadioStreamServlet extends HttpServlet {

    @Inject
    AudioBuffer buffer;

    @Inject
    BroadcastEngine engine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("audio/mpeg");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        AudioBuffer.Reader reader = buffer.createReader();

        OutputStream out = resp.getOutputStream();

        try {
            // Resilience: Wait up to 3 seconds for engine to start if it hasn't yet
            long startWait = System.currentTimeMillis();
            while (!engine.isRunning() && (System.currentTimeMillis() - startWait) < 3000) {
                Thread.sleep(100);
            }

            while (engine.isRunning()) {
                byte[] frame = reader.read();

                if (frame != null) {
                    out.write(frame);
                    out.flush();
                } else {
                    Thread.sleep(10); // fallback pacing
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected from /radio.mp3");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
