import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String []args) {
        int port = Integer.parseInt(args[0]);
        try {
            Event event = new Event();

            EventInterface stub = (EventInterface) UnicastRemoteObject.exportObject(event, 0);

            LocateRegistry.createRegistry(port);
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("EVENT-SERVER", stub);

            System.out.println("Server ready");
        } catch (RemoteException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
