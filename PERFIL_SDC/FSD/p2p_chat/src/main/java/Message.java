import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Message {

    final int counter;
    final String message;

    Message(int localCounter, String message){
        this.counter = localCounter;
        this.message = message;
    }

    byte[] encode(){
        byte[] msg = message.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + msg.length);
        buffer.putInt(counter);
        buffer.putInt(msg.length);
        buffer.put(msg);
        return buffer.array();
    }

    static Message decode(byte[] msg){
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        int localCounter = buffer.getInt();
        int msgLen = buffer.getInt();
        byte[] tmp = new byte[msgLen];
        buffer.get(tmp);
        String message = new String(tmp, StandardCharsets.UTF_8);
        return new Message(localCounter, message);
    }

}
