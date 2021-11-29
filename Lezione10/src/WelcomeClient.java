import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/*
Definire un client WelcomeClient che si unisce a welcomegroup e riceve un messaggio di
welcome, quindi termina.
 */
public class WelcomeClient {
    private static final int messageDim = 256;

    public static void main(String []args) {
        try (MulticastSocket ms = new MulticastSocket(42069)) {
            byte []message = new byte[messageDim];
            InetAddress address = InetAddress.getByName(args[0]);
            DatagramPacket packet = new DatagramPacket(message, message.length);

            ms.joinGroup(address);
            ms.receive(packet);

            String messageReceived = new String(packet.getData(), 0, packet.getLength());
            System.out.println(messageReceived);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
