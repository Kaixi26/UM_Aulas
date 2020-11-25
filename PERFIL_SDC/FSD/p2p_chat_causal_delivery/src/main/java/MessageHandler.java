import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageHandler {

    final Lock lock = new ReentrantLock();

    VectorClock local;
    Map<VectorClock, byte[]> on_hold = new HashMap<>();

    CompletableFuture<byte[]> current_handle = null;

    MessageHandler(int local_id, int size){
        long[] l = new long[size];
        this.local = new VectorClock(local_id, l);
    }

    private void accept(VectorClock clock){
        for(int i=0; i<local.t.length; i++)
            local.t[i] = Math.max(local.t[i], clock.t[i]);
    }

    private boolean acceptable(VectorClock clock){
        boolean ret = true;
        for(int i=0; i<local.t.length; i++)
            if(i == clock.local_id)
                ret = ret && local.t[i]+1 == clock.t[i];
            else
                ret = ret && local.t[i] >= clock.t[i];
        //System.out.println("Comparing local " + local + ", foreign " + clock + ", " + ret);
        return ret;
    }

    private void tryAccept(){
        if (current_handle == null) return;
        for (Map.Entry<VectorClock, byte[]> c : on_hold.entrySet())
            if (acceptable(c.getKey())) {
                accept(c.getKey());
                on_hold.remove(c.getKey());
                current_handle.complete(c.getValue());
                current_handle = null;
                return;
            }
    }

    void addMessage(byte[] msg){
        lock.lock();
        try {
            ByteBuffer buffer = ByteBuffer.wrap(msg);
            VectorClock clock = VectorClock.decode(buffer);
            byte[] rest = new byte[buffer.remaining()];
            buffer.get(rest);
            on_hold.put(clock, rest);
            tryAccept();
        } finally {
            lock.unlock();
        }
    }

    void sendAsync(NettyMessagingService ms, int[] ports, byte[] msg) {
        lock.lock();
        try {
            local.t[local.local_id]++;
            ByteBuffer buffer = ByteBuffer.allocate(local.BYTES + msg.length);
            buffer.put(local.encode());
            buffer.put(msg);
            for (int port : ports)
                ms.sendAsync(Address.from("localhost", port), "message", buffer.array());
        } finally {
            lock.unlock();
        }
    }

    CompletableFuture<byte[]> recv(){
        lock.lock();
        try {
            //System.out.println("recv triggered " + current_handle);
            if(current_handle != null) return null;
            current_handle = new CompletableFuture<>();
            tryAccept();
            return current_handle;
        } finally {
            lock.unlock();
        }
    }

}
