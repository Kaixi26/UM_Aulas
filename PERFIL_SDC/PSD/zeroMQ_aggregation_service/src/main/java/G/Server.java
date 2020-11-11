package G;

import F.Worker;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class Server {
    static public final String BOUNDED_ADDRESS = "tcp://*:12346";
    static final String BOUNDED_ADDRESS_WORKERS = "tcp://*:12347";
    static final String BOUNDED_ADDRESS_WORKERS_FIN = "tcp://*:12348";

    public static void main(String[] args) throws Exception {
        final ZContext context = new ZContext();
        ZMQ.Socket receiver = context.createSocket(SocketType.PULL);
        receiver.bind(BOUNDED_ADDRESS);
        ZMQ.Socket sender = context.createSocket(SocketType.PUSH);
        sender.connect(S.Server.BOUNDED_ADDRESS);
        ZMQ.Socket workerSender = context.createSocket(SocketType.PUSH);
        workerSender.bind(BOUNDED_ADDRESS_WORKERS);
        ZMQ.Socket workerReceiver = context.createSocket(SocketType.PULL);
        workerReceiver.bind(Server.BOUNDED_ADDRESS_WORKERS_FIN);

        new Thread(new Runnable() {
            public void run() {
                System.out.println("Reading from workers.");
                while (true) {
                    int fgx = ByteBuffer.wrap(workerReceiver.recv()).getInt();
                    System.out.println("W: " + fgx);
                    sender.send(ByteBuffer.allocate(4).putInt(fgx).array());
                }
            }
        }).start();
        while(true){
            byte[] msg = receiver.recv();
            System.out.println("F: " + ByteBuffer.wrap(msg).getInt());
            workerSender.send(msg);
        }
    }
}
