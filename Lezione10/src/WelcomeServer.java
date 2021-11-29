/*
Definire un Server WelcomeServer, che
  - invia su un gruppo di multicast (welcomegroup*), ad intervalli regolari, un messaggio
di «welcome».
  - attende tra un invio ed il successivo un intervallo di tempo simulato mediante il
metodo sleep().

*Ad esempio con indirizzo IP 239.255.1.3
*/
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class WelcomeServer {
    private static final int port = 42069;
    private static final int waitTime = 2000;

    public static void main(String []args) {
        try (MulticastSocket multicastSocket = new MulticastSocket()) {
            InetAddress groupAddress = InetAddress.getByName(args[0]);
            byte []welcomeMessage = "<<Welcome!>>".getBytes();

            DatagramPacket packet = new DatagramPacket(welcomeMessage, welcomeMessage.length, groupAddress, port);

            while(true) {
                multicastSocket.send(packet);
                Thread.sleep(waitTime);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
