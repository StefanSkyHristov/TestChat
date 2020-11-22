import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	private String hostName;
	private int portNum;
	private Socket s;
	private PrintStream outputStream;
	private BufferedReader inputStream;
	private BufferedReader serverReader;
	private String username;
	
	public Client(String hostName, int portNum)
	{
		this.hostName = hostName;
		this.portNum = portNum;
		
		try 
		{
			this.s = new Socket(hostName, portNum);
			this.inputStream = new BufferedReader(new InputStreamReader(System.in)); //Read from the Socket
			this.outputStream = new PrintStream(s.getOutputStream()); //Write to the socket
			this.serverReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
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
		
		String messageToServer = "";
		
		while(!messageToServer.equals("LOGOUT"))
		{
			try 
			{
				messageToServer = inputStream.readLine();
				outputStream.println(messageToServer);
				String serverMsgs = serverReader.readLine();
				System.out.println("Server: " + serverMsgs);
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
			System.out.println("Connection closed.");
		} 
		catch (IOException e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
//	public void getServerMessages()
//	{
//		try
//		{	
//			BufferedReader serverReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			String serverMsg = serverReader.readLine();
//			if(!serverMsg.equals("Logout"))
//			{
//				System.out.println("Server: " + serverMsg);
//			}
//			
//		} 
//		catch (IOException e)
//		{
//			System.out.println("Message from server is not received.");
//			e.printStackTrace();
//		}
//	}
	
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
	public static void main(String[] args)
	{
		Client client = new Client("localhost", 8978);
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}
}
