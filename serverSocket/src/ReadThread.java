import java.net.Socket;

import java.io.DataInputStream;

public class ReadThread extends Thread {
  private Socket clientSocket;
  private DataInputStream input;
  private String message;

  public String getExMessage() {
    return exMessage;
  }

  /**
   * Se viene lanciata un'eccezzione da start() o interrupt()
   */
  private String exMessage;

  public ReadThread(Socket clientSocket){
    this.clientSocket = clientSocket;
    this.exMessage = "false";
  }

  public String getMessage() {
    return message;
  }

  /**
   * Lettura del messaggio ricevuto dal client
   */
  @Override
  public void start() {

    try {
      input = new DataInputStream(clientSocket.getInputStream());
      message = input.readUTF();
    }catch(Exception ex)
    {
      exMessage = ex.getMessage();
    }

  }

  /**
   * Terminazione del thread.
   * Con chiusura dello stream di input e del socket.
   */
  @Override
  public void interrupt() {

    try {
      input.close();
      clientSocket.close();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    super.interrupt();

  }

}