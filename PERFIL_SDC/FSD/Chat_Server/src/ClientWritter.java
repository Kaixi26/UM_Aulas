import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientWritter extends Thread {
    private final Socket client;
    private final MessageHandler messages;
    private int lastMessageId = 0;

    ClientWritter(Socket client, MessageHandler messages){
        this.client = client;
        this.messages = messages;
        this.lastMessageId = messages.currentId();
    }

    public void run() {
        try {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            String line = null;
            while ((line = messages.awaitMessage(lastMessageId++)) != null) {
                System.out.println("[Sent][" + client.toString() + "]:\t" + line);
                w.write(line + "\n");
                w.flush();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
