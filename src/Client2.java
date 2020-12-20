
public class Client2 {

	public static void main(String[] args) {
		Client client = new Client("localhost", 8999);
		if(client.connectToServer())
		{
			System.out.println("A new client has connected!");
			client.entryLogin();
			client.startClientSession();
		}
		else
		{
			System.err.println("Connection failed.");
		}

	}

}
