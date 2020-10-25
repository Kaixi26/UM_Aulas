import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageHandler {
    private final Lock writes = new ReentrantLock();
    private final Condition newMessages = writes.newCondition();
    private final HashMap<Integer, ByteBuffer> messages = new HashMap<>();
    private int nextId = 0;

    int currentId(){
        writes.lock();
        try {
            return nextId;
        } finally {
            writes.unlock();
        }
    }

    void addMessage(ByteBuffer message){
        writes.lock();
        try {
            messages.put(nextId++, message);
            newMessages.signalAll();
        } finally {
            writes.unlock();
        }
    }

    ByteBuffer awaitMessage(int lastMessageId) {
        writes.lock();
        try {
            while (lastMessageId >= nextId)
                try {
                    newMessages.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return messages.get(lastMessageId).duplicate();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            writes.unlock();
        }
        return null;
    }
}
