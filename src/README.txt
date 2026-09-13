MAP5 - Esercitazione 5: RTTI e Serializzazione

Struttura:
- src/data: classi del training set e attributi
- src/tree: albero di regressione, nodi discreti/continui ed eccezioni
- src/utility: Keyboard.java fornito dal docente
- src/MainTest.java: menu apprendimento/caricamento e prediction
- prova.dat: dataset con attributi discreti
- provaC.dat: dataset con attributo continuo
- servo.dat: dataset originale

Compilazione dalla cartella map5_solution:
  javac -d out src/data/*.java src/tree/*.java src/utility/*.java src/MainTest.java

Esecuzione:
  java -cp out MainTest

Nel menu:
1 = apprende da <nome>.dat e serializza in <nome>.dmp
2 = carica l'albero da <nome>.dmp

Per il test MAP5 usare come nome file: provaC

Verifiche effettuate:
- compilazione Java 21 riuscita;
- provaC produce le regole X=A AND Y<=2.0, X=A AND Y>2.0, X=B;
- prediction dei tre rami restituisce 1.0, 1.5 e 10.0;
- scelta fuori range genera UnknownValueException;
- salva/carica tramite serializzazione verificati;
- prova.dat continua a produrre lo stesso output discreto di MAP4.
