import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	private String hostName;
	private int portNum;
	private Socket s;
	private PrintStream outputStream;
	private BufferedReader inputStream;
	private BufferedReader serverReader;
	private String username;
	public ArrayList<UserStatusListener>listeners = new ArrayList<>();
	public ArrayList<MessageListener> messageListener = new ArrayList<>();
	
	public Client(String hostName, int portNum)
	{
		this.hostName = hostName;
		this.portNum = portNum;
	}
	
	public boolean connectToServer()
	{
		try 
		{
			this.s = new Socket(hostName, portNum);
			this.inputStream = new BufferedReader(new InputStreamReader(System.in)); //Read from the Socket
			this.outputStream = new PrintStream(s.getOutputStream()); //Write to the socket
			this.serverReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			return true;
		}
		catch (UnknownHostException e) 
		{
			System.out.println(e);
			System.out.println("The host name is unkown or unspecified");
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			System.out.println(e);
			System.out.println("There has been a connection error");
			e.printStackTrace();
		}
		return false;
	}
	
	public void startClientSession()
	{
		Thread starter = new Thread() {
			
			@Override
			public void run()
			{
				String messageToServer = "";
				
				while(!messageToServer.equals("Logout"))
				{
					try 
					{
						messageToServer = inputStream.readLine();
						outputStream.println(messageToServer);
						String serverMsgs = serverReader.readLine();
						System.out.println(serverMsgs);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				
				try 
				{
					inputStream.close();
					outputStream.close();
					s.close();
					System.out.println("User " + getUsername() + " logged out.");
				} 
				catch (IOException e)
				{
					System.out.println(e);
					e.printStackTrace();
				}
			}
		};
		starter.run();
	}
	
	public void readIncomingMessages()
	{
		Thread reader = new Thread() {
			
			@Override
			public void run()
			{	
				try {
					String serverResponse = "";
					while((serverResponse = serverReader.readLine()) != null)
					{
			
						if(serverResponse.startsWith("Online: "))
						{
							callOnlineStatusListener(serverResponse);
						}
						else if(serverResponse.startsWith("Offline: "))
						{
							callOfflineStatusListener(serverResponse);
						}
						else if(serverResponse.startsWith("Message from "))
						{
							callOnReceivedMessageListener(serverResponse);
						}
					}
				} 
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				
				try 
				{
					inputStream.close();
					outputStream.close();
					s.close();
					System.out.println("User " + getUsername() + " logged out.");
				} 
				catch (IOException e)
				{
					System.out.println(e);
					e.printStackTrace();
				}
			}
		};
		reader.start();
	}
	
	public void sendMessage(String user, String message)
	{
		outputStream.println("msg " + user + " " + message);
	}
	
	public void logOffUser()
	{
		outputStream.println("Logout");
	}
	
	private void callOnlineStatusListener(String msg)
	{
		String[] msgTokens = msg.split(" ");
		String user = msgTokens[1];
		for(UserStatusListener statusListener: listeners)
		{
			statusListener.online(user);
		}
	}
	
	private void callOfflineStatusListener(String msg)
	{
		String[] msgTokens = msg.split(" ");
		String user = msgTokens[1];
		for(UserStatusListener statusListener: listeners)
		{
			statusListener.offline(user);
		}
	}
	
	private void callOnReceivedMessageListener(String msg)
	{
		String[] msgTokens = msg.split(" ", 4);
		String user = msgTokens[2];
		String message = msgTokens[3];
		
		for(MessageListener listener: messageListener)
		{
			listener.onMessage(user, message);
		}
	}
	
	public boolean entryLogin()
	{
		boolean login = true;
		Scanner sc = new Scanner(System.in);
		try
		{
			this.outputStream = new PrintStream(s.getOutputStream());
			
			System.out.println("Please type in your username:");
			String username = sc.next();
			
			System.out.println("Please type in your password:");
			String password = sc.next();
			
			this.outputStream.println("Login " + username + " " + password);
			this.setUsername(username);
			
			String response = serverReader.readLine();
			if(response.equals("Login successful: " + getUsername()))
			{
				login = true;
			}
			else
			{
				login = false;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return login;
	}
	
	public void sendMsg(String msg)
	{
		this.outputStream.println("msg " + msg);
	}
	
	public boolean GUILogin(String usernameInput, String passwordInput)
	{
		boolean login = false;
		try
		{
			this.outputStream = new PrintStream(s.getOutputStream());
			this.outputStream.println("Login " + usernameInput + " " + passwordInput);
			
			String response = serverReader.readLine();
			if(response.equals("Login successful: " + usernameInput))
			{
				login = true;
				this.setUsername(usernameInput);
				readIncomingMessages();
			}
			else
			{
				login = false;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return login;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}
	
	public int getPortNum()
	{
		return portNum;
	}
	
	public void setPortNum(int portNum)
	{
		this.portNum = portNum;
	}
	
	public Socket getS() {
		return s;
	}
	
	public void setS(Socket s)
	{
		this.s = s;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public static void main(String[] args)
	{
		Client client = new Client("localhost", 12304);
		client.addStatusListener(new UserStatusListener() {

			@Override
			public void online(String user)
			{
				System.out.println("ONLINE: " + user);
			}

			@Override
			public void offline(String user)
			{
				System.out.println("OFFLINE: " + user);
			}
			
		});
		
		client.addMessageListener(new MessageListener() {

			@Override
			public void onMessage(String fromUser, String message)
			{
				System.out.println(fromUser + " " + message);
			}
			
		});
		
		if(client.connectToServer())
		{
			System.out.println("A new client has connected!");
			if(client.entryLogin())
			{
				//client.startClientSession();
				//client.sendMessage("john","sup brotha!");
				client.readIncomingMessages();
			}
			else
			{
				System.err.println("Could not Login.");
			}
		}
		else
		{
			System.err.println("Connection failed.");
		}
	}
	
	public PrintStream getOutputStream()
	{
		return this.outputStream;
	}
	
	public void addStatusListener(UserStatusListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeStatusListener(UserStatusListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public void addMessageListener(MessageListener listener)
	{
		this.messageListener.add(listener);
	}
	
	public void removeMessageListener(MessageListener listener)
	{
		this.messageListener.remove(listener);
	}
}
