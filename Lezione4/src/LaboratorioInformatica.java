import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/* Risolvere il problema della simulazione del Laboratorio di Informatica,
assegnato nella lezione precedente, utilizzando il costrutto del Monitor
*/
public class LaboratorioInformatica {
    private static final int maxAccesses = 10;
    private static final int numComputers = 20;

    private final boolean []computers;
    private final List<Integer> computersRequired;

    private int professorAwaits = 0;
    private int thesisStudentAwaits = 0;

    public LaboratorioInformatica() {
        computers = new boolean[numComputers];
        for (int i = 0; i < numComputers; i++) {
            computers[i] = false;
        }

        computersRequired = new ArrayList<>();
    }

    /**
     * Controlla se tutto il laboratorio è libero
     *
     * @return false se esiste un computer occupato altrimenti true
     */
    private boolean isFree() {
        for (boolean c : computers) {
            if (c) return false;
        }

        return true;
    }

    /**
     * Controlla se tutto il laboratorio è occupato
     *
     * @return false se esiste un computer libero altrimenti true
     */
    private boolean isAllOccupied() {
        for (boolean c : computers) {
            if (!c) return false;
        }

        return true;
    }

    /**
     * Verifica che siano presenti computer liberi non richiesti dai tesisti
     *
     * @return true se esistono computer liberi non richiesti dai tesisti, false altrimenti
     */
    private boolean notRequestedComputer() {
        for (int i = 0; i < numComputers; i++) {
            if (!computers[i] && !computersRequired.contains(i))
                return true;
        }

        return false;
    }

    /**
     * Ottiene l'accesso ad uno o più computer
     *
     * @param duration tempo di permanenza all'interno del laboratorio
     * @param p Persona che richiede l'accesso
     * @throws InterruptedException
     */
    public void getAccess(int duration, Persona p) throws InterruptedException, IllegalArgumentException {
        if (p instanceof Professore) getAccessProfessor(duration, (Professore) p);

        if (p instanceof Studente) getAccessStudent(duration, (Studente) p);

        if (!(p instanceof Professore) && !(p instanceof Studente))
            throw new IllegalArgumentException("Non è un'istanza della classe Studente o Professore");
    }

    /**
     * Un tesista richiede l'accesso ad un computer specifico
     * @param duration tempo di permanenza all'interno del laboratorio
     * @param tesista Tesista che richiede l'accesso
     * @param idComputer computer a cui il tesista vuole accedere
     * @throws InterruptedException
     * @throws IndexOutOfBoundsException
     */
    public void getAccess(int duration, Tesista tesista, int idComputer) throws InterruptedException {
        if (idComputer < 0 || idComputer > numComputers)
            throw new IndexOutOfBoundsException("Invalid computer id");

        synchronized (this) {
            thesisStudentAwaits++; //Il tesista si mette in attesa
            computersRequired.add(idComputer); //Comunica il computer che desidera

            while (professorAwaits > 0 || computers[idComputer]) //Attende che non ci siano più professori in attesa e che il computer desiderato sia libero
                wait();

            //Entra in laboratorio e occupa il computer da lui desiderato
            thesisStudentAwaits--;
            computers[idComputer] = true;
            computersRequired.remove((Integer) idComputer);
        }

        //Esegue le proprie operazioni al computer
        System.out.println("Il tesista-" + tesista.getId() + " deve lavorare per " + duration + "ms");
        Thread.sleep(duration);
        System.out.println("Il tesista-" + tesista.getId() + " ha finito il lavoro di " + duration + "ms");

        synchronized (this) {
            computers[idComputer] = false; //Libera il computer e esce dal laboratorio

            notifyAll(); //Avvisa che è uscito dal laboratorio
        }
    }

    /**
     * Un professore richiede l'accesso a tutto il laboratorio
     *
     * @param duration tempo di permanenza all'interno del laboratorio
     * @param prof Professore che richiede l'accesso
     * @throws InterruptedException
     */
    private void getAccessProfessor(int duration, Professore prof) throws InterruptedException {
        synchronized (this) {
            professorAwaits++; //Si mette in attesa

            while (!this.isFree()) //Attende fino a quando non è libero tutto il laboratorio
                wait();

            //Esegue le proprie operazioni al computer
            System.out.println("Il professore-" + prof.getId() + " deve lavorare per " + duration + "ms");
            //Non rilascio il recinto di mutua esclusione per avere il controllo di tutto il laboratorio
            Thread.sleep(duration);
            System.out.println("Il professore-" + prof.getId() + " ha finito il lavoro di " + duration + "ms");

            professorAwaits--; //Esce dal laboratorio
            notifyAll(); //Avvisa che è uscito dal laboratorio
        }
    }

    /**
     * Uno studente richiede l'accesso ad un computer
     *
     * @param duration - tempo di permanenza all'interno del laboratorio
     * @param studente - Studente che richiede l'accesso
     * @throws InterruptedException
     */
    private void getAccessStudent(int duration, Studente studente) throws InterruptedException {
        int computerId = 0;

        synchronized (this) {
            //Attende fino a quando non ci sono professori o tesisti in attesa e fino a quando non ci sia almeno
            // un computer libero che non è richiesto da un tesista in attesa
            while (professorAwaits > 0 || isAllOccupied() || (thesisStudentAwaits > 0 && !notRequestedComputer()))
                wait();

            //Entra nel laboratorio e cerca un computer libero
            for (int i = 0; i < numComputers; i++) {
                if (!computers[i] && !computersRequired.contains(i)) {
                    computerId = i;
                    break;
                }
            }

            computers[computerId] = true; //Prende possesso di un computer
        }

        //Esegue le proprie operazioni al computer
        System.out.println("Lo studente-" + studente.getId() + " deve lavorare per " + duration + "ms");
        Thread.sleep(duration);
        System.out.println("Lo studente-" + studente.getId() + " ha finito il lavoro di " + duration + "ms");

        synchronized (this) {
            computers[computerId] = false; //Rilascia il computer occupato ed esce dal laboratorio

            notifyAll(); //Avvisa che è uscito dal laboratorio
        }
    }

    public static void main (String []args) {
        if (args.length != 3) {
            System.err.println("Il programma accetta tre parametri: <numero studenti> <numero tesisti> <numero professori>");
            System.exit(1);
        }

        //Parsing degli argomenti
        int students = Integer.parseInt(args[0]);
        int thesisStudents = Integer.parseInt(args[1]);
        int professors = Integer.parseInt(args[2]);

        LaboratorioInformatica lab = new LaboratorioInformatica();

        Thread []tStudents = new Thread[students];
        Thread []tThesis = new Thread[thesisStudents];
        Thread []tProfessors = new Thread[professors];

        //Creazione e avvio di tutti i thread
        for (int i = 0; i < students; i++) {
            int k = ThreadLocalRandom.current().nextInt(0, maxAccesses); //Genero il numero di accessi che farà lo studente i
            tStudents[i] = new Thread(new Studente(i, k, lab));
            tStudents[i].start();
        }

        for (int i = 0; i < thesisStudents; i++) {
            int k = ThreadLocalRandom.current().nextInt(0, maxAccesses); //Genero il numero di accessi che farà il tesista i
            tThesis[i] = new Thread(new Tesista(i, k, lab, numComputers));
            tThesis[i].start();
        }

        for (int i = 0; i < professors; i++) {
            int k = ThreadLocalRandom.current().nextInt(0, maxAccesses); //Genero il numero di accessi che farà il professore i
            tProfessors[i] = new Thread(new Professore(i, k, lab));
            tProfessors[i].start();
        }

        //Attesa terminazione thread
        try {
            for (Thread ts : tStudents) ts.join();

            for (Thread tt : tStudents) tt.join();

            for (Thread tp : tStudents) tp.join();

        } catch (InterruptedException e) {
            for (Thread ts : tStudents) ts.interrupt();

            for (Thread tt : tStudents) tt.interrupt();

            for (Thread tp : tStudents) tp.interrupt();
        }
    }
}

class Persona {
    protected final int numAccesses;
    protected final LaboratorioInformatica lab;
    protected static final int maxDelay = 3000;
    protected final int id;

    public Persona(int id, int numAccesses, LaboratorioInformatica lab) {
        this.id = id;
        this.numAccesses = numAccesses;
        this.lab = lab;
    }

    public int getId() {
        return id;
    }
}

class Studente extends Persona implements Runnable{
    public Studente(int id, int numAccesses, LaboratorioInformatica lab) {
        super(id, numAccesses, lab);
    }

    public void run() {
        try {
            for (int i = 0; i < numAccesses; i++) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, maxDelay)); //Attesa tra due accessi successivi al laboratorio

                lab.getAccess(ThreadLocalRandom.current().nextInt(0, maxDelay), this); //Accesso al laboratorio
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}

class Tesista extends Persona implements Runnable{
    private int numComputers;

    public Tesista(int id, int numAccesses, LaboratorioInformatica lab, int numComputers) {
        super(id, numAccesses, lab);
        this.numComputers = numComputers;
    }

    public void run() {
        try {
            for (int i = 0; i < numAccesses; i++) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, maxDelay)); //Attesa tra due accessi successivi al laboratorio

                int desiredComputer = ThreadLocalRandom.current().nextInt(0, numComputers); //Generazione del computer a cui vuole accedere
                lab.getAccess(ThreadLocalRandom.current().nextInt(0, maxDelay), this, desiredComputer); //Accesso al laboratorio
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}

class Professore extends Persona implements Runnable{
    public Professore(int id, int numAccesses, LaboratorioInformatica lab) {
        super(id, numAccesses, lab);
    }

    public void run() {
        try {
            for (int i = 0; i < numAccesses; i++) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, maxDelay)); //Attesa tra due accessi successivi al laboratorio

                lab.getAccess(ThreadLocalRandom.current().nextInt(0, maxDelay), this); //Accesso al laboratorio
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
