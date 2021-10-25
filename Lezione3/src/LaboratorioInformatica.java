import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* Il laboratorio di Informatica del Polo Marzotto è utilizzato da tre tipi di utenti (studenti, tesisti e professori)
ed ogni utente deve fare una richiesta al tutor per accedere al laboratorio. I computers del laboratorio sono numerati
da 1 a 20. Le richieste di accesso sono diverse a seconda del tipo dell'utente:
I professori accedono in modo esclusivo a tutto il laboratorio, poichè hanno necessità di utilizzare tutti i
computers per effettuare prove in rete.
I tesisti richiedono l'uso esclusivo di un solo computer, identificato dall'indice i, poiché su quel computer è
istallato un particolare software necessario per lo sviluppo della tesi.
Gli studenti richiedono l'uso esclusivo di un qualsiasi computer.
I professori hanno priorità su tutti nell'accesso al laboratorio, i tesisti hanno priorità sugli studenti.
Nessuno però può essere interrotto mentre sta usando un computer.
Scrivere un programma JAVA che simuli il comportamento degli utenti e del tutor. Il programma riceve in ingresso
il numero di studenti, tesisti e professori che utilizzano il laboratorio ed attiva un thread per ogni utente.
Ogni utente accede k volte al laboratorio, con k generato casualmente. Simulare l'intervallo di tempo che intercorre
tra un accesso ed il successivo e l'intervallo di permanenza in laboratorio mediante il metodo sleep.
Il tutor deve coordinare gli accessi al laboratorio. Il programma deve terminare quando tutti gli utenti hanno
completato i loro accessi al laboratorio.
*/
public class LaboratorioInformatica {
    private static final int maxAccesses = 10;
    private static final int numComputers = 20;

    private final Computer []computers;

    private final Lock mutexLab;
    private final Condition profCondition;
    private final Condition [] thesisCondition;
    private final Condition studentCondition;

    private int professorAwaits = 0;
    private int thesisStudentAwaits = 0;

    public LaboratorioInformatica() {
        computers = new Computer[numComputers];
        for (int i = 1; i <= numComputers; i++) {
            computers[i-1] = new Computer(i);
        }

        mutexLab = new ReentrantLock();
        profCondition = mutexLab.newCondition();
        studentCondition = mutexLab.newCondition();

        thesisCondition = new Condition[numComputers];
        for (int i = 0; i < numComputers; i++) {
            thesisCondition[i] = mutexLab.newCondition();
        }
    }

    /**
     * Controlla se tutto il laboratorio è libero
     *
     * @return false se esiste un computer occupato altrimenti true
     */
    private boolean isFree() {
        for (Computer c : computers) {
            if (c.isOccupied()) return false;
        }

        return true;
    }

    /**
     * Controlla se tutto il laboratorio è occupato
     *
     * @return false se esiste un computer libero altrimenti true
     */
    private boolean isAllOccupied() {
        for (Computer c: computers) {
            if (!c.isOccupied()) return false;
        }

        return true;
    }

    /**
     * Controlla se uno specifico computer è occupato
     *
     * @param seat computer da controllare
     * @return true se il computer è occupato, false altrimenti
     */
    private boolean seatIsOccupied(int seat) {
        return computers[seat].isOccupied();
    }

    /**
     * Ottiene l'accesso ad uno o più computer
     *
     * @param duration tempo di permanenza all'interno del laboratorio
     * @param p Persona che richiede l'accesso
     * @throws InterruptedException
     */
    public void getAccess(int duration, Persona p) throws InterruptedException {
        if (p instanceof Professore) getAccessProfessor(duration, (Professore) p);

        if (p instanceof Studente) getAccessStudent(duration, (Studente) p);
    }

    /**
     * Un tesista ottiene l'accesso ad un computer specifico
     * @param duration tempo di permanenza all'interno del laboratorio
     * @param tesista Tesista che richiede l'accesso
     * @param idComputer computer a cui il tesista vuole accedere
     * @throws InterruptedException
     */
    public void getAccess(int duration, Tesista tesista, int idComputer) throws InterruptedException {
        if (idComputer < 0 || idComputer > numComputers) throw new IndexOutOfBoundsException("Invalid computer id");

        try {
            mutexLab.lock();
            thesisStudentAwaits++;

            while (professorAwaits > 0 || seatIsOccupied(idComputer)) //Attende che non ci siano più professori in attesa e che il computer desiderato sia libero
                thesisCondition[idComputer].await();

            thesisStudentAwaits--;

            mutexLab.unlock();

            //Esegue le proprie operazioni al computer
            System.out.println("Il tesista-" + tesista.getId() + " deve lavorare per " + duration + "ms");
            computers[idComputer].occupy(duration);
            System.out.println("Il tesista-" + tesista.getId() + " ha finito il lavoro di " + duration + "ms");

            mutexLab.lock();
        } finally {
            //Avvisa i professori di aver terminato le proprie operazioni
            profCondition.signal();
            //Avvisa i tesisti in attesa del computer idComputer-esimo di aver terminato le proprie operazioni
            thesisCondition[idComputer].signal();
            //Avvisa i professori di aver terminato le proprie operazioni
            studentCondition.signal();
            mutexLab.unlock();
        }
    }

    /**
     * Ottiene l'accesso a tutto il laboratorio
     *
     * @param duration tempo di permanenza all'interno del laboratorio
     * @param prof Professore che richiede l'accesso
     * @throws InterruptedException
     */
    private void getAccessProfessor(int duration, Professore prof) throws InterruptedException {
        try {
            mutexLab.lock();
            professorAwaits++;

            while (!this.isFree()) //Attende fino a quando non è libero tutto il laboratorio
                profCondition.await();

            professorAwaits--;

            System.out.println("Il professore-" + prof.getId() + " deve lavorare per " + duration + "ms");
            //Faccio la sleep senza occupare tutto il laboratorio perché si trova nel recinto di mutua esclusione
            //del laboratorio quindi non ci sono rischi di race condition
            Thread.sleep(duration);
            System.out.println("Il professore-" + prof.getId() + " ha finito il lavoro di " + duration + "ms");
        } finally {
            //Avvisa altri professori che il laboratorio è libero
            profCondition.signal();
            //Avvisa tutti i tesisti che il laboratorio è libero
            for (Condition thesisCond : thesisCondition)
                thesisCond.signal();
            //Avvisa tutti gli studenti che il laboratorio è libero
            studentCondition.signalAll();
            mutexLab.unlock();
        }
    }

    /**
     * Ottiene l'accesso ad un computer
     *
     * @param duration - tempo di permanenza all'interno del laboratorio
     * @param studente - Studente che richiede l'accesso
     * @throws InterruptedException
     */
    private void getAccessStudent(int duration, Studente studente) throws InterruptedException {
        try {
            mutexLab.lock();

            while (professorAwaits > 0 || thesisStudentAwaits > 0 || isAllOccupied()) //Attende fino a quando non ci sono professori o tesisti in attesa e fino a quando non ci sia almeno un posto libero
                studentCondition.await();

            for (Computer c : computers) {
                if (!c.isOccupied()) {
                    mutexLab.unlock();
                    System.out.println("Lo studente-" + studente.getId() + " deve lavorare per " + duration + "ms");
                    c.occupy(duration);
                    System.out.println("Lo studente-" + studente.getId() + " ha finito il lavoro di " + duration + "ms");
                    mutexLab.lock();
                    return;
                }
            }

        } finally {
            //Avvisa tutti di aver liberato un computer
            profCondition.signal();
            for (Condition thesisCond : thesisCondition)
                thesisCond.signal();
            studentCondition.signal();
            mutexLab.unlock();
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
            tThesis[i] = new Thread(new Tesista(i, k, lab));
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

class Computer {
    private final int id;
    private final Lock lock;
    private boolean occupied;

    public Computer(int id) {
        this.id = id;
        this.lock = new ReentrantLock();
        this.occupied = false;
    }

    /**
     * Controlla se il computer è occupato
     *
     * @return true se il computer è occupato, false altrimenti
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Occupa un computer
     *
     * @param awaitTime tempo di occupazione del computer
     * @throws InterruptedException
     */
    public void occupy(int awaitTime) throws InterruptedException{
        try {
            lock.lock();
            occupied = true;
            Thread.sleep(awaitTime);
        } finally {
            occupied = false;
            lock.unlock();
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
            return;
        }
    }
}

class Tesista extends Persona implements Runnable{
    public Tesista(int id, int numAccesses, LaboratorioInformatica lab) {
        super(id, numAccesses, lab);
    }

    public void run() {
        try {
            for (int i = 0; i < numAccesses; i++) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, maxDelay)); //Attesa tra due accessi successivi al laboratorio

                int desiredComputer = ThreadLocalRandom.current().nextInt(0, 20); //Generazione del computer a cui vuole accedere
                lab.getAccess(ThreadLocalRandom.current().nextInt(0, maxDelay), this, desiredComputer); //Accesso al laboratorio
            }
        } catch (InterruptedException e) {
            return;
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
            return;
        }
    }
}
