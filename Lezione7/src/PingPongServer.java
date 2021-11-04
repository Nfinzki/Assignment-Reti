/*
L'esercizio consiste nella scrittura di un server che offre il servizio di "Ping Pong" e del relativo programma
client.
Un client invia al server un messaggio di "Ping".
Il server, se riceve il messaggio, risponde con un messaggio di "Pong".
Il client sta in attesa n secondi di ricevere il messaggio dal server (timeout) e poi termina.
Client e Server usano il protocollo UDP per lo scambio di messaggi.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class PingPongServer {
    private static final int localPort = 42069;
    private static final int timeout = 5000;
    private static final int bufferLength = 1024;

    public static void main(String []args) {
        System.out.println("Starting server...");
        try (DatagramSocket socket = new DatagramSocket(localPort)) {
            socket.setSoTimeout(timeout);
            System.out.println("Server ready");

            while (true) {
                DatagramPacket request = new DatagramPacket(new byte[bufferLength], bufferLength);
                socket.receive(request);
                String req = new String(request.getData(), 0, request.getLength());
                System.out.println("Request received: " + req);

                byte[] msg;
                if (req.equals("Ping"))
                    msg = "Pong".getBytes();
                else
                    msg = "Invalid request".getBytes();

                DatagramPacket response = new DatagramPacket(msg, msg.length, request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Closing server...");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
