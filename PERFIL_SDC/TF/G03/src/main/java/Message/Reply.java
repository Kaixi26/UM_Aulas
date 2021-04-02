package Message;

import java.nio.charset.StandardCharsets;

public class Reply {
    final int balance;
    final boolean success;
    final int id;

    public Reply(int id, int balance, boolean success){
        this.id = id;
        this.balance = balance;
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public boolean isSuccess() {
        return success;
    }

    public byte[] encode(){
        StringBuilder sb = new StringBuilder()
                .append(id).append(" ")
                .append(balance).append(" ")
                .append(success);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static Reply decode(byte[] msg){
        String[] params = new String(msg).split(" ");
        return new Reply(Integer.parseInt(params[0]),
                Integer.parseInt(params[1]),
                Boolean.parseBoolean(params[2]));
    }
}
