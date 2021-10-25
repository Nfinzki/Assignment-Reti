# Lezione6

## Esercizio 1

Scrivere un programma JAVA che implementi un server che apre una serversocket su una porta e sta in
attesa di richieste di connessione.

Quando arriva una richiesta di connessione, il server accetta la connessione, trasferisce al client un file e poi
chiude la connessione.
Ulteriori dettagli:
- Il server gestisce una richiesta per volta
- Il server invia sempre lo stesso file, usate un file di testo

Per il client potete usare telnet. Qui sotto un esempio di utilizzo:

## Assignment - HTTP-based file transfer

Scrivere un programma Java che implementi un server HTTP che gestisca richieste di trasferimento di file di diverso tipo (es. immagini jpeg, gif) provenienti da un browser Web.

* Il server sta in ascolto su una porta nota al client (es. 6789).
* Il server gestisce richieste HTTP di tipo GET alla request URL `http://localhost:port/filename`.
* Le connessioni possono essere non persistenti.
* Usare le classi `Socket` e `ServerSocket` per sviluppare il programma server.
* Per inviare al server le richieste, utilizzare un qualsiasi browser. In alternativa, se avete un sistema Unix-based (oppure il WSL su Windows) potete utilizzare [cURL](https://curl.se/) da terminale o [wget](https://www.gnu.org/software/wget/).
