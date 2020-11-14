import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainSync {

    static int leader;
    static boolean portSent = false;
    static boolean timeoutSet = false;
    static final Object lock = new Object();
    static final long timeoutMs = 1000;

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

    public static void timeoutVoting(ScheduledExecutorService es){
        synchronized (lock){
            if(timeoutSet) return;
            timeoutSet = true;
        }
        es.schedule(()-> {
            synchronized (lock) {
                System.out.println("Election timed out, current leader is " + leader);
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
    }

    public static void handleElectionMessage(NettyMessagingService ms, ScheduledExecutorService es, String[] args){
        ms.registerHandler("election", (a,m)->{
            System.out.println("election message from " + a);
            synchronized (lock) {
                if(a.port() > leader){
                    System.out.println("new leader " + a);
                    leader = a.port();
                }
            }
            sendPort(ms, es, args);
            timeoutVoting(es);
        }, es);
    }

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService es =
                Executors.newScheduledThreadPool(1);

        NettyMessagingService ms =
                new NettyMessagingService("name", Address.from(Integer.parseInt(args[0])), new MessagingConfig());

        leader = Integer.parseInt(args[0]);

        ms.start().join();

        handleElectionMessage(ms, es, args);

        System.out.println("ENTER TO CONTINUE.");
        System.in.read();
        if(!portSent) {
            sendPort(ms, es, args);
            timeoutVoting(es);
        }
    }

}
