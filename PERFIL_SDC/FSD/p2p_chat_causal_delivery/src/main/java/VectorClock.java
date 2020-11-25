import java.nio.ByteBuffer;
import java.util.Arrays;

public class VectorClock {

    final int BYTES;

    final int local_id;
    long[] t;

    VectorClock(int local_id, long[] t){
        this.local_id = local_id;
        this.t = t;
        BYTES = t.length * Integer.BYTES * 2 + Long.BYTES * t.length;
    }

    byte[] encode(){
        ByteBuffer buffer = ByteBuffer.allocate(BYTES);
        buffer.putInt(local_id);
        buffer.putInt(t.length);
        for (long l : t)
            buffer.putLong(l);
        return buffer.array();
    }

    static VectorClock decode(ByteBuffer buffer){
        int local_id = buffer.getInt();
        int size = buffer.getInt();
        long[] t = new long[size];
        for(int i=0; i<size; i++)
            t[i] = buffer.getLong();
        return new VectorClock(local_id, t);
    }

    @Override
    public String toString() {
        return local_id + "@" + Arrays.toString(t);
    }
}
