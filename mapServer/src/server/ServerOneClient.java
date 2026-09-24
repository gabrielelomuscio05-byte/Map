package server;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;

public class ServerOneClient extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Data trainingSet;
    private RegressionTree tree;
    private String tableName;

    public ServerOneClient(Socket s) throws IOException {
        this.socket = s;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object req = in.readObject();
                if (req == null)
                    break;

                int command = (Integer) req;

                switch (command) {
                    case 0: // Acquisizione dati da tabella database
                        tableName = (String) in.readObject();
                        try {
                            trainingSet = new Data(tableName);
                            out.writeObject("OK");
                        } catch (TrainingDataException e) {
                            out.writeObject("Errore acquisizione dati: " + e.getMessage());
                        } catch (Exception e) {
                            out.writeObject("Errore imprevisto: " + e.getMessage());
                        }
                        break;

                    case 1: // Fase di apprendimento dell'albero di regressione
                        try {
                            if (trainingSet == null) {
                                out.writeObject("Nessun training set caricato per l'apprendimento.");
                                break;
                            }
                            tree = new RegressionTree(trainingSet);
                            if (tableName != null && !tableName.isEmpty()) {
                                try {
                                    tree.salva(tableName + ".dmp");
                                } catch (IOException e) {
                                    System.err.println("Avviso: salvataggio automatico fallito: " + e.getMessage());
                                }
                            }
                            out.writeObject("OK");
                        } catch (Exception e) {
                            out.writeObject("Errore durante l'apprendimento: " + e.getMessage());
                        }
                        break;

                    case 2: // Caricamento dell'albero da archivio serializzato
                        String loadFileName = (String) in.readObject();
                        try {
                            try {
                                tree = RegressionTree.carica(loadFileName + ".dmp");
                            } catch (FileNotFoundException | ClassNotFoundException e1) {
                                tree = RegressionTree.carica(loadFileName);
                            }
                            out.writeObject("OK");
                        } catch (Exception e) {
                            out.writeObject("Errore caricamento albero: " + e.getMessage());
                        }
                        break;

                    case 3: // Fase di predizione interattiva
                        if (tree == null) {
                            out.writeObject("Nessun albero disponibile per la predizione.");
                            break;
                        }
                        try {
                            tree.predictClass(in, out);
                        } catch (UnknownValueException e) {
                            out.writeObject(e.getMessage() != null ? e.getMessage() : e.toString());
                        } catch (Exception e) {
                            out.writeObject("Errore durante la predizione: " + e.getMessage());
                        }
                        break;

                    default:
                        out.writeObject("Comando non riconosciuto: " + command);
                        break;
                }
            }
        } catch (EOFException | SocketException e) {
            // Connessione chiusa dal client normalmente
            System.out.println("Client disconnesso: " + socket.getRemoteSocketAddress());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore di comunicazione con client: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                System.err.println("Errore chiusura socket: " + e.getMessage());
            }
        }
    }
}
