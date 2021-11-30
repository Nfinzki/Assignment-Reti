# Lezione10

## Esercizio 

* Definire un Server WelcomeServer, che 
  - invia su un gruppo di multicast (welcomegroup*), ad intervalli regolari, un messaggio
di «welcome».
  - attende tra un invio ed il successivo un intervallo di tempo simulato mediante il
metodo sleep().
* Definire un client WelcomeClient che si unisce a welcomegroup e riceve un messaggio di
welcome, quindi termina.


*Ad esempio con indirizzo IP 239.255.1.3

## Assignment - Time Server UDP

Definire un Server TimeServer, che
* invia su un gruppo di multicast dategroup, ad intervalli regolari, la data e
l’ora.
* attende tra un invio ed il successivo un intervallo di tempo simulata
mediante il metodo sleep().
* l’indirizzo IP di dategroup viene introdotto da linea di comando.
* definire quindi un client TimeClient che si unisce a dategroup e riceve,
per dieci volte consecutive, data ed ora, le visualizza, quindi termina.