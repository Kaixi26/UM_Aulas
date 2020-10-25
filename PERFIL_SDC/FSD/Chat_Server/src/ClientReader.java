import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader extends Thread {

    private final Socket client;
    private final MessageHandler messages;

    ClientReader(Socket client, MessageHandler messages){
        this.client = client;
        this.messages = messages;
    }

    public void run() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = null;
            while ((line = r.readLine()) != null) {
                //System.out.println("[Recieved][" + client.toString() + "]:\t" + line);
                messages.addMessage(line);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
