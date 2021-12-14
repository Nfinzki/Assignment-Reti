import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Congresso implements CongressoInterface{
    private final Map<Integer, List<String>> []programma;

    public Congresso() {
        programma = new Map[3];
        programma[0] = new HashMap<>();
        programma[1] = new HashMap<>();
        programma[2] = new HashMap<>();

        for (int i = 0; i < 12; i++) {
            programma[0].put(i+1, new ArrayList<>());
            programma[1].put(i+1, new ArrayList<>());
            programma[2].put(i+1, new ArrayList<>());
        }
    }

    public int registerSpeaker(int day, int session, String speakerName) throws RemoteException {
        if (speakerName == null) throw new NullPointerException();
        if (day <= 0 || day > 3) return 1;
        if (session <= 0 || session > 12) return 2;

        if (programma[day - 1].get(session).size() < 5) {
            programma[day - 1].get(session).add(speakerName);
            return 0;
        } else {
            return 3;
        }

    }

    public Map<Integer, List<String>>[] getSchedule() throws RemoteException {
        return programma;
    }
}
