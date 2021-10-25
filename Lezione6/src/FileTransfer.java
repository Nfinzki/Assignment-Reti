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
    private static int maxUptime = 7000;
    private static int maxDelay = 5000;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(42069)) {
            System.out.println("Server running");

            serverSocket.setSoTimeout(maxUptime);
            while (true) {
                try (Socket socket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    System.out.println("Host connected");
                    //pool.execute(new FileSender(socket));

                    String request = reader.readLine();
                    String requestParam[] = request.split(" ");
                    String file = requestParam[1].substring(1);

                    File fileRequested = new File(file);
                    if (!fileRequested.exists()) {
                        out.write("HTTP/1.1 404");
                    } else {
                        out.write("HTTP/1.1 200 OK\r\n");
                        out.write("Content-Type: image/jpeg\r\n");
                        out.write("\r\n");
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileRequested));

                        int readContent;
                        while((readContent = bufferedReader.read()) != -1) {
                            out.write(readContent);
                        }

                        bufferedReader.close();
                    }
                } catch (SocketTimeoutException e) {
                    break;
                } catch (IOException e) {
                    System.err.println("Error while waiting for connection");
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

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
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
            String request = reader.readLine();
            System.out.println(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}