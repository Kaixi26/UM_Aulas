package Dyn;

import Data.Data;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Random;

public class Client {

    static ZContext context = new ZContext();
    static ZMQ.Socket receiver;
    static ZMQ.Socket sender;

    static String room = "main";

    static String getRoom(){
        synchronized (room){
            return room;
        }
    }

    static void changeRoom(String newRoom){
        synchronized (room){
            System.out.println("Changing room to " + newRoom);
            receiver.unsubscribe(Data.roomCodeFrom(room));
            receiver.subscribe(Data.roomCodeFrom(newRoom));
            room = newRoom;
        }
    }


    public static void setupServers(String port){

        ZMQ.Socket requester = context.createSocket(SocketType.REQ);
        requester.connect("tcp://*:" + port);
        System.out.println("Connecting to main server at port " + port + ".");

        requester.send(ByteBuffer.allocate(4).putInt(0).array());
        ByteBuffer buffer = ByteBuffer.wrap(requester.recv());
        requester.disconnect("tcp://*:" + port);

        int recvPort = buffer.getInt();
        int sendPort = buffer.getInt();

        System.out.println("Connecting to publisher server at port " + recvPort + ".");
        System.out.println("Connecting to subscriber server at port " + sendPort + ".");

        receiver = context.createSocket(SocketType.SUB);
        receiver.connect("tcp://*:" + recvPort);
        changeRoom(room);

        sender = context.createSocket(SocketType.PUB);
        sender.connect("tcp://*:" + sendPort);

    }

    public static void main(String[] args) throws Exception {

        System.out.println("PRESS ENTER TO ATTEMPT CONNECTION.");
        System.in.read();
        setupServers(args[0]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Data data = Data.decode(receiver.recv());
                        System.out.println("recv [" + data.roomString + "] " + data.message);
                    }
                } catch (Exception e){}
            }
        }).start();

        while (true){
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
            if(str.matches("^\\\\room ([a-zA-Z_0-9])+\n?")) {
                changeRoom(str.substring(6));
            } else {
                Data data = new Data(getRoom(), str);
                sender.send(data.encode());
            }
        }
    }
}
