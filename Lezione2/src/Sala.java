import java.util.concurrent.*;

/*Nella sala biglietteria di una stazione sono presenti 5 emettitrici automatiche dei biglietti. Nella sala non
possono essere presenti più di 10 persone in attesa di usare le emettitrici.
Scrivere un programma che simula la situazione sopra descritta.
● La sala della stazione viene modellata come una classe JAVA. Uno dopo l’altro arrivano 50
viaggiatori (simulare un intervallo di 50 ms con Thread.sleep).

● ogni viaggiatore viene simulato da un task, la prima operazione consiste nello stampare
“Viaggiatore {id}: sto acquistando un biglietto”, aspettare per un intervallo di tempo random tra 0 e
1000 ms e poi stampa “Viaggiatore {id}: ho acquistato il biglietto”.

● I task vengono assegnati a un numero di thread pari al numero delle emettitrici

● Il rispetto della capienza massima della sala viene garantita dalla coda gestita dal thread. I
viaggiatori che non possono entrare in un certo istante perché la capienza massima è stata
raggiunta abbandonano la stazione (il programma main stampa quindi “Traveler no. {i}: sala
esaurita”.

● Suggerimento: usare un oggetto ThreadPoolExecutor in cui il numero di thread è pari al numero
degli sportelli
 */
public class Sala {
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
                pool.execute(new Viaggiatore(i));
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
    }
}

class Viaggiatore implements Runnable{
    private final int id;

    public Viaggiatore (int id) {
        this.id = id;
    }

    public void run() {
        System.out.println("Viaggiatore " + this.id + ": sto acquistando un biglietto");
        try {
            Thread.sleep(Math.abs(ThreadLocalRandom.current().nextInt()) % 1001);
            //ThreadLocalRandom.current(0, 1000+1)
        } catch (InterruptedException e) {
            System.out.println(e + "interruption catched");
            return;
        }
        System.out.println("Viaggiatore " + this.id + ": ho acquistato il biglietto");
    }
}