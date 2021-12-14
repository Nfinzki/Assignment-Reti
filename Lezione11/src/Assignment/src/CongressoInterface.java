import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface CongressoInterface extends Remote {
    int registerSpeaker(int day, int session, String speakerName) throws RemoteException;

    Map<Integer, List<String>>[] getSchedule() throws RemoteException;
}
