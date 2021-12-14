import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.util.List;

public class Client {
    public static void main(String []args) {
        EventInterface serverObject;
        Remote remoteObject;

        if (args.length < 2) {
            System.err.println("Usage: port client");
        }

        int port = Integer.parseInt(args[0]);

        try {
            Registry registry = LocateRegistry.getRegistry(port);
            remoteObject = registry.lookup("EVENT-SERVER");
            serverObject = (EventInterface) remoteObject;

            serverObject.addUser(args[1]);
            List<String> userList = serverObject.getUserList();

            for (String user : userList)
                System.out.println(user);
        } catch (RemoteException | NotBoundException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
