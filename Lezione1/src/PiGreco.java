public class PiGreco implements Runnable {
    private double accuracy;

    public PiGreco(double accuracy) {
        this.accuracy = accuracy;
    }

    public void run() {
        double pi = 4, den = 3;
        int i = 2;

        while (!Thread.interrupted() && Math.abs(pi - Math.PI) >= accuracy) {
            if (i % 2 == 0) {
                pi -= 4/den;
            } else {
                pi += 4/den;
            }
            i++;
            den +=2;
        }

        System.out.println("Pi = " + pi);
    }

    public static void main(String arg[]) {

        double accuracy = Double.parseDouble(arg[0]);
        int maxTime = Integer.parseInt(arg[1]);

        Thread calculatePI = new Thread(new PiGreco(accuracy));
        calculatePI.start();

        try {
            calculatePI.join(maxTime);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }

        calculatePI.interrupt();
    }
}
