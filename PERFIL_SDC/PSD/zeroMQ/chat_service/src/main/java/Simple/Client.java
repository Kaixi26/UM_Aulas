package Simple;

import Data.Data;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {

    static String room = "main";

    static String getRoom(){
        synchronized (room){
            return room;
        }
    }

    static void changeRoom(ZMQ.Socket sock, String newRoom){
        synchronized (room){
            System.out.println("Changing room to " + newRoom);
            sock.unsubscribe(Data.roomCodeFrom(room));
            sock.subscribe(Data.roomCodeFrom(newRoom));
            room = newRoom;
        }
    }


    public static void main(String[] args) throws Exception {

        ZContext context = new ZContext();

        ZMQ.Socket receiver = context.createSocket(SocketType.SUB);
        receiver.connect("tcp://*:" + Server.sendPort);
        changeRoom(receiver, room);

        ZMQ.Socket sender = context.createSocket(SocketType.PUB);
        sender.connect("tcp://*:" + Server.recvPort);

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
                changeRoom(receiver, str.substring(6));
            } else {
                Data data = new Data(getRoom(), str);
                sender.send(data.encode());
            }
        }
    }
}
