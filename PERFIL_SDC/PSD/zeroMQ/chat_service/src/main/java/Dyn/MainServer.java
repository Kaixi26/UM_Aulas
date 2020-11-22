package Dyn;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.*;

public class MainServer {

    static Random random = new Random();
    static String[] pubPorts;
    static String[] subPorts;
    static List<String> subServers = new ArrayList<>();

    public static void replyToClient(ZMQ.Socket rep){
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4);
        buffer.putInt(Integer.parseInt(pubPorts[random.nextInt(pubPorts.length)]));
        if (subServers.size() > 0)
            buffer.putInt(Integer.parseInt(subServers.get(random.nextInt(subServers.size()))));
        else
            buffer.putInt(-1);
        rep.send(buffer.array());
    }

    public static void replyToServer(ZMQ.Socket rep, int port){
        subServers.add(port + "");

        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 * subPorts.length);
        buffer.putInt(subPorts.length);
        for(String subPort : subPorts)
            buffer.putInt(Integer.parseInt(subPort));

        rep.send(buffer.array());
    }

    public static void main(String[] args) {
        if(args.length < 3){
            System.out.println("Invalid argument amount.");
            return;
        }
        int numSub = Integer.parseInt(args[1]);
        pubPorts = new String[numSub];
        subPorts = new String[numSub];
        for(int i=0; i < numSub; i++) {
            pubPorts[i] = args[i + 2];
            subPorts[i] = args[i + 2 + numSub];
            System.out.println("Adding server (" + args[i + 2] + "," + args[i + 2 + numSub] + ")");
        }

        ZContext context = new ZContext();

        System.out.println("Setting reply port as " + args[0] + ".");
        ZMQ.Socket receiver = context.createSocket(SocketType.REP);
        receiver.bind("tcp://*:" + args[0]);

        while (true){
            int code = ByteBuffer.wrap(receiver.recv()).getInt();
            System.out.println("recv : " + code);
            if(code == 0)
                replyToClient(receiver);
            else
                replyToServer(receiver, code);
        }

    }

}
