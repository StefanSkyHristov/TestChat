import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	private final int portNum;
	private ArrayList<ServerHandler> handlers = new ArrayList<>();;
	private DatabaseConnection db;
	private Client client;
	
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
				this.client = new Client(s.getInetAddress().toString(), portNum);
				this.client.setS(s);
				System.out.println("Client details " + s.getInetAddress().toString() + " " + s.getPort());
				System.out.println("Connection successful!");
				
				ServerHandler handler = new ServerHandler(client, this, db);
				//ServerHandler handler = new ServerHandler(s, this, db); ---->Correct version
				Thread t = new Thread(handler); // We have to initialize the handler as a Thread since
												//just using the default .run() method on "Runnable" does not seem to work
				handlers.add(handler);
				t.start();
			}
		} 
		catch (IOException e)
		{
			System.out.println("Houston, we've got a problem!");
			System.out.println("A client could not connect to the server.");
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
