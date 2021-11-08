/*
PING è una utility per la valutazione delle performance della rete utilizzata per verificare la raggiungibilità di un
host su una rete IP e per misurare il round trip time (RTT) per i messaggi spediti da un host mittente verso un host destinazione.

* lo scopo di questo assignment è quello di implementare un server PING ed un corrispondente client PING che consenta al client di misurare il suo RTT verso il server.

* la funzionalità fornita da questi programmi deve essere simile a quella della utility PING disponibile in tutti i
moderni sistemi operativi. La differenza fondamentale è che si utilizza UDP per la comunicazione tra client e server,
invece del protocollo ICMP (Internet Control Message Protocol).

* inoltre, poichè l'esecuzione dei programmi avverrà su un solo host o sulla rete locale e in entrambe i casi sia la
latenza che la perdita di pacchetti risultano trascurabili, il server deve introdurre un ritardo artificiale ed
ignorare alcune richieste per simulare la perdita di pacchetti

PING CLIENT

* accetta due argomenti da linea di comando: nome e porta del server. Se uno o più argomenti risultano scorretti,
il client termina, dopo aver stampato un messaggio di errore del tipo ERR -arg x, dove x è il numero dell'argomento.

* utilizza una comunicazione UDP per comunicare con il server ed invia 10 messaggi al server, con il seguente formato:
PING seqno timestamp in cui seqno è il numero di sequenza del PING (tra 0-9) ed il timestamp (in millisecondi)
indica quando il messaggio è stato inviato

* non invia un nuovo PING fino che non ha ricevuto l'eco del PING precedente, oppure è scaduto un timeout.

* stampa ogni messaggio spedito al server ed il RTT del ping oppure un * se la risposta non è stata ricevuta entro 2 secondi

* dopo che ha ricevuto la decima risposta (o dopo il suo timeout), il client stampa un riassunto simile a quello stampato dal PING UNIX

---- PING Statistics ----

10 packets transmitted, 7 packets received, 30% packet loss, round-trip (ms) min/avg/max = 63/190.29/290

* RTT medio è stampato con 2 cifre dopo la virgola
*/

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class PingerClient {
    private static final int packets = 10;

    public static void main(String []args) {
        if (args.length != 2) {
            System.err.println("Usage: java PingerClient hostname port");
            System.exit(1);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        if (port < 0 || port > 65535) {
            System.err.println("ERR - arg 2");
        }

        int packetsReceived = 0;
        long min = Integer.MAX_VALUE;
        long max = Integer.MIN_VALUE;
        long avg = 0;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress host = InetAddress.getByName(hostname);
            socket.setSoTimeout(2000);

            for (int i = 0; i < packets; i++) {
                String msg = "PING " + i + " " + System.currentTimeMillis();
                byte[] message = msg.getBytes();

                DatagramPacket request = new DatagramPacket(message, message.length, host, port);
                socket.send(request);

                System.out.print(msg + " RTT: ");
                try {
                    DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                    long startTime = System.currentTimeMillis();
                    socket.receive(response);
                    long RTT = System.currentTimeMillis() - startTime;
                    System.out.println(RTT + " ms");

                    packetsReceived++;
                    min = Math.min(min, RTT);
                    max = Math.max(max, RTT);
                    avg += RTT;
                } catch (SocketTimeoutException e) {
                    System.out.println("*");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        float percentage = packets - packetsReceived;
        percentage = percentage * 100 / packets;

        System.out.println("---- PING Statistics ----");
        System.out.println(packets + " packets transmitted, " + packetsReceived + " packets received, "
                + String.format("%.02f", percentage) +"% packet loss, round-trip (ms) min/avg/max = "
                + min + "/" + String.format("%.02f", (float) avg/packetsReceived) + "/" + max);
    }
}
