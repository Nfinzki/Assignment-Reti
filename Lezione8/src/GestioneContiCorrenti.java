/*Creare un file contenente oggetti che rappresentano i conti correnti di una banca. Ogni conto corrente contiene il
nome del correntista e una lista di movimenti. I movimenti registrati per un conto corrente sono relativi agli
ultimi 2 anni, quindi possono essere molto numerosi.

Per ogni movimento vengono registrati la data e la causale del movimento. L'insieme delle causali possibili è fissato:
Bonifico, Accredito, Bollettino, F24, PagoBancomat.

Rileggere il file e trovare, per ogni possibile causale, quanti movimenti hanno quella causale.

Progettare un'applicazione che attiva un insieme di thread. Uno di essi legge dal file gli oggetti "conto corrente"
e li passa, uno per volta, ai thread presenti in un thread pool. Ogni thread calcola il numero di occorrenze di ogni
possibile causale all'interno di quel conto corrente ed aggiorna un contatore globale.

Alla fine il programma stampa per ogni possibile causale il numero totale di occorrenze.

Utilizzare:

1. NIO per creare il file.
2. NIO oppure IO classico per rileggere il file.
3. Il formato JSON (e quindi la libreria GSON) per la serializzazione.
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GestioneContiCorrenti {
    private static final String filename = "contiCorrenti.json";
    private static final int numConti = 25;
    private static final int numNomi = 5;
    private static final int minMovimenti = 1;
    private static final int maxMovimenti = 10;

    private static final int bufferSize = 16 * 1024;

    private static final int threadPoolDelay = 5000;

    public static void main(String []args) {
        //---- SCRITTURA JSON ----

        //Inizializzazione campi che genereranno il nome del correntista
        String []names = new String[numNomi];
        names[0] = "Mario";
        names[1] = "Francesco";
        names[2] = "Giulia";
        names[3] = "Maria";
        names[4] = "Luigi";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Creazione canale per la scrittura sul file
        try (WritableByteChannel out = Channels.newChannel(new FileOutputStream(filename))){
            //Inizializzazione ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            //Scrittura prima parentesi sul buffer
            buffer.put("[\n".getBytes());

            //Scrittura dei conti sul file Json
            for(int i = 0; i < numConti; i++) {
                List<Movimento> listaMovimenti = new ArrayList<>();
                //Generazione numero di movimenti
                int numMovimenti = ThreadLocalRandom.current().nextInt(minMovimenti, maxMovimenti);

                //Generazione dei movimenti
                for (int j = 0; j < numMovimenti; j++) {
                    listaMovimenti.add( //Aggiunta del movimento alla lista
                            new Movimento(
                                new Date(ThreadLocalRandom.current().nextLong(System.currentTimeMillis())), //Generazione data
                                getCausale(Math.abs(ThreadLocalRandom.current().nextInt())) //Generazione causale
                            )
                    );
                }

                //Scrittura della virgola se non è il primo elemento
                if (i != 0) buffer.put(",\n".getBytes());

                //Serializzazione oggetto ContoCorrente
                String serializedContoCorrente = gson.toJson(
                        new ContoCorrente(
                                names[ThreadLocalRandom.current().nextInt(0, numNomi)], //Generazione nome del correntista
                                listaMovimenti
                        )
                );

                //Scrittura dell'oggetto serializzato sul buffer
                buffer.put(serializedContoCorrente.getBytes());

                //Preparazione buffer in lettura per scrivere sul file
                buffer.flip();
                //Scrittura sul file
                while(buffer.hasRemaining()) out.write(buffer);

                //Preparazione buffer per la scrittura
                buffer.clear();
            }
            //Scrittura della parentesi chiusa per chiudere il file Json
            buffer.put("\n]".getBytes());
            //Preparazione buffer in lettura per scrivere sul file
            buffer.flip();
            //Scrittura sul file
            while(buffer.hasRemaining()) out.write(buffer);

        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }

        //---- LETTURA JSON ----

        //Creazione ThreadPool
        ExecutorService pool = Executors.newCachedThreadPool();

        //Creazione contatori di frequenza
        AtomicInteger numBonifici = new AtomicInteger(0);
        AtomicInteger numAccrediti = new AtomicInteger(0);
        AtomicInteger numBollettini = new AtomicInteger(0);
        AtomicInteger numF24 = new AtomicInteger(0);
        AtomicInteger numPagoBancomat = new AtomicInteger(0);

        //Deserializzazione degli oggetti dal file Json
        try {
            FileInputStream fis = new FileInputStream(filename);
            JsonReader reader = new JsonReader(new InputStreamReader(fis));

            //Consuma '['
            reader.beginArray();
            //Deserializzazione degli oggetti uno alla volta
            while (reader.hasNext()) {
                ContoCorrente cc = gson.fromJson(reader, ContoCorrente.class); //Deserializzazione

                //Passaggio dell'oggetto ContoCorrente al ThreadPool
                pool.execute(new Worker(cc, numBonifici, numAccrediti, numBollettini, numF24, numPagoBancomat));
            }
            //Consuma ']'
            reader.endArray();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }

        //Chiusura ThreadPool
        pool.shutdown();
        try {
            if (!pool.awaitTermination(threadPoolDelay, TimeUnit.MILLISECONDS))
                pool.shutdownNow();
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }

        //Stampa le statistiche
        System.out.println(Causale.Bonifico + ": " + numBonifici);
        System.out.println(Causale.Accredito + ": " + numAccrediti);
        System.out.println(Causale.Bollettino + ": " + numBollettini);
        System.out.println(Causale.F24 + ": " + numF24);
        System.out.println(Causale.PagoBancomat + ": " + numPagoBancomat);

    }

    private static Causale getCausale(int n) {
        return switch (n % 5) {
            case 1 -> Causale.Accredito;
            case 2 -> Causale.Bollettino;
            case 3 -> Causale.F24;
            case 4 -> Causale.PagoBancomat;
            default -> Causale.Bonifico; //Corrisponde al case 0
        };

    }
}

class Worker implements Runnable {
    private final ContoCorrente conto;
    private final AtomicInteger numBonifici;
    private final AtomicInteger numAccrediti;
    private final AtomicInteger numBollettini;
    private final AtomicInteger numF24;
    private final AtomicInteger numPagoBancomat;

    public Worker(ContoCorrente conto, AtomicInteger numBonifici, AtomicInteger numAccrediti,
                  AtomicInteger numBollettini, AtomicInteger numF24, AtomicInteger numPagoBancomat) {
        this.conto = conto;
        this.numBonifici = numBonifici;
        this.numAccrediti = numAccrediti;
        this.numBollettini = numBollettini;
        this.numF24 = numF24;
        this.numPagoBancomat = numPagoBancomat;
    }

    public void run() {
        //Esamina un movimento alla volta del conto corrente
        for (Movimento m : conto.getMovimenti()) {
            switch (m.getCasuale()) {
                case Bonifico -> numBonifici.incrementAndGet();
                case Accredito -> numAccrediti.incrementAndGet();
                case Bollettino -> numBollettini.incrementAndGet();
                case F24 -> numF24.incrementAndGet();
                case PagoBancomat -> numPagoBancomat.incrementAndGet();
            }
        }
    }
}

class ContoCorrente {
    private final String name;
    private final List<Movimento> listaMovimenti;

    public ContoCorrente(String name) {
        if (name == null) throw new NullPointerException();
        this.name = name;
        listaMovimenti = new ArrayList<>();
    }

    public ContoCorrente(String name, List<Movimento> movimenti) {
        if (name == null || movimenti == null) throw new NullPointerException();
        this.name = name;
        this.listaMovimenti = movimenti;
    }

    public void addMovement(Movimento mov) {
        listaMovimenti.add(mov);
    }

    public List<Movimento> getMovimenti() {
        return listaMovimenti;
    }
}

enum Causale {
    Bonifico,
    Accredito,
    Bollettino,
    F24,
    PagoBancomat
}

class Movimento {
    private final Date data;
    private final Causale casuale;

    public Movimento(Date data, Causale casuale) {
        this.data = data;
        this.casuale = casuale;
    }

    public Causale getCasuale() {
        return casuale;
    }
}