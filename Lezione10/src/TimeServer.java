/*
Definire un Server TimeServer, che
- invia su un gruppo di multicast dategroup, ad intervalli regolari, la data e
l’ora.
- attende tra un invio ed il successivo un intervallo di tempo simulata
mediante il metodo sleep().
- l’indirizzo IP di dategroup viene introdotto da linea di comando.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;

public class TimeServer {
    private static final int port = 42069;
    private static final int sleepTime = 2000;

    public static void main (String []args) {
        if (args.length != 1) {
            System.err.println("Usage: TimeServer address");
            System.exit(1);
        }

        try (MulticastSocket dateGroup = new MulticastSocket()) { //Creazione socket per il multicast
            InetAddress address = InetAddress.getByName(args[0]); //Recupera l'InetAddress dell'indirizzo fornito in input

            //Invia i messaggi
            while (true) {
                Date date = new Date(System.currentTimeMillis()); //Recupera data e ora correnti
                byte  []stringDate = date.toString().getBytes(); //Converte il dato in byte

                //Costruisce il datagram da inviare
                DatagramPacket packet = new DatagramPacket(stringDate, stringDate.length, address, port);

                dateGroup.send(packet); //Invia il datagram

                //Simula l'intervallo di tempo tra un'invio e l'altro
                Thread.sleep(sleepTime);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
