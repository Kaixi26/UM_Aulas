import spread.SpreadException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Main {

    private static Map<String, Server> servers = new HashMap<>();

    public static void main(String[] _args) throws Exception {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while(!(line = stdin.readLine()).matches("q")){
            String[] args = line.split(" ");
            switch(args[0]){
                case "start":
                    System.out.println("starting server.");
                    servers.put(args[1], Server.start("bank_" + args[1], Integer.parseInt(args[2])));
                    break;
                case "kill":
                    System.out.println("killing server");
                    servers.remove(args[1]).stop();
                    break;
            }
        }
        //Server.start("bank1", 4804);
    }
}
