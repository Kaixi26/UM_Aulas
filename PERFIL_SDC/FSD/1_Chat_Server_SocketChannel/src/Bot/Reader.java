package Bot;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Reader extends Thread {

    private final BufferedReader in;
    private final long delay;

    Reader(BufferedReader in, long delay){
        this.in = in;
        this.delay = delay;
    }

    public void run() {
        try {
            while(true){
                in.readLine();
                Thread.sleep(delay);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
