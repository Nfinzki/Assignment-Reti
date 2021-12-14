import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static int port = 42069;
    public static void main(String []args) {
        if (args.length > 1) {
            System.err.println("Usage: Server <port>");
            System.exit(1);
        }
        if (args.length == 1) port = Integer.parseInt(args[0]);

        try {
            Congresso congresso = new Congresso();
            CongressoInterface stub = (CongressoInterface) UnicastRemoteObject.exportObject(congresso, 0);

            LocateRegistry.createRegistry(port);
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("CONGRESSO-SERVER", stub);

            System.out.println("Server ready");
        } catch (RemoteException e) {
            System.err.println(e);
            System.exit(1);
        }

    }
}
