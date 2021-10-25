import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
Scrivere un programma JAVA che implementi un server che apre una serversocket su una porta e sta in
attesa di richieste di connessione.
Quando arriva una richiesta di connessione, il server accetta la connessione, trasferisce al client un file e poi
chiude la connessione.
Ulteriori dettagli:
- Il server gestisce una richiesta per volta
- Il server invia sempre lo stesso file, usate un file di testo
Per il client potete usare telnet. Qui sotto un esempio di utilizzo:
*/
public class EsercizioSocket {
    public static int maxUptime = 7000;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Il programma accetta solo un campo <filename>");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!args[0].contains(".txt") || file.isDirectory()) {
            System.err.println("Il programma accetta solo file testuali");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(42069)) {
            System.out.println("Server running...");

            serverSocket.setSoTimeout(maxUptime);
            while (true) {
                try (Socket socket = serverSocket.accept();
                     FileInputStream fileInputStream = new FileInputStream(file)) {

                    System.out.println("Host connesso");
                    OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

                    int c;
                    while((c = fileInputStream.read()) != -1) {
                        out.write(c);
                        out.flush();
                    }

                    out.close();
                } catch (SocketTimeoutException e) {
                    System.out.println("Server didn't receive requests for too long");
                    break;
                } catch (IOException e) {
                    System.err.println(e.toString());
                    continue;
                } catch (Exception e) {
                    System.out.println(e.toString());
                    break;
                }
            }

        } catch (IOException e){
            System.err.println(e.toString());
        }

        System.out.println("Server closed");
    }
}
