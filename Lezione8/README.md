# Lezione8

## Esercizio 1

Scrivere un programma che legge il file di testo random.txt (in allegato) e scrive su un file di output
la frequenza dei caratteri alfabetici contenuti nel testo. Lettura e scrittura devono essere
effettuate utilizzando NIO, in particolare le classi FileChannel e ByteBuffer.
Non è necessario distinguere fra caratteri maiuscoli o minuscoli.
Nota: dato il file in ingresso, potete assumere che ciascun byte che leggete dal buffer rappresenti
un carattere.

## Assignment - Gestione conti correnti

Creare un file contenente oggetti che rappresentano i conti correnti di una banca. Ogni conto corrente contiene il nome del correntista e una lista di movimenti. I movimenti registrati per un conto corrente sono relativi agli ultimi 2 anni, quindi possono essere molto numerosi.

Per ogni movimento vengono registrati la data e la causale del movimento. L'insieme delle causali possibili è fissato: Bonifico, Accredito, Bollettino, F24, PagoBancomat.

Rileggere il file e trovare, per ogni possibile causale, quanti movimenti hanno quella causale.

Progettare un'applicazione che attiva un insieme di thread. Uno di essi legge dal file gli oggetti "conto corrente" e li passa, uno per volta, ai thread presenti in un thread pool. Ogni thread calcola il numero di occorrenze di ogni possibile causale all'interno di quel conto corrente ed aggiorna un contatore globale.

Alla fine il programma stampa per ogni possibile causale il numero totale di occorrenze.

Utilizzare:

1. NIO per creare il file.
2. NIO oppure IO classico per rileggere il file.
3. Il formato JSON (e quindi la libreria GSON) per la serializzazione.
