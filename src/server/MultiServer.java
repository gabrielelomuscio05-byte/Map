package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {

    private int PORT = 8080;

    public MultiServer(int port) {
        this.PORT = port;
        run();
    }

    private void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("MultiServer avviato in ascolto sulla porta " + PORT + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuova connessione accettata: " + socket.getRemoteSocketAddress());
                try {
                    new ServerOneClient(socket);
                } catch (IOException e) {
                    System.err.println("Errore istanziazione ServerOneClient: " + e.getMessage());
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Errore ServerSocket: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed())
                    serverSocket.close();
            } catch (IOException e) {
                System.err.println("Errore chiusura ServerSocket: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Porta non valida specificata, verrà usata la porta predefinita 8080.");
            }
        }
        new MultiServer(port);
    }
}
