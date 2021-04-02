package Message;

import java.nio.charset.StandardCharsets;

public class Request {
    private Type type;
    private int diff = 0;
    private int id = 0;

    public static Request movement(int id, int diff){
        Request message = new Request();
        message.type = Type.MOVEMENT;
        message.diff = diff;
        message.id = id;
        return message;
    }

    public static Request balance(int id){
        Request message = new Request();
        message.type = Type.BALANCE;
        message.id = id;
        return message;
    }

    public Type getType() {
        return type;
    }

    public int getDiff() {
        return diff;
    }

    public int getId() {
        return id;
    }

    public byte[] encode(){
        StringBuilder sb = new StringBuilder().append(id).append(" ");
        switch (type){
            case BALANCE:
                sb.append("B");
                break;
            case MOVEMENT:
                sb.append("M ").append(diff);
                break;
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static Request decode(byte[] msg) throws Exception {
        String[] params = new String(msg).split(" ");
        switch (params[1]){
            case "B":
                return balance(Integer.parseInt(params[0]));
            case "M":
                return movement(Integer.parseInt(params[0]), Integer.parseInt(params[2]));
        }
        throw new Exception();
    }
}
