import java.io.Serializable;

public class Bank implements Serializable {

    private int moneys = 0;
    final Object sync = new Object();

    public Bank(){
    }

    int balance(){
        synchronized (sync) {
            return moneys;
        }
    }

    boolean movement(int diff){
        synchronized (sync) {
            if (-diff > moneys) return false;
            moneys += diff;
            return true;
        }
    }
}
