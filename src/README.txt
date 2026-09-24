MAP6 - Esercitazione 6: JDBC e Socket Client-Server

Struttura del progetto (in 'src'):
- src/data: classi del training set e attributi (Data, Attribute, ContinuousAttribute, DiscreteAttribute, TrainingDataException)
- src/database: classi di accesso e modellazione del DB relazionale (DbAccess, TableData, Column, TableSchema, Example, EmptySetException, DatabaseConnectionException)
- src/server: architettura server multithread e socket (MultiServer, ServerOneClient, UnknownValueException)
- src/tree: albero di regressione, nodi discreti/continui (RegressionTree, ContinuousNode, DiscreteNode, LeafNode, Node, SplitNode, UnknownValueException)
- src/map7Client: classe client per la connessione al server via socket (MainTest)
- src/utility: Keyboard.java fornito dal docente
- src/lib: driver JDBC mysql-connector-java-8.0.17.jar
- src/MainTest.java: test standalone che acquisisce da tastiera il nome della tabella del database
- src/map6.sql: script SQL per la creazione del database MapDB, utente MapUser e tabella provaC

Progetti Eclipse distinti:
- mapClient: contiene src/map7Client/MainTest.java e src/utility/Keyboard.java
- mapServer: contiene tutti i package server, data, database, tree, utility, MainTest e lib/mysql-connector-java-8.0.17.jar

Configurazione Database MySQL:
1. Eseguire lo script SQL contenuto in 'src/map6.sql':
   - Database: MapDB
   - Utente: MapUser (password: map) con privilegi SELECT su MapDB.*
   - Tabella: provaC (colonne X varchar(10), Y float(5,2), C float(5,2))

Compilazione da terminale:
  javac -cp "src;src/lib/mysql-connector-java-8.0.17.jar" src/data/*.java src/database/*.java src/server/*.java src/tree/*.java src/utility/*.java src/map7Client/*.java src/*.java

Esecuzione:
1. Modalità Standalone (test locale con DB):
   java -cp "src;src/lib/mysql-connector-java-8.0.17.jar" MainTest
   (Digitare il nome tabella: provaC)

2. Modalità Client-Server distribuita:
   - Avvio Server:
     java -cp "src;src/lib/mysql-connector-java-8.0.17.jar" server.MultiServer 8080
   - Avvio Client (da altro terminale o macchina remota):
     java -cp "src" map7Client.MainTest localhost 8080

Funzionamento del Client:
- Scelta [1]: invia comando 0 con nome tabella (es. provaC) per acquisizione dati, seguito da comando 1 per apprendimento e salvataggio automatico dell'albero in <tabella>.dmp.
- Scelta [2]: invia comando 2 per caricamento dell'albero serializzato dall'archivio <tabella>.dmp.
- Fase di Predizione: invia comando 3; riceve interrogazioni "QUERY", invia le risposte dell'utente (rami dell'albero), e riceve infine "OK" con la classe predetta (oppure messaggio di errore se l'input non è valido).
- Ripetizione con 'y'/'n'.
