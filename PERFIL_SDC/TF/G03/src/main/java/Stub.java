import Message.Reply;
import Message.Request;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Stub {
    private final SpreadConnection conn;
    private int lastSentId = 0;

    private Stub(int port) {
        conn = new SpreadConnection();
    }

    public static Stub start(int port, String name) throws SpreadException, UnknownHostException {
        Stub stub = new Stub(port);
        stub.conn.connect(InetAddress.getByName("localhost"), port, name, false, false);
        return stub;
    }

    public void stop() throws SpreadException {
        conn.disconnect();;
    }

    private Reply send(Request req) throws SpreadException, InterruptedIOException {
        SpreadMessage message = new SpreadMessage();
        message.setData(req.encode());
        message.setSafe();
        message.addGroup("bank");
        conn.multicast(message);
        Reply rep;
        do {
            rep = Reply.decode(conn.receive().getData());
        } while(rep.getId() < lastSentId);
        return rep;
    }

    public boolean movement(int value) throws SpreadException, InterruptedIOException {
        return send(Request.movement(++lastSentId, value)).isSuccess();
    }

    public int balance() throws SpreadException, InterruptedIOException {
        return send(Request.balance(++lastSentId)).getBalance();
    }


}
