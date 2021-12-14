import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Event implements EventInterface{
    private final List<String> userRegistered;

    public Event() {
        userRegistered = new ArrayList<>();
    }


    public void addUser(String name) throws RemoteException {
        if (name == null) throw new NullPointerException();

        userRegistered.add(name);
    }

    public List<String> getUserList() throws RemoteException {
        return userRegistered;
    }
}
