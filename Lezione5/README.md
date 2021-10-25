# Lezione5

## Esercizio 1

Scrivere un programma Java che, a partire dal percorso di una directory (es.
"/path/to/dir/"), recupera il contenuto della directory e delle eventuali sottodirectory.
Il programma scrive in un file di nome “directories” il nome delle directory che incontra
e nel file “files” il nome dei file.

## Assignment - File Crawler

Si scriva un programma Java che riceve in input un filepath che individua una directory D e stampa le informazioni del contenuto di quella directory e, ricorsivamente, di tutti i file contenuti nelle sottodirectory di D.

Il programma deve essere strutturato come segue:

* Attiva un thread produttore ed un insieme di k thread consumatori.
* Il produttore comunica con i consumatori mediante una coda.
* Il produttore visita ricorsivamente la directory data e eventualmente tutte le sottodirectory e mette nella coda il nome di ogni directory individuata.
* I consumatori prelevano dalla coda i nomi delle directory e stampano il loro contenuto.

La coda deve essere realizzata con una LinkedList. Ricordiamo che una LinkedList non è una struttura thread-safe. Dalle API Java: "Note that the implementation is not synchronized. If multiple threads access a linked list concurrently, and at least one of the threads modifies the list structurally, it must be synchronized externally".
