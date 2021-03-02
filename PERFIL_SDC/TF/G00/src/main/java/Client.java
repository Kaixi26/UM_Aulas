import java.io.IOException;
import java.util.Random;

public class Client {

    private static final int movementAmount = 100;
    private static int localBalance = 0;
    private static Random random = new Random(0);

    public static void main(String[] args) throws IOException {
        ServerStub stub = new ServerStub();
        stub.bind(10000);

        for(int i=0; i<movementAmount; i++){
            int value = random.nextInt() % 1000;
            stub.movement(value);
            localBalance = updateBalance(localBalance, value);
        }
        System.out.println("Bank balance " + stub.balance());
        System.out.println("Local balance " + localBalance);
    }

    private static int updateBalance(int balance, int diff){
        if(-diff > balance) return balance;
        return balance + diff;
    }
}
