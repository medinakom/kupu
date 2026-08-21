package id.my.mdn.kupu.app.radio.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SineWaveGenerator {

    private static final int SAMPLE_RATE = 44100;
    private static final double FREQUENCY = 440.0; // A4
    private double phase = 0;

    public byte[] nextPcmChunk(int samples) {
        ByteBuffer pcm = ByteBuffer.allocate(samples * 2);
        pcm.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < pcm.capacity() / 2; i++) {
            double v = Math.sin(phase);
            short s = (short) (v * 32767); // <-- THIS IS CRITICAL
            pcm.putShort(s);
            phase += 2 * Math.PI * FREQUENCY / SAMPLE_RATE;
        }
        return pcm.array();
    }
}
