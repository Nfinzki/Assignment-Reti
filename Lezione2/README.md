# Lezione2

## Esercizio 1

Nella sala biglietteria di una stazione sono presenti 5 emettitrici automatiche dei biglietti. Nella sala non
possono essere presenti più di 10 persone in attesa di usare le emettitrici.
Scrivere un programma che simula la situazione sopra descritta.
* La sala della stazione viene modellata come una classe JAVA. Uno dopo l’altro arrivano 50
viaggiatori (simulare un intervallo di 50 ms con Thread.sleep).
* ogni viaggiatore viene simulato da un task, la prima operazione consiste nello stampare
“Viaggiatore {id}: sto acquistando un biglietto”, aspettare per un intervallo di tempo random tra 0 e
1000 ms e poi stampa “Viaggiatore {id}: ho acquistato il biglietto”.
* I task vengono assegnati a un numero di thread pari al numero delle emettitrici
* Il rispetto della capienza massima della sala viene garantita dalla coda gestita dal thread. I
viaggiatori che non possono entrare in un certo istante perché la capienza massima è stata
raggiunta abbandonano la stazione (il programma main stampa quindi “Traveler no. {i}: sala
esaurita”.
* Suggerimento: usare un oggetto ThreadPoolExecutor in cui il numero di thread è pari al numero
degli sportelli

## Esercizio 2

Estendi il programma dell’Esercizio 1 gestendo la terminazione del threadpool.
Dopo l’arrivo dell’ultimo viaggiatore e l’invio del corrispondente task al threadpool, terminare il threadpool.

## Esercizio 3

Scrivere un programma che calcola le potenze di un numero n (esempio n=2) da n
2 a n
50 e restituisce come
risultato la somma delle potenze, ovvero:
Result = n
2 + n
3 + … + n
50
* Creare una classe Power di tipo Callable che riceve come parametri di ingresso il numero n e un
intero (l’esponente), stampa “Esecuzione {n}^{esponente} in {idthread}” e restituisce il risultato
dell’elevamento a potenza (usare la funzione Math.pow() di Java
https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#pow-double-double-)
* Creare una classe che nel metodo public static void main(String args[]) crea un threadpool e gli
passa i task Power.
* I risultati restituiti dai task vengono recuperati e sommati e il risultato della somma viene
stampato (usare una struttura dati, es. ArrayList per memorizzare gli oggetti di tipo Future
restituiti dal threadpool in corrispondenza dell’invocazione del metodo submit).

## Assignement - Ufficio postale con threadpool

Simulare il flusso di clienti in un ufficio postale che ha 4 sportelli. Nell'ufficio esiste:

* un'ampia sala d'attesa in cui ogni persona può entrare liberamente. Quando entra, ogni persona prende il numero dalla numeratrice e aspetta il proprio turno in questa sala.
* una seconda sala, meno ampia, posta davanti agli sportelli, in cui si può entrare solo a gruppi di k persone
* una persona si mette quindi prima in coda nella prima sala, poi passa nella seconda sala.

Ogni persona impiega un tempo differente per la propria operazione allo sportello. Una volta terminata l'operazione, la persona esce dall'ufficio.

Scrivere un programma in cui:

* l'ufficio viene modellato come una classe JAVA, in cui viene attivato un ThreadPool di dimensione uguale al numero degli sportelli
* la coda delle persone presenti nella sala d'attesa è gestita esplicitamente dal programma
* la seconda coda (davanti agli sportelli) è quella gestita implicitamente dal ThreadPool
* ogni persona viene modellata come un task, un task che deve essere assegnato ad uno dei thread associati agli sportelli
* si preveda di far entrare tutte le persone nell'ufficio postale, all'inizio del programma

Facoltativo: prevedere il caso di un flusso continuo di clienti e la possibilità che l'operatore chiuda lo sportello stesso dopo che in un certo intervallo di tempo non si presentano clienti al suo sportello.
