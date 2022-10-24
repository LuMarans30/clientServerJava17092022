import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

/**
 * La classe Client gestisce la connessione con un server.
 * Utilizza due thread, uno per la lettura e un altro la scrittura.
 * @author Andrea Marano
 * @version 1.0
 */
public class Client extends JFrame {

  private WriteThread writeThread;
  private ReadThread readThread;
  /**
   * Contiene l'indirizzo ip e la porta del server
   */
  private JTextField txtIpPorta;

  /**
   * Contiene il testo da inviare al server
   */
  private JTextField txtMsg;

  /**
   * Invia i dati al server
   */
  private JButton btnInvio;

  /**
   * Il messaggio ricevuto dal server
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
  private JTextField txtUsername;


  /**
   * Gestisce la connessione client/server
   */
  private Socket clientSocket;


  /**
   * Creazione dello stream verso il server
   * @param ip ip del server
   * @param porta porta in ascolto sul server
   * @throws Exception se la creazione del socket fallisce
   */
  public void start(String ip, int porta) throws Exception {
    clientSocket = new Socket(ip, porta);
  }

  /**
   * Chiusura dei thread di lettura/scrittura da/a il server, del socket e dell'applicazione.
   */
  public void stop(){

    if(readThread!=null)
      readThread.interrupt();

    if(writeThread!=null)
      writeThread.interrupt();

    System.exit(0);
  }

  /**
   * Scrittura del messaggio inserito nella TextField txtMsg verso il server e lettura del messaggio dal server con relativa visualizzazione nella TextField txtRisultato.
   * @param messaggio messaggio da inviare al server
   */
  public void work(String messaggio) {

    try
    {
      writeThread = new WriteThread(clientSocket, messaggio);
      writeThread.start();

      if (!writeThread.getExMessage().equals("false"))
        throw new Exception(writeThread.getExMessage());

      readThread = new ReadThread(clientSocket);
      readThread.start();

      if (!readThread.getExMessage().equals("false"))
        throw new Exception(readThread.getExMessage());

      while (readThread.isAlive() && writeThread.isAlive());
      String message = readThread.getMessage();

      txtRisultato.setText(message);

      if(message.isEmpty())
      {
        work(messaggio);
      }

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Costruttore di default
   */
  public Client() {

    btnInvio.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       * Invia il messaggio al server tramite un socket
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        try
        {
          if(!txtMsg.getText().isEmpty())
            work(txtMsg.getText());
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
          if(!txtIpPorta.getText().isEmpty() && !txtUsername.getText().isEmpty()){
            String ip = txtIpPorta.getText().split(":")[0];
            int porta = Integer.parseInt(txtIpPorta.getText().split(":")[1]);
            start(ip,porta);
            work(txtUsername.getText());
            btnCollega.setEnabled(false);
            btnInvio.setEnabled(true);
          }
        }
        catch (Exception ex)
        {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    addWindowListener(new java.awt.event.WindowAdapter() {
      /**
       * Chiusura della finestra del client e chiamata del metodo stop().
       * @param windowEvent evento che si verifica in caso di chiusura della finestra
       */
      @Override
      public void windowClosing(java.awt.event.WindowEvent windowEvent) {
          try
          {
            stop();
          }catch(Exception ex)
          {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
          }
        }
    });
  }

  public static void main(String[] args) {
    FlatDarkLaf.setup();
    JFrame frame = new Client();
    frame.setTitle("Client");
    frame.setContentPane(new Client().jPanel1);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setSize(new Dimension(480,360));
    frame.setVisible(true);
    frame.setResizable(false);
  }
}