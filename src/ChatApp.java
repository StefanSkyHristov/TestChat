import javafx.application.Application;
import javafx.stage.Stage;

public class ChatApp extends Application {
	private LoginGUI loginForm;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.loginForm = new LoginGUI();
		loginForm.start(new Stage());
		
	}
	
	public static void main (String[] args) {
		launch(args);
	}
}
