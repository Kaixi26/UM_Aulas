package Simple;

import Data.Data;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Server {

    final static public int recvPort = 12340;
    final static public int sendPort = 12341;

    public static void main(String[] args) throws Exception {
        ZContext context = new ZContext();

        ZMQ.Socket sender = context.createSocket(SocketType.PUB);
        sender.bind("tcp://*:" + sendPort);

        ZMQ.Socket receiver = context.createSocket(SocketType.SUB);
        receiver.bind("tcp://*:" + recvPort);
        receiver.subscribe("".getBytes());

        while (true) {
            byte[] msg = receiver.recv();
            Data data = Data.decode(msg);
            System.out.println("recv [" + data.roomString + "] " + data.message);
            sender.send(msg);
        }

    }
}
