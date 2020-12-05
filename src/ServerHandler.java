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
	
	public ServerHandler(Client client, Server server, DatabaseConnection db)
	{
		this.server = server;
		this.db = db;
		this.client = client;
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
			BufferedReader clientInputStream = new BufferedReader(new InputStreamReader(this.client.getS().getInputStream()));
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
			this.outputStream = new PrintStream(this.client.getS().getOutputStream());
			
			String messageFromClient = "";
			boolean accepted = false;

			while((messageFromClient = clientInputStream.readLine()) != null)
			{

				if(messageFromClient.startsWith("Login"))
				{
					handleLoginAuthentication(messageFromClient);
					startUserChatSession();
					break;
				}
				else
				{
					System.out.println("Client: " + messageFromClient);
					String serverMsg = serverInput.readLine();
					outputStream.println(serverMsg);
				}
			}
			
			System.out.println("Connection closing...");
			
				this.client.getS().close();
				clientInputStream.close();
				outputStream.close();
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
			this.client.setUsername(username);
			System.out.println("User " + username + " has joined the chat!");
			
			this.listOfHandlers = server.getHandlersList();
			System.out.println("Size is " + listOfHandlers.size());
			
			String notification = "User " + username + " has joined the conversation!";
			for(ServerHandler handler: listOfHandlers)
			{
				if(handler.getClient().getUsername() != this.getClient().getUsername())
				{
					handler.notify(notification);
				}
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
			this.outputStream = new PrintStream(this.client.getS().getOutputStream());
			this.outputStream.println(txtMsg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void logOff()
	{
		server.removeHandler(this);
		List<ServerHandler> listOfHandlers = server.getHandlersList();
		
		for(ServerHandler handler: listOfHandlers)
		{
			//outputStream.println(this.getClient().getUsername() + " has logged off the chat.");---> original:working
			notify(this.getClient().getUsername() + " has logged off the chat.");
		}
		try
		{
			this.client.getS().close();
		}
		catch (IOException e)
		{
			System.out.println("Error in LogOff");
			e.printStackTrace();
		}
	}
	
	private void startUserChatSession()
	{
		try
		{
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader clientInputStream = new BufferedReader(new InputStreamReader(this.client.getS().getInputStream()));
			String messageFromClient = "";
			String serverMsg;
			while((messageFromClient = clientInputStream.readLine()) != null)
			{
				if(messageFromClient.startsWith("Logout"))
				{
					logOff();
					break;
				}
				else
				{
					System.out.println(this.client.getUsername() + ": " + messageFromClient);
					serverMsg = serverInput.readLine();
					outputStream.println(serverMsg);
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("User is no longer available.");
			e.printStackTrace();
		}
	}
	
	public Client getClient()
	{
		return this.client;
	}
}