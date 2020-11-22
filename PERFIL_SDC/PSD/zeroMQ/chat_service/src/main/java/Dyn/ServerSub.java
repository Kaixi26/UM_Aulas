package Dyn;

import Data.Data;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class ServerSub {

    static ZContext context = new ZContext();
    static ZMQ.Socket receiver;
    static ZMQ.Socket sender;

    public static void setupServers(String[] args){

        ZMQ.Socket requester = context.createSocket(SocketType.REQ);
        requester.connect("tcp://*:" + args[0]);
        System.out.println("Connecting to main server at port " + args[0] + ".");

        requester.send(ByteBuffer.allocate(4).putInt(Integer.parseInt(args[1])).array());
        ByteBuffer buffer = ByteBuffer.wrap(requester.recv());
        requester.disconnect("tcp://*:" + args[0]);

        int numPorts = buffer.getInt();
        int[] sendPort = new int[numPorts];

        for (int i=0; i<numPorts; i++)
            sendPort[i] = buffer.getInt();

        receiver = context.createSocket(SocketType.SUB);
        receiver.bind("tcp://*:" + args[1]);
        receiver.subscribe("".getBytes());
        System.out.println("Receiving from clients at port " + args[1] + ".");

        sender = context.createSocket(SocketType.PUB);
        for (int port : sendPort) {
            sender.connect("tcp://*:" + port);
            System.out.println("Connecting to publisher server " + port + ".");
        }

    }

    public static void main(String[] args) throws Exception {
        if(args.length != 2){
            System.out.println("Invalid argument amount.");
            return;
        }

        setupServers(args);

        while (true) {
            byte[] msg = receiver.recv();
            Data data = Data.decode(msg);
            System.out.println("recv [" + data.roomString + "] " + data.message);
            sender.send(msg);
        }

    }
}
