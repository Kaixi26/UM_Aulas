package Scalable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ServerPub {

    public static void main(String[] args) throws Exception {
        if(args.length != 2){
            System.out.println("Invalid argument amount.");
            return;
        }
        String sendPort = args[0];
        System.out.println("Setting send port as " + sendPort + ".");
        String recvPort = args[1];
        System.out.println("Setting recv port as " + recvPort + ".");

        ZContext context = new ZContext();

        ZMQ.Socket sender = context.createSocket(SocketType.PUB);
        sender.bind("tcp://*:" + sendPort);

        ZMQ.Socket receiver = context.createSocket(SocketType.SUB);
        receiver.bind("tcp://*:" + recvPort);
        receiver.subscribe("".getBytes());

        while (true) {
            byte[] msg = receiver.recv();
            Data.Data data = Data.Data.decode(msg);
            System.out.println("recv [" + data.roomString + "] " + data.message);
            sender.send(msg);
        }

    }
}
