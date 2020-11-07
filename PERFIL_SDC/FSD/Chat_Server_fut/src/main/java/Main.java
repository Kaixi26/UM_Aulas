import State.State;
import spullara.nio.channels.FutureServerSocketChannel;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;

import static java.util.concurrent.Executors.defaultThreadFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        State state = new State();
        AsynchronousChannelGroup g =
                AsynchronousChannelGroup.withFixedThreadPool(1, defaultThreadFactory());

        FutureServerSocketChannel server =
                new FutureServerSocketChannel();
        server.bind(new InetSocketAddress(12345));

        Connection.acceptNew(server, state);

    }
}
