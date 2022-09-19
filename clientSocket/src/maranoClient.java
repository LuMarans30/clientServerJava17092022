import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;

import static java.lang.System.exit;

/**
 * La classe maranoClient, sottoclasse di JFrame, è il client GUI che gestisce la connessione da/a un server (di default locale)
 * @author Andrea Marano
 * @version 1.0
 */
public class maranoClient extends JFrame {

    /**
     * Contiene l'indirizzo ip e la porta del server
     */
    private JTextField txtIpPorta;

    /**
     * Contiene l'espressione matematica da inviare al server
     */
    private JTextField txtOp;

    /**
     * Invia i dati al server
     */
    private JButton btnInvio;

    /**
     * Il risultato dell'espressione matematica ricevuto dal server
     */
    private JTextField txtRisultato;

    /**
     * Container che contiene tutti i componenti grafici utilizzati
     */
    private JPanel jPanel1;

    /**
     * Esegue la connessione verso il server utilizzando l'ip e porta specificati da txtIpPorta
     */
    private JButton btnCollega;

    /**
     * Flusso di dati in input (dal server al client)
     */
    private DataInputStream socketInput;

    /**
     * Flusso di dati in output (dal client al server)
     */
    private DataOutputStream socketOutput;

    /**
     * Gestisce la connessione client/server
     */
    private Socket clientSocket;

    /**
     * Apertura della connessione verso l'indirizzo IP e porta specificati in input
     * @param ip l'ip del server
     * @param porta La porta aperta del server
     * @throws IOException se la connessione al server fallisce
     */
    public void start(String ip, int porta) throws IOException {

        clientSocket = new Socket(ip,porta);
    }

    /**
     * Terminazione della connessione, dei relativi stream e chiusura del client
     * @throws IOException se la chiusura delle connessioni o stream fallisce
     */
    public void stop() throws IOException {

        socketInput.close();
        socketOutput.close();
        clientSocket.close();
        exit(0);
    }

    /**
     * Invio dell'operazione da svolgere al server e lettura del risultato
     * @param operazione l'espressione matematica che il server deve risolvere
     * @throws IOException se la lettura/scrittura dei dati da/a il server fallisce o se il metodo stop() lancia un'eccezzione
     */
    public void work(String operazione) throws IOException {

        socketInput = new DataInputStream(clientSocket.getInputStream());
        socketOutput = new DataOutputStream(clientSocket.getOutputStream());

        socketOutput.writeUTF(operazione);
        String msg = socketInput.readUTF();
        txtRisultato.setText(msg);

        if(operazione.toLowerCase(Locale.ROOT).equals("quit"))
        {
            stop();
        }
    }


    /**
     * Costruttore di default
     */
    public maranoClient() {

        btnInvio.setEnabled(false);

        btnInvio.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Invia l'espressione matematica al server tramite un socket
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    if(!txtOp.getText().isEmpty())
                        work(txtOp.getText());
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCollega.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Collegamento al server
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    if(!txtIpPorta.getText().isEmpty()) {
                        String ip = txtIpPorta.getText().split(":")[0];
                        int porta = Integer.parseInt(txtIpPorta.getText().split(":")[1]);
                        start(ip,porta);
                    }

                    btnCollega.setEnabled(false);
                    btnInvio.setEnabled(true);
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Metodo main del client
     * @param args argomenti del metodo main
     */
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        JFrame frame = new maranoClient();
        frame.setTitle("Client");
        frame.setContentPane(new maranoClient().jPanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(480,360));
        frame.setVisible(true);
        frame.setResizable(false);
    }


}
