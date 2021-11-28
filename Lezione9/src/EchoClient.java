/*
• scrivere un programma echo server usando la libreria java NIO e, in
particolare, il Selector e canali in modalità non bloccante, e un programma
echo client, usando NIO (va bene anche con modalità bloccante).
• Il server accetta richieste di connessioni dai client, riceve messaggi inviati dai
client e li rispedisce (eventualmente aggiungendo "echoed by server" al
messaggio ricevuto).
• Il client legge il messaggio da inviare da console, lo invia al server e visualizza
quanto ricevuto dal server
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient {
    private static final int port = 42069;
    private static final String hostname = "localhost";
    private static final int bufferSize = 16 * 1024;

    public static void main(String []args) {
        if (args.length != 1) {
            System.err.println("Usage: EchoClient message");
            System.exit(1);
        }

        try (SocketChannel client = SocketChannel.open(new InetSocketAddress(hostname, port))) { //Apertura socket
            ByteBuffer buffer = ByteBuffer.allocate(args[0].length() + " - echoed by server".length()); //Alloca il buffer
            buffer.put(args[0].getBytes()); //Scrive la stringa sul buffer
            buffer.flip(); //Predispone il buffer in lettura
            client.write(buffer); //Legge dal buffer e scrive sul canale

            buffer.clear(); //Pulisce il buffer
            client.read(buffer); //Legge la risposta del server dal canale e scrive sul buffer

            String stringResponse = new String(buffer.array()); //Costruisce la stringa di risposta

            System.out.println(stringResponse);
        } catch (IOException e) {
            System.err.println("IOException e: " + e.getMessage());
            System.exit(1);
        }
    }
}
