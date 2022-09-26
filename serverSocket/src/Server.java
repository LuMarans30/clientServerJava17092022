
import java.net.*;

/**
 * La classe Server gestisce la connessione con un client.
 * Utilizza due thread, uno per la lettura e un altro la scrittura.
 * @author Andrea Marano
 * @version 1.0
 */
public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ReadThread readThread;
    private WriteThread writeThread;

    /**
     * La porta che viene aperta è la numero 5000 per default
     */
    public static final int PORTA = 5000;

    /**
     * Costruttore di default
     * Inizializzazione degli attributi della classe
     */
    public Server() {
        serverSocket = null;
        readThread = null;
        writeThread = null;
    }

    /**
     * Il server ascolta su una porta (di default la porta 5000)
     * @throws Exception se il metodo work() o la creazione del socket fallisce
     */
    public void start() throws Exception {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Il server e' stato avviato ed e' in attesa di connessione");
        work();
    }

    /**
     * Terminazione di tutte le connessioni e stream di dati
     * @throws Exception se la chiusura delle connessione o stream fallisce
     */
    public void stop() throws Exception {

        if(readThread!=null)
            readThread.interrupt();

        if(writeThread!=null)
            writeThread.interrupt();

        serverSocket.close();

        System.exit(0);
    }

    /**
     * Finché il server non riceve il messaggio "exit", legge il messaggio e lo invia al client
     * Se riceve "exit" invia il messaggio "Connessione terminata"
     * @throws Exception se la lettura/scrittura da/a client fallisce
     */
    public void messaggia() throws Exception
    {
        boolean running = true;
        String message;

        while(running) {
            readThread = new ReadThread(clientSocket);
            readThread.start();
            if (!readThread.getExMessage().equals("false"))
                throw new Exception(readThread.getExMessage());

            running = !readThread.getMessage().equals("exit");

            if(!running)
                 message = "Connessione terminata";
            else
                message = readThread.getMessage();

            writeThread = new WriteThread(clientSocket, message);
            writeThread.start();
            if (!writeThread.getExMessage().equals("false"))
                throw new Exception(writeThread.getExMessage());

        }
    }

    /**
     * Accetta la richiesta di connessione da parte del client ed elabora l'input
     * @throws Exception se il metodo stop() lancia un'eccezzione
     */
    public void work() throws Exception {

        clientSocket = serverSocket.accept();

        System.out.println("Il client " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort() + " si e' connesso");

        messaggia();

        try
        {
            stop();
            System.out.println("Il client " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort() + " si e' disconnesso");
        }catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }


    /**
     * Avviamento del server
     * @param args argomenti del main
     */
    public static void main(String[] args)
    {
        Server maranoServer = new Server();

        try
        {
            maranoServer.start();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

}
