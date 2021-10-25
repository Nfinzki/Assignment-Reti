/* Si scriva un programma Java che riceve in input un filepath che individua una directory D e stampa le informazioni
del contenuto di quella directory e, ricorsivamente, di tutti i file contenuti nelle sottodirectory di D.

Il programma deve essere strutturato come segue:

Attiva un thread produttore ed un insieme di k thread consumatori.
Il produttore comunica con i consumatori mediante una coda.
Il produttore visita ricorsivamente la directory data e eventualmente tutte le sottodirectory e mette nella coda
il nome di ogni directory individuata.
I consumatori prelevano dalla coda i nomi delle directory e stampano il loro contenuto.
La coda deve essere realizzata con una LinkedList. Ricordiamo che una LinkedList non è una struttura thread-safe.
Dalle API Java: "Note that the implementation is not synchronized. If multiple threads access a linked list concurrently,
and at least one of the threads modifies the list structurally, it must be synchronized externally".
*/

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileCrawler {
    private static final int numThread = 5;
    private static final int maxDelay = 5000;

    public static void main (String []args) {
        if (args.length != 2) {
            System.err.println("Usa FileCrawler <directory> <num. consumatori>");
            System.exit(1);
        }

        String startingDirectoryName = args[0];
        int numConsumer = Integer.parseInt(args[1]);

        //Verifica l'esistenza della directory
        File startingDirectory = new File(startingDirectoryName);
        if (!startingDirectory.isDirectory()) {
            System.err.println(startingDirectoryName + " non è una directory");
            System.exit(1);
        }

        //Verifica che il numero dei consumatori passato in input sia valido
        if (numConsumer <= 0) {
            System.err.println("Il numero dei consumer dev'essere > 0");
            System.exit(1);
        }

        //Creazione lista condivisa tra i vari thread
        LinkedList<String> directoryList = new LinkedList<>();

        //Creazione ed esecuzione del produttore
        Thread tProduttore = new Thread(new Produttore(startingDirectory, directoryList));
        tProduttore.start();


        //Creazione ed esecuzione dei consumatori
        Thread[] tConsumatore = new Thread[numConsumer];
        for (int i = 0; i < numConsumer; i++) {
            tConsumatore[i] = new Thread(new Consumatore(directoryList));
            tConsumatore[i].start();
        }

        //Attesa terminazione thread
        try {
            tProduttore.join();

            for (Thread t : tConsumatore)
                t.join();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}

class Produttore implements Runnable {
    private final File dir;
    private final List<String> dirList;

    public Produttore(File dir, List<String> lst) {
        this.dir = dir;
        dirList = lst;
    }

    public void run() {
        inspectDir(dir);

        //Aggiunge stringa di terminazione
        synchronized (dirList) {
            dirList.add("");
            dirList.notifyAll();
        }
    }

    /**
     * Visita ricorsivamente la directory e aggiunge alla lista condivisa tutte le directory di cui stampre il contenuto
     * @param dir directory da cui partire
     */
    private void inspectDir(File dir) {
        synchronized (dirList) {
            dirList.add(dir.getAbsolutePath()); //Inserimento in lista
            dirList.notify(); //Sveglia un potenziale thread in attesa di un inserimento
        }

        //Visita ricorsivamente tutte le sottocartelle
        File[] listFiles = dir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory())
                    inspectDir(file);
            }
        }
    }
}

class Consumatore implements Runnable {
    private final LinkedList<String> dirList;

    public Consumatore(LinkedList<String> lst) {
        this.dirList = lst;
    }

    public void run() {
        int i = 0;

        while (true) {
            String dirToAnalyze;

            synchronized (dirList) {
                try {
                    while (dirList.isEmpty()) { //Attende che la lista venga riempita
                        dirList.wait();
                    }

                    //Controlla che sia arrivata la stringa di terminazione
                    if (dirList.peek().equals("")) return;

                    dirToAnalyze = dirList.removeFirst(); //Rimuove l'elemento in testa
                } catch (InterruptedException e) {
                    return;
                }
            }

            printContent(new File(dirToAnalyze)); //Stampa il contenuto della directory
        }
    }

    /**
     * Stampa il contenuto di una directory
     * @param dir directory di cui stampare il contenuto
     */
    private void printContent(File dir) {
        File[] listFiles = dir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                System.out.println(dir.getName() + " contiene: " + file.getAbsolutePath());
            }
        }
    }
}