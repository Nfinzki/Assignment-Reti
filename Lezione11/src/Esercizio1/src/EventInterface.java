import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EventInterface extends Remote {
    void addUser(String name) throws RemoteException;

    List<String> getUserList() throws RemoteException;
}
