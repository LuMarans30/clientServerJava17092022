import java.io.File;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * La classe Server gestisce la connessione con più client.
 * Utilizza due thread, uno per la lettura e un altro la scrittura.
 * @author Andrea Marano
 * @version 1.0
 */
public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final Map<String,Socket> clientSocketMap;
    private Socket clientSocket;
    private ReadThread readThread;
    private WriteThread writeThread;
    private String username, message;

    /**
     * La porta che viene aperta è la numero 5000 per default
     */
    public static final int PORTA = 5000;

    /**
     * Costruttore di default
     * Inizializzazione degli attributi della classe
     * @throws Exception se la creazione del socket fallisce
     */
    public Server() throws Exception {
        serverSocket = new ServerSocket(PORTA);
        clientSocketMap = new HashMap<>();
        readThread = null;
        writeThread = null;
    }

    public void run() {
        try {
            messaggia(clientSocket);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Il server ascolta su una porta (di default la porta 5000)
     * @throws Exception se il metodo work() o la creazione del socket fallisce
     */
    public void start() throws Exception {
        System.out.println("Il server e' stato avviato ed e' in attesa di connessione");
        work();
    }

    /**
     * Terminazione di tutte le connessioni e stream di dati
     * @throws Exception se la chiusura delle connessione o stream fallisce
     */
    public void stop() throws Exception {
        clientSocket.close();
    }

    /**
     * Finché il server non riceve il messaggio "exit", legge il messaggio e lo invia al client
     * Se riceve "exit" invia il messaggio "Connessione terminata"
     * @throws Exception se la lettura/scrittura da/a client fallisce
     */
    public void messaggia(Socket clientSocket) throws Exception
    {
        boolean running = true;

        while (running) {
            readThread = new ReadThread(clientSocket);
            readThread.start();
            if (!readThread.getExMessage().equals("false"))
                throw new Exception(readThread.getExMessage());

            message = readThread.getMessage();

            if (message.equals("exit")) {
                message = "Connessione terminata";
                running = false;
            }

            clientSocketMap.forEach((key, value) -> {
                if (value.equals(clientSocket)) {
                    username = key;
                }
            });

            if(!message.isEmpty())
                System.out.println("Utente: " + username + " - Messaggio: " + message);

            writeThread = new WriteThread(clientSocket, message);
            writeThread.start();
            if (!writeThread.getExMessage().equals("false"))
                throw new Exception(writeThread.getExMessage());
        }

        stop();
    }

    /**
     * Accetta la richiesta di connessione da parte del client ed elabora l'input
     * @throws Exception se il metodo stop() lancia un'eccezzione
     */
    public void work() throws Exception{

        while(true) {
            clientSocket = serverSocket.accept();
            readThread = new ReadThread(clientSocket);
            readThread.start();
            if (!readThread.getExMessage().equals("false"))
                throw new Exception(readThread.getExMessage());

            username = readThread.getMessage();

            clientSocketMap.put(username, clientSocket);

            writeThread = new WriteThread(clientSocket, "Benvenuto " + username);

            writeThread.start();
            if (!writeThread.getExMessage().equals("false"))
                throw new Exception(writeThread.getExMessage());

            System.out.println("Il client " + username + " si e' connesso");

            Thread thread = new Thread(this);
            thread.start();
        }
    }


    /**
     * Avviamento del server
     * @param args argomenti del main
     */
    public static void main(String[] args)
    {
        try
        {
            Server maranoServer = new Server();
            maranoServer.start();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

}
