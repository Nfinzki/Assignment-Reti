import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestCounter {
    public static final int workers = 20;
    public static final int maxWaiting = 2000;

    public static long runTest(ExecutorService pool, Counter counter) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < workers; i++) pool.execute(new Writer(counter));
        for (int i = 0; i < workers; i++) pool.execute(new Reader(counter));
        pool.shutdown();

        try {
            if (!pool.awaitTermination(maxWaiting, TimeUnit.MILLISECONDS))
                pool.shutdownNow();
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }

        long end = System.currentTimeMillis();

        return end - start;
    }

    public static void main (String []args) {
        //Primo test senza lock
        Counter c1 = new Counter();
        ExecutorService p1 = Executors.newCachedThreadPool();
        long test1 = runTest(p1, c1);

        //Secondo test con le lock reetrant
        Counter c2 = new ReentrantCounter();
        ExecutorService p2 = Executors.newCachedThreadPool();
        long test2 = runTest(p2, c2);

        //Terzo test con le lock ReadWrite
        Counter c3 = new ReadWriteCounter();
        ExecutorService p3 = Executors.newCachedThreadPool();
        long test3 = runTest(p3, c3);

        System.out.println("Test 1: " + test1 + "ms");
        System.out.println("Test 2: " + test2 + "ms");
        System.out.println("Test 3: " + test3 + "ms");

        //Test con FixedThreadPool al variare del numero di thread
        int exp = 6;
        Long []time = new Long[exp+1];

        for (int i = 0; i <= exp; i++) {
            Counter c = new ReentrantCounter();
            ExecutorService p = Executors.newFixedThreadPool((int) Math.pow(2, i));
            time[i] = runTest(p, c);
        }

        //Stampo i risultati
        for (int i = 0; i <= exp; i++) {
            System.out.println("FixedThreadPool con " + (int) Math.pow(2, i) + " thead: " + time[i] + "ms");
        }
    }
}

class Writer implements Runnable {
    private Counter counter;

    public Writer(Counter c) {
        counter = c;
    }

    public void run() {
        counter.increment();
    }
}

class Reader implements Runnable {
    private Counter counter;

    public Reader(Counter c) {
        counter = c;
    }

    public void run() {
        int count = counter.get();
        System.out.println("Counter = " + count);
    }
}

class ReentrantCounter extends Counter {
    private Lock lock = new ReentrantLock();

    @Override
    public void increment() {
        try {
            lock.lock();
            contatore++;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public int get() {
        int value;
        try {
            lock.lock();
            value = contatore;
        } finally {
            lock.unlock();
        }

        return value;
    }
}

class ReadWriteCounter extends Counter {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void increment() {
        try {
            lock.writeLock().lock();
            contatore++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int get() {
        int value;
        try {
            lock.readLock().lock();
            value = contatore;
        } finally {
            lock.readLock().unlock();
        }

        return value;
    }
}