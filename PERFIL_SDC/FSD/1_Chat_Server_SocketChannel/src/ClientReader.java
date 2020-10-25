import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientReader extends Thread {

    private final int BUFFER_SIZE = 100;
    private final SocketChannel client;
    private final MessageHandler messages;

    ClientReader(SocketChannel client, MessageHandler messages){
        this.client = client;
        this.messages = messages;
    }

    public void run() {
        try {
            while (true) {
                ByteBuffer message = ByteBuffer.allocate(BUFFER_SIZE);
                client.read(message);
                message.flip();
//                String messageStr = new String(
//                        Arrays.copyOfRange(message.array(), 0, message.limit())
//                        , StandardCharsets.UTF_8);
//                System.out.println("[Recieved][" + client.toString() + "]:\t" + messageStr);
                messages.addMessage(message);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
