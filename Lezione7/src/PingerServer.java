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

PING SERVER

è essenzialmente un echo server: rimanda al mittente qualsiasi dato riceve

* accetta un argomento da linea di comando: la porta, che è quella su cui è attivo il server + un argomento opzionale,
il seed, un valore long utilizzato per la generazione di latenze e perdita di pacchetti. Se uno qualunque degli argomenti
è scorretto, stampa un messaggio di errore del tipo ERR -arg x,dove x è il numero dell'argomento.

* dopo aver ricevuto un PING, il server determina se ignorare il pacchetto (simulandone la perdita) o effettuarne l'eco.
La probabilità di perdita di pacchetti di default è del 25%.

* se decide di effettuare l'eco del PING, il server attende un intervallo di tempo casuale per simulare la latenza di rete

* stampa l'indirizzo IP e la porta del client, il messaggio di PING e l'azione intrapresa dal server in seguito alla sua
ricezione (PING non inviato,oppure PING ritardato di x ms).
*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PingerServer {
    private static final int bufferLength = 1024;
    private static final int socketTimeout = 10000;
    private static final int poolTimeout = 5000;

    public static void main(String []args) {
        if (args.length < 1 || args.length > 2) { //TODO Rivedere dalle slide cosa scrivere in caso di errore
            System.err.println("Usage: PingerServer Port [Seed]");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        if (port < 0 || port > 65535) {
            System.err.println("ERR - arg 1 : Invalid port number");
            System.exit(1);
        }

        long seed = 69691;
        if (args.length == 2) {
            seed = Long.parseLong(args[1]);

            if (seed < 0) {
                System.err.println("ERR - arg 2 : Negative seed");
                System.exit(1);
            }
        }

        //Inizializzazione del generatore di seed da passare ai thread
        Random seedGenerator = new Random(seed);

        //Inizializzazione ThreadPool
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("Server starting...");
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            serverSocket.setSoTimeout(socketTimeout);
            System.out.println("Server started");

            while (true) {
                DatagramPacket request = new DatagramPacket(new byte[bufferLength], bufferLength);
                serverSocket.receive(request);

                pool.execute(new Pinger(serverSocket, request, seedGenerator.nextLong()));
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Closing server...");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        //Chiusura ThreadPool
        pool.shutdown();
        try {
            if (!pool.awaitTermination(poolTimeout, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }

        System.out.println("Server closed");
    }
}

class Pinger implements Runnable{
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final long seed;

    public Pinger(DatagramSocket socket, DatagramPacket request, long seed) {
        this.socket = socket;
        this.request = request;
        this.seed = seed;
    }

    public void run() {
        Random generator = new Random(seed);
        int delay;
        String msg = new String(request.getData(), 0, request.getLength());

        System.out.print(request.getAddress().getHostAddress() + ":" + request.getPort() + "> " + msg + " ACTION: ");
        try {
            if (generator.nextInt(100) >= 24) { //Il pacchetto non viene perso con il 75% di probabilità
                DatagramPacket response = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());

                delay = generator.nextInt();
                Thread.sleep(delay);
                socket.send(response);
                System.out.println("delayed " + delay + " ms");
            } else {
                System.out.println("not sent");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}