# Lezione3

## Esercizio 1

Scrivere un programma in cui un contatore viene aggiornato da 20 scrittori e il suo valore letto e
stampato da 20 lettori.
1. Creare una Classe Counter che offre i metodi increment() e get() per incrementare e
recuperare il valore di un contatore. Vedi esempio di struttura di una classe Counter
(non-thread safe) in allegato
2. Definire un task Writer che implementa Runnable e nel metodo run invoca il metodo
increment di un oggetto Counter
3. Definire un task Reader che implementa Runnable e nel metodo run invoca il metodo get
di un oggetto Counter e lo stampa
4. Definire una classe contenente il metodo main. Nel main viene creata un’istanza di
Counter. Vengono quindi creati 20 oggetti di tipo Writer e 20 oggetti di tipo Reader (a cui
viene passato il riferimento all’oggetto counter nel costruttore). I task vengono quindi
assegnati ad un threadpool (inviare al pool prima i writer e poi i reader) (suggerimento:
usare un CachedThreadPool).
5. Estendere la classe Counter fornita usando un oggetto di tipo ReentrantLock per garantire
l’accesso in mutua esclusione alle sezioni critiche.
6. Estendere la classe Counter usando al posto di ReentrantLock delle Read/Write Lock e
confrontare l’intervallo di tempo richiesto dal threadpool per completare i task in questo
caso col caso precedente (usare System.currentTimeMillis() per recuperare l’ora corrente,
potete prendere un primo timestamp prima del ciclo di creazione dei task e il secondo
timestamp dopo la terminazione del threadpool).
7. (opzionale) Sostituire il threadpool di tipo CachedThreadPool con un FixedThreadPool, al
variare del numero di thread (es. 1 ,2, 4) verificare l’intervallo di tempo richiesto dal
threadpool per completare i task.

## Assignement - Gestione Laboratorio Informatica

Il laboratorio di Informatica del Polo Marzotto è utilizzato da tre tipi di utenti (studenti, tesisti e professori) ed ogni utente deve fare una richiesta al tutor per accedere al laboratorio. I computers del laboratorio sono numerati da 1 a 20. Le richieste di accesso sono diverse a seconda del tipo dell'utente:
1. I professori accedono in modo esclusivo a tutto il laboratorio, poichè hanno necessità di utilizzare tutti i computers per effettuare prove in rete.
2. I tesisti richiedono l'uso esclusivo di un solo computer, identificato dall'indice i, poiché su quel computer è installato un particolare software necessario per lo sviluppo della tesi.
3. Gli studenti richiedono l'uso esclusivo di un qualsiasi computer.

I professori hanno priorità su tutti nell'accesso al laboratorio, i tesisti hanno priorità sugli studenti. Nessuno però può essere interrotto mentre sta usando un computer.
Scrivere un programma JAVA che simuli il comportamento degli utenti e del tutor. Il programma riceve in ingresso il numero di studenti, tesisti e professori che utilizzano il laboratorio ed attiva un thread per ogni utente. Ogni utente accede k volte al laboratorio, con k generato casualmente. Simulare l'intervallo di tempo che intercorre tra un accesso ed il successivo e l'intervallo di permanenza in laboratorio mediante il metodo sleep. Il tutor deve coordinare gli accessi al laboratorio. Il programma deve terminare quando tutti gli utenti hanno completato i loro accessi al laboratorio.
