// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param loginID The login ID for client.
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
	this.loginID = loginID;
    this.clientUI = clientUI;
    openConnection();

  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.startsWith("#")) {
    		handleCommand(message);
    	}else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method handles command messages (starting with #)            
   *
   * @param command The command message from the UI.    
   */
  private void handleCommand(String command) throws IOException {
	  if (command.equals("#quit")) {
		  quit();
	  }else if (command.equals("#logoff")) {
		  closeConnection();
	  }else if (command.startsWith("#sethost")) {
		  if (isConnected()) {
			  clientUI.display("Must be logged off to set host.");
		  } else {
			  String[] a = command.split(" ", 2); 
			  setHost(a[1]);
		  }
	  }else if (command.startsWith("#setport")) {
		  if (isConnected()) {
			  clientUI.display("Must be logged off to set port.");
		  } else {
			  String[] a = command.split(" ", 2); 
			  setPort(Integer.parseInt(a[1]));
		  }
	  }else if (command.equals("#login")) {
		  if (!(isConnected())) {
			  openConnection();
		  }
	  }else if (command.equals("#gethost")) {
		  clientUI.display("Host: " + getHost());
	  }else if (command.equals("#getport")) {
		  clientUI.display("Port: " + getPort());
	  }else {
		  clientUI.display("Unrecognized command.");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  

  /**
   * Implements the hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   * 
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down");
	  quit();
  }
  
  /**
	* Implements the hook method called after the connection has been closed. The default
	* implementation does nothing. The method may be overriden by subclasses to
	* perform special processing such as cleaning up and terminating, or
	* attempting to reconnect.
	*/
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection closed");
  }
  
  /**
	* Implements hook method called after a connection has been established. The default
	* implementation does nothing. It may be overridden by subclasses to do
	* anything they wish.
	*/
  @Override
	protected void connectionEstablished() {
	  try {
		sendToServer("#login "+loginID);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
//End of ChatClient class
