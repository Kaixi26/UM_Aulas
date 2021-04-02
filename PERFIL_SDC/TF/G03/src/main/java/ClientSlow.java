import spread.SpreadException;

import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Random;

public class ClientSlow {

    private static final Random random = new Random();

    public static void main(String[] args) throws Exception {
        Stub stub = Stub.start(4803, "client_slow");
        int localBalance = 0;

        for (int i=0; System.in.read() != 'q'; i++){
            int value = Math.abs(random.nextInt() % 1000);
            stub.movement(value);
            localBalance += value;
            System.out.println("Added " + value);
            System.out.println("Balance " + stub.balance());
        }
        stub.stop();
    }

    public static int getBalance(String name) throws SpreadException, UnknownHostException, InterruptedIOException {
        Stub stub = Stub.start(4803, name);
        return stub.balance();
    }
}
