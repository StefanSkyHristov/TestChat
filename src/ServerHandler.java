import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler implements Runnable {
	private Socket clientSocket;
	private DatabaseConnection db;
	private Client client;
	private final Server server;
	private List<ServerHandler>listOfHandlers;
	private PrintStream outputStream;
	
	
	public ServerHandler(Socket clientSocket, Server server, DatabaseConnection db)
	{
		this.server = server;
		this.db = db;
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run()
	{
		handleClientCommunication();
	}
	
	private void handleClientCommunication()
	{
		 try 
		 {
			BufferedReader clientInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
			this.outputStream = new PrintStream(clientSocket.getOutputStream());
			
			String messageFromClient = "";
			boolean accepted = false;

			while((messageFromClient = clientInputStream.readLine()) != null)
			{
					if(messageFromClient.equals("Logout"))
					{
						break;
					}
					else if(messageFromClient.startsWith("Login"))
					{
						handleLoginAuthentication(messageFromClient);
						
					}
					else
					{
						System.out.println("Client: " + messageFromClient);
						String serverMsg = serverInput.readLine();
						outputStream.println(serverMsg);
					}
			}
			
			System.out.println("Connection closing...");
			
				clientInputStream.close();
				clientSocket.close();
				System.out.println("Connection closed.");
		 } 
		 catch (IOException e)
		 {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public boolean handleLoginAuthentication(String message)
	{
		boolean authenticated = false;
		String[] messageSplit = message.split(" ");
		String username = messageSplit[1];
		String password = messageSplit[2];
		
		authenticated = db.authenticate(username, password);
		
		if(authenticated)
		{
			System.out.println("User " + username + " has joined the chat!");
			this.listOfHandlers = server.getHandlersList();
			System.out.println("Size is " + listOfHandlers.size());
			String notification = "User " + username + " has joined the conversation!";
			for(ServerHandler handler: listOfHandlers)
			{
				handler.notify(notification);
			}
		}
		else
		{
			System.out.println("Error in login attempt.");
		}
		
		return authenticated;
	}
	
	private void notify(String txtMsg)
	{
		try
		{
			this.outputStream = new PrintStream(clientSocket.getOutputStream());
			this.outputStream.println(txtMsg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
//	public void broadcastMessages(String fromUser, String message)
//	{
//		String defaultUser = "Client";
//		if(fromUser)
//		System.out.println("Client: " + messageFromClient);
//		String serverMsg = serverInput.readLine();
//		outputStream.println(serverMsg);
//	}
}