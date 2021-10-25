# Lezione4

## Esercizio 1

Scrivere un programma in cui alcuni thread generano e consumano numeri interi da una risorsa
condivisa, chiamata Dropbox, avente capacità 1.
- Nella classe Dropbox, il buffer avente un solo elemento è realizzato tramite una variabile
intera. La classe offre un metodo take per consumare il numero e svuotare il buffer e un
metodo put per inserire un nuovo valore se il buffer è vuoto.
- Definire un task Consumer il cui metodo costruttore prende in ingresso un valore booleano
(true per consumare valori pari e false per valori dispari) e il riferimento ad un’istanza di
Dropbox. Nel metodo run invoca il metodo take sull’istanza di Dropbox.
- Definire un task Producer il cui metodo costruttore prende in ingresso il riferimento ad
un’istanza di Dropbox. Nel metodo run genera un intero in modo random, nel range
[0,100), e invoca il metodo put sull’istanza di Dropbox.
- Definire una classe contenente il metodo main. Nel main viene creata un’istanza di
Dropbox. Vengono quindi creati 2 oggetti di tipo Consumer (uno true e uno false) e un
oggetto di tipo Producer, ciascuno eseguito da un thread distinto. 

## Assignment - Laboratorio di Informatica con monitor

Risolvere il problema della simulazione del Laboratorio di Informatica, assegnato nella lezione precedente, utilizzando il costrutto del Monitor
