import State.*;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientConnection {

    public static void read(AsynchronousSocketChannel client
            , State state
            , ByteBuffer buf) {
        client.read(buf, client, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer rd, AsynchronousSocketChannel client){
                if(rd == -1) {
                    state.clients.remove(client);
                    return;
                }
                //System.out.println("Read " + rd + " bytes: " +
                //        new String(Arrays.copyOfRange(buf.array(), 0, rd-1), StandardCharsets.UTF_8));
                state.messages.addMessage(ByteBuffer.wrap(Arrays.copyOfRange(buf.array(), 0, rd)));
                buf.clear();
                ClientConnection.handleWrites(state);
                ClientConnection.read(client, state, buf);
            }

            @Override
            public void failed(Throwable throwable, AsynchronousSocketChannel client) {

            }
        });
    }

    public static void handleWrites(State state){
        int id = state.messages.currentId();
        Client client;
        while((client = state.clients.handle(id)) != null) {
            write(client, state);
        }
    }

    public static void write(Client client, State state){
        ByteBuffer message = state.messages.getNextMessage(client.messageId);
        if(message == null){
            state.clients.handled(client);
            return;
        }
        client.socket.write(message, client, new CompletionHandler<Integer, Client>() {
            @Override
            public void completed(Integer wr, Client client) {
                //System.out.println("Wrote " + wr + " bytes");
                client.messageId++;
                write(client, state);
            }

            @Override
            public void failed(Throwable throwable, Client client) {

            }
        });
    }

}
