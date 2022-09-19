
import org.mariuszgromada.math.mxparser.Expression;

import java.net.*;

import java.io.*;
import java.util.Locale;

import static java.lang.System.exit;

/**
 * La classe Server calcola il risultato di un'espressione matematica ricevuta in input dal Client
 * @author Andrea Marano
 * @version 1.0
 */
public class maranoServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream socketInput;
    private DataOutputStream socketOutput;
    /**
     * La porta che viene aperta è la numero 5000 per default
     */
    public static final int PORTA = 5000;

    /**
     * Inizializzazione degli attributi della classe
     */
    public maranoServer() {
        serverSocket = null;
        clientSocket = new Socket();
        socketInput = null;
        socketOutput = null;
    }

    /**
     * Il server ascolta su una porta (di default la porta 5000)
     * @throws Exception se il metodo work() lancia un'eccezzione
     */
    public void start() throws Exception {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Il server e' stato avviato ed e' in attesa di connessione");

        try {
            work();
            System.out.println("Il client " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort() + " si e' connesso");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Terminazione di tutte le connessioni e stream di dati
     * @throws Exception se la chiusura delle connessione o stream fallisce
     */
    public void stop() throws Exception {
        socketInput.close();
        socketOutput.close();
        clientSocket.close();
        serverSocket.close();
        exit(0);
    }

    /**
     * Calcolazione del valore corrispondente all'espressione matematica e invio al client
     * In caso che riceve il comando "exit" termina l'iterazione e invia come risultato "Fine connessione"
     * @throws Exception se il calcolo del risultato o l'invio dei dati al Client fallisce
     */
    public void calcola() throws Exception
    {
        boolean flag=true;

        while(flag)
        {
            socketInput = new DataInputStream(clientSocket.getInputStream());
            socketOutput = new DataOutputStream(clientSocket.getOutputStream());
            String message = socketInput.readUTF(); //es. 5*3
            if(message.toLowerCase(Locale.ROOT).equals("exit"))
                flag=false;
            else
            {
                //parseDouble(String string) throws NumberFormatException
                //eval(String string) throws ScriptException
                Expression expression = new Expression(message);

                double risultato = expression.calculate();
                socketOutput.writeUTF(String.valueOf(risultato));
            }
        }

        socketOutput.writeUTF("Fine connessione");

    }

    /**
     * Accetta la richiesta di connessione da parte del client ed elabora l'input
     * @throws Exception se il metodo stop() lancia un'eccezzione
     */
    public void work() throws Exception {

        clientSocket = serverSocket.accept();

        calcola();

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
        maranoServer maranoServer = new maranoServer();

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
