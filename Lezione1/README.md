# Lezione1

## Esercizio 1 - Ciascun programma ha un Thread

Non creerai un Thread poichè la JVM crea sempre un main Thread
1. Crea la classe DatePrinter con un metodo public static void main(String args[]) a cui
aggiungerai il codice per i passi seguenti.
2. Crea un loop infinito con while(true).
3. Nel corpo del loop devono essere eseguite le seguenti azioni: stampare data e ora correnti
e nome del thread in esecuzione (suggerimento: usa Thread.currentThread()) e
successivamente stare in sleep per 2 secondi. Suggerimento: usa
java.util.Calendar https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html per
recuperare data e ora correnti.
Al termine dell’esercizio provare ad aggiungere dopo il ciclo while l’operazione di stampa del
nome del thread in esecuzione.

## Esercizio 2 - Estendi la classe Thread

Crea una classe DatePrinterThread che estenda java.lang.Thread. Aggiungi un metodo public
static void main(String args[]) per creare e avviare una istanza di DatePrinterThread.
Nel metodo public void run() crea un loop infinito con while(true).
Nel corpo del loop devono essere eseguite le seguenti azioni: stampare data e ora correnti e
nome del thread in esecuzione e stare in sleep per 2 secondi (2000 millisecondi).
Nel metodo main(): crea un’istanza di DatePrinterThread and avviala usando start().
Successivamente stampare di nuovo il nome del thread in esecuzione

## Esercizio 3 - Usa l'interfaccia Runnable

1. Crea una classe DatePrinterRunnable che implementi java.lang.Runnable. Aggiungi un
metodo public static void main(String args[]).
2. L’implementazione del metodo run() nella classe DatePrinterRunnable è lo stesso
dell’esercizio precedente.
3. Nel metodo main(): crea un oggetto DatePrinterRunnable e un oggetto di tipo Thread che
prende come parametro del costruttore un oggetto DatePrinterRunnable. Dopo
l’invocazione del metodo start() stampare il nome del thread in esecuzione.

## Assignment - Calcolo di PiGreco

Scrivere un programma che attiva un thread T che effettua il calcolo approssimato di Pi Greco. Il programma principale riceve in input da linea di comando un parametro che indica il grado di accuratezza (accuracy) per il calcolo di Pi Greco ed il tempo massimo di attesa dopo cui il programma principale interompe il thread T.

Il thread T effettua un ciclo infinito per il calcolo di PiGreco, usando la serie di

Gregory-Leibniz ( PiGreco= 4/1 – 4/3 + 4/5 - 4/7 + 4/9 - 4/11 ...).

Il thread esce dal ciclo quando una delle due condizioni seguenti risulta

verificata:

1) il thread è stato interrotto

2) la differenza tra il valore stimato di PiGreco ed il valore Math.PI (della libreria JAVA) è minore di accuracy
