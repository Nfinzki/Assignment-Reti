import java.util.Calendar;

public class DatePrinter {
    public static void main(String args[]) {
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
}
