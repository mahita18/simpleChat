package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
	String loginKey = "loginID";
	
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  ChatIF serverUI; 

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient (Object msg, ConnectionToClient client){
	
	  
    System.out.println("Message received: " + msg + " from " + client.getInfo(loginKey));
    
    String msgStr = (String)msg;
    
    if (msgStr.startsWith("#login")){
    	if(client.getInfo(loginKey) == null) {
			String[] x = msgStr.split(" ", 2); 
	    	client.setInfo(loginKey, x[1]);
	    	
	    	System.out.println(client.getInfo(loginKey)+ " has logged on.");
	    	this.sendToAllClients(client.getInfo(loginKey)+ " has logged on.");
	    	
	    } else {
	    	try {
				client.sendToClient("Error. Login ID was already made.");
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    } else {
    	this.sendToAllClients(client.getInfo(loginKey)+ "> "+msg);
    }
  }
  
  public void handleMessageFromServerUI(String message){
	  try{
		  if (message.startsWith("#")) {
			  handleCommand(message);
		  }else {
			  System.out.println("SERVER MSG> " + message);
			  this.sendToAllClients("SERVER MSG> " + message);
		  }
	   }
	   catch(IOException e){
	      serverUI.display("Error occurred.");
	    }
	  
  }

	private void handleCommand(String command) throws IOException {
		 if (command.equals("#quit")) {
			 close();
			 System.exit(0);
		  }else if (command.equals("#stop")) {
			  stopListening();
		  }else if (command.startsWith("#close")) {
			  close();
		  }else if (command.startsWith("#setport")) {
			  if (isListening()) {
				  serverUI.display("Must be closed to set port.");
			  } else {
				  String[] a = command.split(" ", 2); 
				  setPort(Integer.parseInt(a[1]));
			  }
		  }else if (command.equals("#start")) {
			  if (isListening()) {
				  serverUI.display("Server was not stopped!.");
			  } else {
				  listen();
			  }
			  
		  }else if (command.equals("#getport")) {
			  serverUI.display("Port: " + getPort());
		  }else {
			  serverUI.display("Unrecognized command.");
		  }
	}
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Implements hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A new client has connected to the server.");
  }

  /**
   * Implements hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
  		System.out.println(client.getInfo(loginKey)+" has disconnected.");
  	}
  
  
  
}
//End of EchoServer class
