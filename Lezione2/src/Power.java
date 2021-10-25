/*Scrivere un programma che calcola le potenze di un numero n (esempio n=2) da n
2 a n
50 e restituisce come
risultato la somma delle potenze, ovvero:
Result = n
2 + n
3 + … + n
50
● Creare una classe Power di tipo Callable che riceve come parametri di ingresso il numero n e un
intero (l’esponente), stampa “Esecuzione {n}^{esponente} in {idthread}” e restituisce il risultato
dell’elevamento a potenza (usare la funzione Math.pow() di Java
https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#pow-double-double-)
● Creare una classe che nel metodo public static void main(String args[]) crea un threadpool e gli
passa i task Power.
● I risultati restituiti dai task vengono recuperati e sommati e il risultato della somma viene
stampato (usare una struttura dati, es. ArrayList per memorizzare gli oggetti di tipo Future
restituiti dal threadpool in corrispondenza dell’invocazione del metodo submit).*/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Power implements Callable<Double>{
    private double n;
    private int exp;

    public Power(double n, int exp) {
        this.n = n;
        this.exp = exp;
    }

    public Double call() throws Exception{
        if (exp < 2 || exp > 50) {
            throw new Exception("Esponente non valido: " + exp);
        }

        System.out.println("Esecuzione " + n + "^" + exp + " in " + Thread.currentThread().getId());
        return Math.pow(n, exp);
    }

    public static void main(String [] args) {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            return;
        }

        Double n = Double.parseDouble(args[0]);

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<Double>> pows = new ArrayList<>();

        for (int i = 2; i <= 50; i++) {
            pows.add(pool.submit(new Power(n, i)));
        }

        double res = 0;
        try {
            for (Future<Double> f : pows) res += f.get();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            pool.shutdownNow();
        }

        System.out.println("Risultato: " + res);

        pool.shutdown();

        try {
            if (!pool.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
    }
}