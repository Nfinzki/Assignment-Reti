import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
Scrivere un programma Java che implementi un server HTTP che gestisca richieste di trasferimento di file di diverso
tipo (es. immagini jpeg, gif) provenienti da un browser Web.

Il server sta in ascolto su una porta nota al client (es. 6789).
Il server gestisce richieste HTTP di tipo GET alla request URL http://localhost:port/filename.
Le connessioni possono essere non persistenti.
Usare le classi Socket e ServerSocket per sviluppare il programma server.
Per inviare al server le richieste, utilizzare un qualsiasi browser. In alternativa, se avete un sistema
Unix-based (oppure il WSL su Windows) potete utilizzare
cURL (https://curl.se/) da terminale o wget (https://www.gnu.org/software/wget/).
 */
public class FileTransfer {
    private static final int maxUptime = 7000;
    private static final int maxDelay = 5000;
    private static final int serverPort = 42069;

    public static void main(String[] args) {
        //Creazione ThreadPool per servire i client
        ExecutorService pool = Executors.newCachedThreadPool();

        //Creazione serverSocket
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server running");

            serverSocket.setSoTimeout(maxUptime); //Imposta il timeout del socket
            while (true) {
                try {
                    Socket socket = serverSocket.accept(); //Attesa connessione client
                    System.out.println("Host connected");

                    pool.execute(new FileSender(socket)); //Passaggio del task al ThreadPool

                } catch (SocketTimeoutException e) { //Scaduto il timeout
                    break;
                } catch (IOException e) {
                    System.err.println("Error while waiting for connection");
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        //Chiusura ThreadPool
        pool.shutdown();
        try {
            if (!pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }

        System.out.println("Server closed");
    }
}

class FileSender implements Runnable {
    private final Socket communicationSocket;

    public FileSender(Socket communicationSocket) {
        this.communicationSocket = communicationSocket;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
             OutputStream out = communicationSocket.getOutputStream()) {
            //Lettura richiesta
            String request = reader.readLine();
            String []requestParam = request.split(" ");

            //Controllo del tipo di richiesta
            if (!requestParam[0].equals("GET")) {
                out.write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes());
                communicationSocket.close();
                return;
            }

            String file = requestParam[1].substring(1); //Ottenimento filename
            File fileRequested = new File(file); //Apertura file

            //Controlla l'esistenza del file
            if (!fileRequested.exists()) {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            } else {
                //Lettura dei dati del file
                byte[] fileData = new byte[(int) fileRequested.length()];
                FileInputStream fileIn = new FileInputStream(fileRequested);
                fileIn.read(fileData);
                fileIn.close();

                //Costruzione del response header
                String responseHeader = "HTTP/1.1 200 OK\r\n";
                responseHeader += "Content-Type: " + getContentType(file) + "\r\n";
                responseHeader += "Content-Length: " + fileRequested.length() + "\r\n";
                responseHeader += "\r\n";

                //Invio della risposta al client
                out.write(responseHeader.getBytes());
                out.write(fileData);
            }

            communicationSocket.close(); //Chiusura socket
        } catch (IOException e) {
            System.err.println("Error while waiting for connection: " + e);
        }
    }

    /**
     *
     * @param filepath stringa che identifica un path
     * @return estensione del file se filepath è diverso da null
     * @throws NullPointerException se filepath è null
     */
    private static String getContentType(String filepath) {
        if (filepath == null) throw new NullPointerException();

        String fileType;
        switch (filepath.substring(filepath.length() - 3)) {
            case "jpg" -> fileType = "image/jpeg";
            case "png" -> fileType = "image/png";
            case "gif" -> fileType = "image/gif";
            case "txt" -> fileType = "txt/plain";
            case "mp3" -> fileType = "audio/mpeg";
            case "mp4" -> fileType = "video/mp4";
            default -> fileType = "text/html";
        }
        return fileType;
    }
}