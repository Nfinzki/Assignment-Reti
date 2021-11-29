/*
Definire un client WelcomeClient che si unisce a welcomegroup e riceve un messaggio di
welcome, quindi termina.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class WelcomeClient {
    private static final int messageDim = 256;
    private static final int port = 42069;

    public static void main(String []args) {
        try (MulticastSocket ms = new MulticastSocket(port)) {
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
