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
 * La classe Form1, sottoclasse di JFrame, è il client GUI che gestisce la connessione da/a un server (di default locale)
 */
public class Form1 extends JFrame {

    private JTextField txtIpPorta;
    private JTextField txtOp;
    private JButton btnInvio;
    private JTextField txtRisultato;
    private JPanel jPanel1;
    private JButton btnCollega;
    private DataInputStream socketInput;
    private DataOutputStream socketOutput;
    private Socket clientSocket;

    /**
     * Apertura della connessione verso l'indirizzo IP e porta specificati in input
     * @param ip
     * @param porta
     * @throws IOException
     */
    public void start(String ip, int porta) throws IOException {

        clientSocket = new Socket(ip,porta);
    }

    /**
     * Terminazione della connessione, dei relativi stream e chiusura del client
     * @throws IOException
     */
    public void stop() throws IOException {

        socketInput.close();
        socketOutput.close();
        clientSocket.close();
        exit(0);
    }

    /**
     * Invio dell'operazione da svolgere al server e lettura del risultato
     * @param operazione
     * @throws IOException
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
    public Form1() {

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

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        JFrame frame = new Form1();
        frame.setTitle("Client");
        frame.setContentPane(new Form1().jPanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(480,360));
        frame.setVisible(true);
        frame.setResizable(false);
    }


}
