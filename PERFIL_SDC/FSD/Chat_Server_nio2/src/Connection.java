import State.State;
import State.Client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Connection {

    public static void acceptNew(AsynchronousServerSocketChannel server, State state){
        server.accept(server, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
            @Override
            public void completed(AsynchronousSocketChannel client,
                                  AsynchronousServerSocketChannel server) {
                state.clients.add(new Client(client, state.messages.currentId()));
                System.out.println("Accepted [" + state.clients.connected() + " clients connected].");
                ClientConnection.read(client, state, ByteBuffer.allocate(1024));
                Connection.acceptNew(server, state);
            }

            @Override
            public void failed(Throwable throwable, AsynchronousServerSocketChannel asynchronousServerSocketChannel) {

            }
        });
    }
}
