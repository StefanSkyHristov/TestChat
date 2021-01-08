
public class MainServer {

	public static void main(String[] args) {
		Server server = new Server(12304);
		server.run();
	}
}