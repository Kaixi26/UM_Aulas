import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(12345));
        MessageHandler messages = new MessageHandler();
        int connectedAmount = 0;
        while(true){
            SocketChannel client = ss.accept();
            new ClientReader(client, messages).start();
            new ClientWritter(client, messages).start();
            System.out.println("Clients connected: " + connectedAmount++);
        }
    }
}
