import java.io.*;

/*Scrivere un programma Java che, a partire dal percorso di una directory (es.
"/path/to/dir/"), recupera il contenuto della directory e delle eventuali sottodirectory.
Il programma scrive in un file di nome “directories” il nome delle directory che incontra
e nel file “files” il nome dei file.
*/
public class PathExplorer {
    public static void main(String []args) {
        if (args.length != 1) {
            System.err.println("PathExplorer <path>");
            System.exit(1);
        }

        File directory = new File(args[0]);
        if (!directory.isDirectory()) {
            System.err.println("<path> dev'essere una directory");
            System.exit(1);
        }

        File dirFile = new File("directories.txt");
        File fileFile = new File("files.txt");
        try {
            if (!dirFile.createNewFile()) {
                System.err.println("File \"directories\" già esistente");
                System.exit(1);
            }
            if (!fileFile.createNewFile()) {
                System.err.println("File \"files\" già esistente");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Exception caught while creating files");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            PrintWriter writerDir = new PrintWriter(new BufferedOutputStream(new FileOutputStream(dirFile)));
            PrintWriter writerFile = new PrintWriter(new BufferedOutputStream(new FileOutputStream(fileFile)));

            inspectDir(
                    directory,
                    writerDir,
                    writerFile
            );

            writerDir.close();
            writerFile.close();
        } catch (FileNotFoundException e) {
            System.err.println("Uno o più file non sono stati trovati");
            System.exit(1);
        }
    }

    public static void inspectDir(File dir, PrintWriter dirFile, PrintWriter fileFile) {
        for (String path : dir.list()) {
            File file = new File(dir + "\\" + path);

            if (file.isDirectory()) {
                dirFile.println(file.getName());

                inspectDir(file, dirFile, fileFile);
            } else {
                fileFile.println(file.getName());
            }
        }
    }
}
