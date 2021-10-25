import java.util.Calendar;

public class DataPrinterThread extends Thread {
    public void run() {

        while(true) {
            Calendar actualDateTime = Calendar.getInstance();

            System.out.println(actualDateTime.getTime());
            System.out.println(Thread.currentThread().getName());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + "exception");
                return;
            }
        }
    }

    public static void main(String arg[]) {
        DataPrinterThread dpt = new DataPrinterThread();
        dpt.start();

        System.out.println(Thread.currentThread().getName());
    }
}
