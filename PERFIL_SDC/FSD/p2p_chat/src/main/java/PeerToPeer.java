import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PeerToPeer {

    static int counter = 0;
    static final Object o = new Object();

    public static int getCounter(){
        synchronized (o){
            return counter;
        }
    }

    public static void updateCounter(){
        synchronized (o){
            counter++;
            System.out.println("[Updated Counter] " + counter);
        }
    }

    public static void updateCounter(int recvCounter){
        synchronized (o){
            counter = Integer.max(counter, recvCounter) + 1;
            System.out.println("[Updated Counter] " + counter);
        }
    }


    public static void main(String[] args) throws Exception {
        ScheduledExecutorService es =
                Executors.newScheduledThreadPool(1);

        NettyMessagingService ms =
                new NettyMessagingService("name", Address.from(Integer.parseInt(args[0])), new MessagingConfig());

        ms.start().join();

        ms.registerHandler("message", (a,m) -> {
            Message message = Message.decode(m);
            System.out.println("new message from " + a + ":" + message.message);
            updateCounter(message.counter);
            }, es);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (!input.matches("^:q ?.*")){
            input = reader.readLine();
            for(int i=1; i<args.length; i++) {
                final int port = Integer.parseInt(args[i]);
                Message message = new Message(getCounter(), input);
                ms.sendAsync(Address.from("localhost", port), "message", message.encode());
                updateCounter();
            }

        }
    }

}
