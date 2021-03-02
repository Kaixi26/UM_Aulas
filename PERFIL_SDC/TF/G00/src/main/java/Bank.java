public class Bank {

    private int moneys = 0;

    public Bank(){
    }

    int balance(){
        return moneys;
    }

    boolean movement(int diff){
        if(-diff > moneys) return false;
        moneys += diff;
        return true;
    }
}
