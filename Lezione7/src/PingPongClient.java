import java.io.IOException;
import java.net.*;

public class PingPongClient {
    private static final int remotePort = 42069;
    private static final int timeout = 5000;
    private static final int bufferLength = 1024;

    public static void main(String []args) {
        try (DatagramSocket socket = new DatagramSocket(0)) {
            socket.setSoTimeout(timeout);
            byte []msg = "Ping".getBytes();

            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(msg, msg.length, host, remotePort);
            socket.send(request);

            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
            socket.receive(response);
            String responseMsg = new String(response.getData(), 0, response.getLength());
            System.out.println(responseMsg);

        } catch(SocketTimeoutException e) {
            System.out.println("Server didn't response");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
