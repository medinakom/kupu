package id.my.mdn.kupu.app.radio.audio;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class AudioBuffer {

    private static final int CAPACITY = 1024; // frames (~4–5 seconds)

    private final byte[][] buffer = new byte[CAPACITY][];
    private final AtomicLong writeSeq = new AtomicLong(0);

    /** Producer writes */
    public void write(byte[] frame) {
        long seq = writeSeq.getAndIncrement();
        buffer[(int) (seq % CAPACITY)] = frame;
    }

    /** Consumer snapshot */
    public Reader createReader() {
        return new Reader(writeSeq.get());
    }

    // ============================
    // Reader (per client)
    // ============================
    public class Reader {

        private long readSeq;

        private Reader(long startSeq) {
            this.readSeq = startSeq;
        }

        public byte[] read() {

            long latest = writeSeq.get();

            // too far behind → jump forward
            if (latest - readSeq > CAPACITY) {
                readSeq = latest - CAPACITY;
            }

            if (readSeq >= latest) {
                return null; // nothing new yet
            }

            byte[] frame = buffer[(int) (readSeq % CAPACITY)];
            readSeq++;

            return frame;
        }
    }
}
