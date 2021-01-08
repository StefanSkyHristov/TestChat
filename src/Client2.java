
public class Client2 {

	public static void main(String[] args) {
		Client client = new Client("localhost", 12304);
		if(client.connectToServer())
		{
			System.out.println("A new client has connected!");
			client.entryLogin();
			//client.startClientSession();
			client.readIncomingMessages();
		}
		else
		{
			System.err.println("Connection failed.");
		}

	}

}
