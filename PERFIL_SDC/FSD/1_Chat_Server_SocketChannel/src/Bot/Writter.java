package Bot;

import java.io.BufferedWriter;

public class Writter extends Thread {

    private final BufferedWriter out;
    private final String message;
    private final long delay;

    Writter(BufferedWriter out, String message, long delay){
        this.out = out;
        this.message = message;
        this.delay = delay;
    }

    public void run() {
        try {
            while(true){
                out.write(message);
                out.flush();
                Thread.sleep(delay);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
