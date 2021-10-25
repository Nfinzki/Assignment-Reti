import java.time.Duration;
import java.util.concurrent.*;

/* Estendi il programma dell’Esercizio 1 gestendo la terminazione del threadpool.
   Dopo l’arrivo dell’ultimo viaggiatore e l’invio del corrispondente task al threadpool, terminare il threadpool.
*/
public class SalaT {
    public static void main(String[] args) {
        ExecutorService pool = new ThreadPoolExecutor(
                5,
                5,
                0,
                TimeUnit.MICROSECONDS,
                new ArrayBlockingQueue<Runnable>(10)
        );

        for (int i = 0; i < 50; i++) {
            try {
                pool.execute(new ViaggiatoreT(i));
            } catch (RejectedExecutionException e) {
                System.out.println("Traveler no. " + i + ": sala esaurita");
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println(e + "catched");
                return;
            }
        }

        pool.shutdown();
        try {
            if (!pool.awaitTermination(5000, TimeUnit.MILLISECONDS))
                pool.shutdownNow();
        } catch (InterruptedException e) {
                pool.shutdownNow();
        }
    }
}

class ViaggiatoreT implements Runnable{
    private final int id;

    public ViaggiatoreT (int id) {
        this.id = id;
    }

    public void run() {
        System.out.println("Viaggiatore " + this.id + ": sto acquistando un biglietto");
        try {
            Thread.sleep(Math.abs(ThreadLocalRandom.current().nextInt()) % 1001);
            //ThreadLocalRandom.current.nextInt(0, 1000+1)
        } catch (InterruptedException e) {
            System.out.println(e + "interruption catched");
            return;
        }
        System.out.println("Viaggiatore " + this.id + ": ho acquistato il biglietto");
    }
}
