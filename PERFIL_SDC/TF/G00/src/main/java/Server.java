import spullara.nio.channels.FutureServerSocketChannel;
import spullara.nio.channels.FutureSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class Server {
    private FutureServerSocketChannel server;
    private final Bank bank = new Bank();

    public Server() {
    }

    public Server start(int port) throws IOException {
        server = new FutureServerSocketChannel();
        server.bind(new InetSocketAddress(port));
        acceptClients();
        return this;
    }

    private void acceptClients(){
        server.accept().thenAcceptAsync(client -> {
            System.out.println("Accepted client.");
            handleClient(client, ByteBuffer.allocate(Integer.BYTES));
            acceptClients();
        });
    }

    private void handleClient(FutureSocketChannel client, ByteBuffer buf){
        client.read(buf).thenAcceptAsync(rd -> {
            try {
                if(rd <= 0) return;

                buf.flip();
                int value = buf.getInt();
                buf.flip();

                synchronized (bank){
                    if(value == 0) buf.putInt(bank.balance());
                    else buf.putInt(bank.movement(value) ? 0 : 1);
                }
                buf.flip();

                client.write(buf).thenAcceptAsync(wr -> {
                    buf.flip();
                    handleClient(client, buf);
                });
            } catch (Exception e){ e.printStackTrace(); }
        });
    }


}
