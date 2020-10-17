import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        MessageHandler messages = new MessageHandler();
        while(true){
            Socket client = ss.accept();
            new ClientReader(client, messages).start();
            new ClientWritter(client, messages).start();
        }
    }
}
