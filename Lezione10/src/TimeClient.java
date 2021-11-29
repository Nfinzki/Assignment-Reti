/*
- definire quindi un client TimeClient che si unisce a dategroup e riceve,
per dieci volte consecutive, data ed ora, le visualizza, quindi termina.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class TimeClient {
    private static final int port = 42069;
    private static final int bufferSize = 1024;
    private static final int messageDesired = 10;

    public static void main(String []args) {
        if (args.length != 1) {
            System.err.println("Usage: TimeClient address");
            System.exit(1);
        }

        try (MulticastSocket dateGroup = new MulticastSocket(port)) { //Creazione socket per il multicast
            InetAddress address = InetAddress.getByName(args[0]); //Recupera l'InetAddress dell'indirizzo fornito in input
            dateGroup.joinGroup(address); //Si aggiunge al gruppo multicast

            byte []messageBuffer = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length); //Creazione datagram da ricevere

            //Ricezione dei messaggi
            for (int i = 0; i < messageDesired; i++) {
                dateGroup.receive(packet); //Si mette in attesa di ricevere un messaggio
                String date = new String(packet.getData(), 0, packet.getLength()); //Estrae l'informazione
                System.out.println(date);
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
