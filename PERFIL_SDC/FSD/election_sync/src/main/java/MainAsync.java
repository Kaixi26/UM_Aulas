import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainAsync {

    static int currentLeader;
    static boolean portSent = false;
    static final Object lock = new Object();
    static Set<Integer> unreceivedMessagesPorts = new HashSet<>();

    public static void sendPort(NettyMessagingService ms, ScheduledExecutorService es, String[] args){
        synchronized (lock){
            if(portSent) return;
            portSent = true;
        }
        for(int i = 1; i < args.length; i++) {
            final String port = args[i];
            ms.sendAsync(Address.from("localhost", Integer.parseInt(port)), "election", "boop".getBytes())
                    .thenRun(() -> {
                        System.out.println("Mensagem enviada para " + port);
                    });
        }
    }

    public static void handleElectionMessage(NettyMessagingService ms, ScheduledExecutorService es, String[] args){
        ms.registerHandler("election", (a,m)->{
            System.out.println("election message from " + a);
            synchronized (lock) {
                unreceivedMessagesPorts.remove(a.port());
                if(a.port() > currentLeader){
                    System.out.println("new leader " + a);
                    currentLeader = a.port();
                }
                if(unreceivedMessagesPorts.isEmpty())
                    System.out.println("Current leader is " + currentLeader);
            }
            sendPort(ms, es, args);
        }, es);
    }

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService es =
                Executors.newScheduledThreadPool(1);

        NettyMessagingService ms =
                new NettyMessagingService("name", Address.from(Integer.parseInt(args[0])), new MessagingConfig());

        currentLeader = Integer.parseInt(args[0]);
        for(int i=1; i < args.length; i++)
            unreceivedMessagesPorts.add(Integer.parseInt(args[i]));

        ms.start().join();

        handleElectionMessage(ms, es, args);

        System.out.println("ENTER TO CONTINUE.");
        System.in.read();
        if(!portSent)
            sendPort(ms, es, args);
    }

}
