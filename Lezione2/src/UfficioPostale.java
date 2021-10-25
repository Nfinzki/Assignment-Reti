/*Simulare il flusso di clienti in un ufficio postale che ha 4 sportelli. Nell'ufficio esiste:

- un'ampia sala d'attesa in cui ogni persona può entrare liberamente. Quando entra, ogni persona prende il numero dalla
numeratrice e aspetta il proprio turno in questa sala.
- una seconda sala, meno ampia, posta davanti agli sportelli, in cui si può entrare solo a gruppi di k persone
una persona si mette quindi prima in coda nella prima sala, poi passa nella seconda sala.
- Ogni persona impiega un tempo differente per la propria operazione allo sportello. Una volta terminata l'operazione,
la persona esce dall'ufficio.

Scrivere un programma in cui:

- l'ufficio viene modellato come una classe JAVA, in cui viene attivato un ThreadPool di dimensione uguale al numero degli sportelli
- la coda delle persone presenti nella sala d'attesa è gestita esplicitamente dal programma
- la seconda coda (davanti agli sportelli) è quella gestita implicitamente dal ThreadPool
- ogni persona viene modellata come un task, un task che deve essere assegnato ad uno dei thread associati agli sportelli
- si preveda di far entrare tutte le persone nell'ufficio postale, all'inizio del programma

Facoltativo: prevedere il caso di un flusso continuo di clienti e la possibilità che l'operatore chiuda lo sportello
stesso dopo che in un certo intervallo di tempo non si presentano clienti al suo sportello.
*/

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UfficioPostale implements Runnable{
    private static final int office = 4;
    private static Integer firstRoomCapacity = 20;
    private static int secondRoomCapacity = 5;
    private static int keepAliveTimeOffice = 3000;
    private static int numPersone = 50;

    private final BlockingQueue<Persona> queue;
    private final ExecutorService pool;
    private final Lock lock;
    private final Condition condition;

    public UfficioPostale(BlockingQueue<Persona> queue, ExecutorService p, Lock lock, Condition condition) {
        this.queue = queue;
        this.pool = p;
        this.lock = lock;
        this.condition = condition;
    }

    /**
     * Task che si occupa di far passare le persone dalla prima alla seconda stanza
     */
    public void run() {
        Persona p = null;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                p = queue.take();
            } catch (InterruptedException e) {
                break;
            }

            boolean accepted = false;
            while (!accepted) {
                try {
                    pool.execute(p); //Prova a spostare la persona dalla prima alla seconda sala
                    accepted = true;
                } catch (RejectedExecutionException e) {
                    try {
                        lock.lock();
                        condition.await(); //Seconda sala piena, attesa che si liberi un posto
                    } catch (InterruptedException ie) {
                        break;
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    public static void main (String []args) {
        //Parsing degli argomenti facoltativi
        if (args.length != 0) {
            switch (args.length) {
                case 4: numPersone = Integer.parseInt(args[3]);
                case 3: keepAliveTimeOffice = Integer.parseInt(args[2]);
                case 2: secondRoomCapacity = Integer.parseInt(args[1]);
                case 1: {
                    firstRoomCapacity = Integer.parseInt(args[0]);
                    if (firstRoomCapacity <= secondRoomCapacity) {
                        System.out.println("La prima stanza dev'essere più grande della seconda");
                        return;
                    }
                }
            }
        }

        //Inizializzazione strutture dati
        BlockingQueue<Persona> firstRoom = new ArrayBlockingQueue<>(firstRoomCapacity);
        ExecutorService pool = new ThreadPoolExecutor(
                0,
                office,
                keepAliveTimeOffice, //Tempo di inattività prima che l'operatore chiuda il proprio sportello
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(secondRoomCapacity) //Coda per la seconda sala gestita dal thread pool
        );
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        //Creazione e avvio del thread che sposta le persone dalla prima alla seconda sala
        Thread roomSwitcher = new Thread(new UfficioPostale(firstRoom, pool, lock, condition));
        roomSwitcher.start();

        // Entrata delle persone nella prima stanza simulando un flusso continuo
        for (int i = 1; i <= numPersone; i++) {
            try {
                firstRoom.put(new Persona(i, lock, condition));
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 2000)); //Due persone possono arrivare nello stesso momento o in due momenti diversi
            } catch (InterruptedException e) {
                pool.shutdownNow();
                roomSwitcher.interrupt();
                return;
            }
        }

        //Terminazione
        if (roomSwitcher.isAlive()) roomSwitcher.interrupt();
        pool.shutdown();

    }
}

class Persona implements Runnable {
    private static final int maxTime = 3000;
    private final int number;

    private final Lock lock;
    private final Condition condition;

    public Persona(int number, Lock lock, Condition condition) {
        this.number = number;
        this.lock = lock;
        this.condition = condition;
    }

    public void run() {
        System.out.println("Thread-" + Thread.currentThread().getId() + ": Inizio operazione persona con id=" + this.number);

        try {
            //Simulazione dell'operazione che la persona effettua allo sportello
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, maxTime));
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
            return;
        }

        System.out.println("Thread-" + Thread.currentThread().getId() + ": Fine operazione persona con id=" + this.number);

        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
