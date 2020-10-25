import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientWritter extends Thread {
    private final SocketChannel client;
    private final MessageHandler messages;
    private int lastMessageId = 0;

    ClientWritter(SocketChannel client, MessageHandler messages){
        this.client = client;
        this.messages = messages;
        this.lastMessageId = messages.currentId();
    }

    public void run() {
        try {
            ByteBuffer buf = null;
            while ((buf = messages.awaitMessage(lastMessageId++)) != null) {
                //System.out.println("[Sent][" + client.toString() + "]:\t" + line);
                client.write(buf);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
