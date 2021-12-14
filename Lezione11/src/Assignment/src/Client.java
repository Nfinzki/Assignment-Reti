import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static int port = 42069;
    public static void main(String []args) {
        if (args.length > 1) {
            System.err.println("Usage: Client <port>");
            System.exit(1);
        }
        if (args.length == 1) port = Integer.parseInt(args[0]);

        try {
            Registry registry = LocateRegistry.getRegistry(port);
            CongressoInterface serverObject = (CongressoInterface) registry.lookup("CONGRESSO-SERVER");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.equals("exit")) break;

                String []splittedInput = input.split(" ");
                if (splittedInput[0].equals("add")) {
                    if (splittedInput.length != 4) {
                        System.err.println("'add' usage: add 'day' 'session' 'speaker name'");
                        continue;
                    }

                    switch (serverObject.registerSpeaker(
                            Integer.parseInt(splittedInput[1]),
                            Integer.parseInt(splittedInput[2]),
                            splittedInput[3])) {
                        case 1 -> System.err.println("Invalid day: 0 <= day < 3");
                        case 2 -> System.err.println("Invalid session: 0 < session <= 12");
                        case 3 -> System.err.println("This schedule is full for day: " + splittedInput[1]);
                    }
                } else if (splittedInput[0].equals("show")) {
                    Map<Integer, List<String>> []schedule = serverObject.getSchedule();
                    printSchedule(schedule);
                } else {
                    System.err.println(splittedInput[0] + " isn't a valid command");
                }
            }

        } catch (RemoteException | NotBoundException e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    private static void printSchedule(Map<Integer, List<String>> []programma) {
        String output = "";
        for (int i = 0; i < 3; i++) {
            output += "Day: " + (i+1) + "\n";
            for (int j = 0; j < 5; j++)
                output += "\tIntervento " + (j+1);
            output += "\n";

            for (int j = 0; j < 12; j++) {
                output += "Sessione " + (j+1);
                output += "\t";

                for (String s : programma[0].get(j+1))
                    output += "\t" + s;

                output += "\n";
            }
            output += "\n";
        }
        System.out.println(output);
    }
}
