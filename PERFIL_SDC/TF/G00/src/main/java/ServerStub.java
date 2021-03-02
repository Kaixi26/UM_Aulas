import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ServerStub {

    private final Socket server = new Socket();
    private BufferedOutputStream out;
    private BufferedInputStream in;


    public ServerStub(){
    }

    public void bind(int port) throws IOException {
        server.connect(new InetSocketAddress("localhost", port));
        out = new BufferedOutputStream(server.getOutputStream());
        in = new BufferedInputStream(server.getInputStream());
    }

    public boolean movement(int value) throws IOException {
        out.write(ByteBuffer.allocate(4).putInt(value).array());
        out.flush();
        byte[] buf = new byte[4] ;
        in.read(buf);
        return ByteBuffer.wrap(buf).getInt() == 0;
    }

    public int balance() throws IOException {
        out.write(ByteBuffer.allocate(4).putInt(0).array());
        out.flush();
        byte[] buf = new byte[4] ;
        in.read(buf);
        return ByteBuffer.wrap(buf).getInt();
    }


}
