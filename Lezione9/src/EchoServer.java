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
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    private static final int port = 42069;
    private static final int bufferSize = 16 * 1024;

    private static final int timeout = 10000;

    public static void main(String []args) {
        //Inizializzazione de server
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             ServerSocket serverSocket = serverSocketChannel.socket();
             Selector selector = Selector.open()) {

            serverSocket.bind(new InetSocketAddress(port)); //Associa il socket alla porta
            serverSocketChannel.configureBlocking(false); //Imposta la modalità non bloccante
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); //Registra il canale sul Selector

            System.out.println("Server started");

            while (true) {
                //Attesa di una richiesta
                if (selector.select(timeout) == 0) break;

                //Recupera le chiavi pronte
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();

                while (iterator.hasNext()) {
                    //Recupera la chiave
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isAcceptable()) { //Nuova connessione
                            ServerSocketChannel server = (ServerSocketChannel) key.channel(); //Recupera il channel
                            SocketChannel client = server.accept(); //Accetta la connessione del client
                            System.out.println(client);

                            client.configureBlocking(false); //Imposta il canale in modalità non bloccante
                            SelectionKey clientKey = client.register( //Registra il canale sul Selector in lettura
                                    selector,
                                    SelectionKey.OP_READ
                            );

                            //Alloca il buffer e fa l'attach con il canale
                            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
                            clientKey.attach(byteBuffer);

                        } else if (key.isReadable()) { //Il client è pronto in lettura
                            SocketChannel client = (SocketChannel) key.channel(); //Recupera il channel
                            ByteBuffer byteBuffer = (ByteBuffer) key.attachment(); //Recupera il buffer

                            byteBuffer.clear(); //Predispone la scrittura sul buffer
                            client.read(byteBuffer); //Scrive dati sul buffer

                            //Aggiunge all'interestSet l'operazione di scrittura
                            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        } else if (key.isWritable()) { //Il client è pronto in scrittura
                            SocketChannel client = (SocketChannel) key.channel(); //Recupera il channel
                            ByteBuffer byteBuffer = (ByteBuffer) key.attachment(); //Recupera il buffer

                            byteBuffer.put(" - echoed by server".getBytes()); //Aggiunge la stringa al messaggio
                            byteBuffer.flip(); //Predisponde il buffer in modalità lettura

                            client.write(byteBuffer); //Legge i dati dal buffer e li scrive sul canale
                            byteBuffer.clear();

                            //Chiusura comunicazione con il client
                            client.close();
                            key.cancel();
                        }
                    } catch (IOException e) {
                        System.err.println("Error serving requests: " + e.getMessage());
                        key.cancel();
                        key.channel().close();
                    }
                }
            }

            System.out.println("Closing server...");
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }

    }
}
