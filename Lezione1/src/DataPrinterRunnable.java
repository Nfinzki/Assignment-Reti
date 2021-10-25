import java.util.Calendar;


public class DataPrinterRunnable implements Runnable {
    public void run() {
        while(true) {
            Calendar actualDateTime = Calendar.getInstance();

            System.out.println(actualDateTime.getTime());
            System.out.println(Thread.currentThread());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread() + "exception");
                return;
            }
        }
    }

    public static void main (String[] args) {
        Thread thread = new Thread(new DataPrinterRunnable());
        thread.start();

        System.out.println(Thread.currentThread());
    }
}
