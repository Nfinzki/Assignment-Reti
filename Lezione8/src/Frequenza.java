/*
Scrivere un programma che legge il file di testo random.txt (in allegato) e scrive su un file di output
la frequenza dei caratteri alfabetici contenuti nel testo. Lettura e scrittura devono essere
effettuate utilizzando NIO, in particolare le classi FileChannel e ByteBuffer.
Non è necessario distinguere fra caratteri maiuscoli o minuscoli.
Nota: dato il file in ingresso, potete assumere che ciascun byte che leggete dal buffer rappresenti
un carattere.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class Frequenza {
    private static String inputFile = "random.txt";
    private static String outputFile = "frequenze.txt";
    private static final int bufferSize = 1024;

    public static void main(String []args) {
        //Controllo del corretto utilizzo
        if (args.length > 2) {
            System.err.println("Usage Frequenza [inputFile] [outputFile]");
            System.exit(1);
        }

        //Parsing degli argomenti
        switch (args.length) {
            case 2: outputFile = args[1];
            case 1: inputFile = args[0];
                    break;
        }

        //Inizializzazione struttura per registrare le frequenze
        Map<Character, Integer> frequency = new HashMap<>();

        //Apertura channel
        try (FileInputStream fSrc = new FileInputStream(inputFile);
             FileChannel src = fSrc.getChannel();
             FileOutputStream fDst = new FileOutputStream(outputFile);
             FileChannel dst = fDst.getChannel()) {

            //Creazione buffer
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            //Lettura caratteri dal file e scrittura nel buffer
            while (src.read(buffer) != -1) {
                //Flip del buffer per permetterne la lettura
                buffer.flip();

                //Lettura di tutti i caratteri presenti nel buffer
                while(buffer.hasRemaining()) {
                    char readChar = (char) buffer.get(); //Lettura carattere

                    //Controlla la presenza di caratteri che non siano lettere
                    if (!Character.isLetter(readChar)) continue;

                    readChar = Character.toLowerCase(readChar); //Converte la lettera in minuscolo

                    Integer freq = frequency.get(readChar); //Recupera la frequenza della lettera
                    if (freq == null) {
                        frequency.put(readChar, 1); //Lettera non ancora presente nella Map, inserzione
                    } else {
                        frequency.put(readChar, ++freq); //Aggiornamento frequenza
                    }
                }

                //Imposta il buffer per la prossima scrittura
                buffer.clear();
            }

            //Scrittura delle frequenze sul file di output
            for (var elem : frequency.entrySet()) {
                String line = elem.getKey() + " -> " + elem.getValue() + "\n"; //Costruzione stringa da scrivere

                //Imposta il buffer per la scrittura
                buffer.clear();
                buffer.put(line.getBytes()); //Scrittura sul buffer

                buffer.flip(); //Flip del buffer per permettere la lettura
                //Lettura byte dal buffer e scrittura sul file
                while (buffer.hasRemaining()) dst.write(buffer);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File non trovato: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Errore di IO: " + e.getMessage());
            System.exit(1);
        }
    }
}
