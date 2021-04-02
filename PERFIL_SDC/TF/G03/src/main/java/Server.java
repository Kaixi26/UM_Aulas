import Message.Reply;
import Message.Request;
import spread.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    private final SpreadConnection conn;
    private final SpreadGroup group;
    private final String name;
    private final Object sync = new Object();
    private final Queue<SpreadMessage> storedMessages = new ArrayDeque<>();

    private Bank bank = null;


    private Server(String name){
        this.conn = new SpreadConnection();
        this.group = new SpreadGroup();
        this.name = name;
    }

    private void log(String msg){
        System.out.println(this.name + ": " + msg);
    }

    private void applyMessage(SpreadMessage spreadMessage) throws Exception {
        Request req = Request.decode(spreadMessage.getData());
        Reply rep;
        switch (req.getType()){
            case BALANCE:
                rep = new Reply(req.getId(), this.bank.balance(),true);
                break;
            case MOVEMENT:
                rep = new Reply(req.getId(), 0, this.bank.movement(req.getDiff()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + req.getType());
        }
        SpreadMessage repMsg = new SpreadMessage();
        repMsg.setData(rep.encode());
        repMsg.setReliable();
        repMsg.addGroup(spreadMessage.getSender());
        this.conn.multicast(repMsg);
    }

    private void regularMessageHandler(SpreadMessage spreadMessage) throws Exception {
        synchronized (sync) {
            this.log("Regular message received, '" + new String(spreadMessage.getData()) + "' from " + spreadMessage.getSender() + ".");
            if (new String(spreadMessage.getData()).matches("R [0-9]+")){
                if(bank != null) return;
                bank = new Bank();
                bank.movement(Integer.parseInt(new String(spreadMessage.getData()).substring(2)));
                for (SpreadMessage storedMessage : storedMessages) {
                    applyMessage(storedMessage);
                }
                this.log("Updated bank.");
            } else if(bank == null){
                this.log(spreadMessage.getSender().toString());
                storedMessages.add(spreadMessage);
            } else {
                applyMessage(spreadMessage);
                this.log("Current balance: " + bank.balance());
            }
        }
    }

    private void membershipMessageHandler(SpreadMessage spreadMessage) throws SpreadException {
        synchronized(sync) {
            if (spreadMessage.getMembershipInfo().isCausedByJoin()) {
                this.log("Membership join message received, current members: " + Arrays.toString(spreadMessage.getMembershipInfo().getMembers()) + ".");
                if(spreadMessage.getMembershipInfo().getMembers().length <= 1){
                    this.log("First member to join, initializing bank.");
                    bank = new Bank();
                } else if(bank != null){
                    SpreadMessage repMsg = new SpreadMessage();
                    repMsg.setData(("R " + this.bank.balance()).getBytes(StandardCharsets.UTF_8));
                    repMsg.setReliable();
                    repMsg.addGroup(spreadMessage.getSender());
                    this.conn.multicast(repMsg);
                }
            } else if (spreadMessage.getMembershipInfo().isCausedByNetwork()){
                this.log("Membership message received: " + new String(spreadMessage.getData()) + ".");
            }
        }
    }


    public static Server start(String name, int port) throws UnknownHostException, SpreadException {
        final Server server = new Server(name);
        server.conn.connect(InetAddress.getByName("localhost"), port, name, false, true);
        server.conn.add(genListener(server));
        server.group.join(server.conn, "bank");
        return server;
    }

    void stop() throws SpreadException {
        group.leave();
        conn.disconnect();
    }

    private static AdvancedMessageListener genListener(Server server){
        return new AdvancedMessageListener() {
            @Override
            public void regularMessageReceived(SpreadMessage spreadMessage) {
                try {
                    server.regularMessageHandler(spreadMessage);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void membershipMessageReceived(SpreadMessage spreadMessage) {
                try {
                    server.membershipMessageHandler(spreadMessage);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }
}
