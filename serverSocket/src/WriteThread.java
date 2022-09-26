import java.net.Socket;
import java.io.DataOutputStream;

public class WriteThread extends Thread {
  private Socket clientSocket;
  private DataOutputStream output;

  /**
   * Messaggio da inviare al client
   */
  private String message;

  public String getExMessage() {
    return exMessage;
  }

  private String exMessage;

  /**
   * Costruttore parametrico
   * @param clientSocket socket aperto dal client
   * @param message messaggio da inviare al client
   */
  public WriteThread(Socket clientSocket, String message) {
    this.clientSocket = clientSocket;
    this.message = message;
    this.exMessage = "false";
  }

  /**
   * Scrittura del messaggio al client
   */
  @Override
  public void start() {
    try {
      output = new DataOutputStream(clientSocket.getOutputStream());
      output.writeUTF(message);
    } catch (Exception ex) {
      exMessage = ex.getMessage();
    }

  }

  /**
   * Terminazione del thread.
   * Con chiusura dello stream di output e del socket.
   */
  @Override
  public void interrupt() {

    try {
      output.close();
      clientSocket.close();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    super.interrupt();
  }

}
