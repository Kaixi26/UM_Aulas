import State.State;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.defaultThreadFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        State state = new State();
        AsynchronousChannelGroup g =
                AsynchronousChannelGroup.withFixedThreadPool(1, defaultThreadFactory());

        AsynchronousServerSocketChannel server =
                AsynchronousServerSocketChannel.open(g);
        server.bind(new InetSocketAddress(12345));

        Connection.acceptNew(server, state);
        g.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

    }
}
