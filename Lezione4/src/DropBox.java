/* Scrivere un programma in cui alcuni thread generano e consumano numeri interi da una risorsa
condivisa, chiamata Dropbox, avente capacità 1.

- Nella classe Dropbox, il buffer avente un solo elemento è realizzato tramite una variabile
intera. La classe offre un metodo take per consumare il numero e svuotare il buffer e un
metodo put per inserire un nuovo valore se il buffer è vuoto.

- Definire un task Consumer il cui metodo costruttore prende in ingresso un valore booleano
(true per consumare valori pari e false per valori dispari) e il riferimento ad un’istanza di
Dropbox. Nel metodo run invoca il metodo take sull’istanza di Dropbox.

- Definire un task Producer il cui metodo costruttore prende in ingresso il riferimento ad
un’istanza di Dropbox. Nel metodo run genera un intero in modo random, nel range
[0,100), e invoca il metodo put sull’istanza di Dropbox.

- Definire una classe contenente il metodo main. Nel main viene creata un’istanza di
Dropbox. Vengono quindi creati 2 oggetti di tipo Consumer (uno true e uno false) e un
oggetto di tipo Producer, ciascuno eseguito da un thread distinto.
*/

import java.util.concurrent.ThreadLocalRandom;

public class DropBox {
    int buffer;
    boolean isFull;

    public DropBox() {
        this.isFull = false;
    }

    public synchronized int take(boolean req) throws InterruptedException{
        while (!isFull || ((buffer % 2 == 0) != req)) this.wait();

        isFull = false;
        this.notify();

        return buffer;
    }

    public synchronized void put(int value) throws InterruptedException{
        while (isFull) this.wait();

        buffer = value;
        isFull = true;
        this.notify();
    }

    public static void main(String []args) {
        DropBox buffer = new DropBox();
        Thread threadOddConsumer = new Thread(new Consumer(false, buffer));
        Thread threadParConsumer = new Thread(new Consumer(true, buffer));
        Thread threadProducer = new Thread(new Producer(buffer));

        threadProducer.start();
        threadOddConsumer.start();
        threadParConsumer.start();

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 300));
        } catch (InterruptedException e) {
            threadProducer.interrupt();
            threadOddConsumer.interrupt();
            threadParConsumer.interrupt();
        }

        threadProducer.interrupt();
        threadOddConsumer.interrupt();
        threadParConsumer.interrupt();
    }
}

class Consumer implements Runnable {
    private boolean parity;
    private DropBox buffer;

    public Consumer(boolean parity, DropBox buffer) {
        this.parity = parity;
        this.buffer = buffer;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int obj;
            try {
                obj = buffer.take(parity);
                System.out.println("Consumato: " + obj);
            } catch (InterruptedException e) {
                System.out.println("Consumer interrotto");
                return;
            }
        }
    }
}

class Producer implements Runnable {
    private DropBox buffer;

    public Producer(DropBox buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int value = ThreadLocalRandom.current().nextInt(0, 100);
            try {
                buffer.put(value);

                System.out.println("Producer: Inserito " + value);
            } catch (InterruptedException e) {
                System.out.println("Producer interrotto");
                return;
            }
        }
    }
}
