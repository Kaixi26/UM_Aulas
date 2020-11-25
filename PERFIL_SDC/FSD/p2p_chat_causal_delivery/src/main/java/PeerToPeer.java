import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.defaultThreadFactory;

public class PeerToPeer {

    static int local_id;
    static int local_port;
    static int[] ports;
    static MessageHandler messageHandler;

    static void receiver(){
        messageHandler.recv().thenAcceptAsync(msg -> {
            System.out.println("Message delivered : " + new String(msg, StandardCharsets.UTF_8));
            receiver();
        });
    }

    public static void main(String[] args) throws Exception {
        local_id = Integer.parseInt(args[0]);
        local_port = Integer.parseInt(args[1]);
        ports = new int[args.length - 2];
        for(int i=2; i<args.length; i++)
            ports[i-2] = Integer.parseInt(args[i]);

        messageHandler = new MessageHandler(local_id, ports.length + 1);


        ScheduledExecutorService es =
                Executors.newScheduledThreadPool(1);

        NettyMessagingService ms =
                new NettyMessagingService("name", Address.from("localhost", local_port), new MessagingConfig());

        ms.start().join();

        ms.registerHandler("message", (a,m) -> {
            //System.out.println("new message from " + a + ":" + new String(m));
            messageHandler.addMessage(m);
            }, es);

        receiver();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (!input.matches("^:q ?.*")){
            input = reader.readLine();
            messageHandler.sendAsync(ms, ports, input.getBytes());
        }
    }

}
