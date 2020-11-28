import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	private final int portNum;
	private ArrayList<ServerHandler> handlers = new ArrayList<>();;
	private DatabaseConnection db;
	
	public Server(int portNum)
	{
		this.portNum = portNum;
	}

	@Override
	public void run() 
	{	
		try
		{
			ServerSocket serverS = new ServerSocket(portNum);
			while(true)
			{
				System.out.println("Server started successfully.");
				System.out.println("Waiting for client request...");
				
				
				this.db = new DatabaseConnection("jdbc:mysql://localhost:3306/javaserver", "root", "");
				
				Socket s = serverS.accept();
				System.out.println("Connection successful!");
				
				ServerHandler handler = new ServerHandler(s, this, db);
				Thread t = new Thread(handler); // We have to initialize the handler as a Thread since
												//just using the default .run() method on "Runnable" does not seem to work
				handlers.add(handler);
				t.start();
			}
		} 
		catch (IOException e)
		{
			System.out.println("Houston, we've got a problem!");
			System.out.println("Connection failed...");
			e.printStackTrace();
		}
	}
	
	public int getPortNum()
	{
		return portNum;
	}
	
	public List<ServerHandler> getHandlersList()
	{
		return this.handlers;
	}
}
