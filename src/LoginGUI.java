import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginGUI extends Application implements UserStatusListener {
	
	private Stage window;
	private BorderPane baseCanvas;
	private AnchorPane leftBase;
	private AnchorPane rightBase;
	private TextField usernameField;
	private PasswordField userPassword;
	private VBox textFields;
	private Button loginButton;
	private final String APP_NAME = "Login Form";
	private Client client;
	
	private Pane root = new Pane();
	private ScrollPane container = new ScrollPane();
	private VBox userBox = new VBox(5);
	private ObservableList<String>names = FXCollections.observableArrayList();
	private ListView<String> lv = new ListView<>(names);
	
	public LoginGUI()
	{
		this.client = new Client("localhost", 12304);
		this.client.addStatusListener(this);
	}
	public void createForm()
	{	
		baseCanvas = new BorderPane();
		baseCanvas.setPrefSize(700, 500);
		
		this.leftBase = new AnchorPane();
		this.rightBase = new AnchorPane();
		this.leftBase.setPrefSize(350, 500);
		this.rightBase.setPrefSize(415, 500);
		
		this.leftBase.setStyle("-fx-background-color: #04d9ff");
		
		this.usernameField = new TextField();
		this.usernameField.setPromptText("Type in your username here...");
		this.usernameField.setStyle("-fx-background-color: transparent");
		this.usernameField.setStyle("-fx-border-color: #1CF5C0");
		
		this.userPassword = new PasswordField();
		this.userPassword.setPromptText("Type in your password here...");
		this.userPassword.setStyle("-fx-background-color: transparent");
		this.userPassword.setStyle("-fx-border-color: #1CF5C0");
		
		this.loginButton = new Button("Login");
		this.loginButton.setStyle("-fx-background-color: #14B1DF");
		
		this.textFields = new VBox();
		textFields.setSpacing(10);
		textFields.getChildren().addAll(usernameField, userPassword, loginButton);
		this.rightBase.getChildren().add(textFields);
		textFields.setLayoutX(100);
		textFields.setLayoutY(200);
		
		
		this.baseCanvas.setLeft(leftBase);
		this.baseCanvas.setRight(rightBase);
		
		Scene scene = new Scene(baseCanvas,700,500);
		window.setScene(scene);
		window.show();
		
	
		this.loginButton.setOnAction(e -> {
			handleLoginAction();
		});
		
		this.usernameField.setOnKeyPressed(new EventHandler<KeyEvent>()
		{

			@Override
			public void handle(KeyEvent evt) {
				if(evt.getCode().equals(KeyCode.ENTER))
				{
					handleLoginAction();
				}
			}
		});
		
		this.userPassword.setOnKeyPressed(new EventHandler<KeyEvent>()
		{

			@Override
			public void handle(KeyEvent evt)
			{
				if(evt.getCode().equals(KeyCode.ENTER))
				{
					handleLoginAction();
				}
			}
		});
	}
	
	public void handleLoginAction()
	{
		String usernameInput = this.usernameField.getText();
		String userPasswordInput = this.userPassword.getText();
		if(usernameInput.isEmpty() || userPasswordInput.isEmpty())
		{
			JOptionPane.showMessageDialog(null,"You have a missing Credential.","Invalid Input",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			
			if(client.connectToServer())
			{
				System.out.println("A new client has connected!");
				if(client.GUILogin(usernameInput, userPasswordInput))
				{
					createUsernamesList(usernameInput);
					window.close();
					
					this.lv.setOnMouseClicked(evt -> {
						System.out.println("Works!");
						if(evt.getClickCount() == 2 && !names.isEmpty())
						{
							 String user = this.lv.getSelectionModel().getSelectedItem();
							 MessageWindow messageWindow = new MessageWindow(client, user);
							 try
							 {
								messageWindow.start(new Stage());
							 }
							 catch (Exception e1)
							 {
								System.out.println("Ooops!");
								e1.printStackTrace();
							 }
						}
					});
				}
				else
				{
					System.err.println("Login failed");
				}
			}
			else
			{
				System.err.println("Connection failed.");
			}
		}
	}
	
	private void createUsernamesList(String loggedInUser)
	{
		Scene scene;
		Stage stage = new Stage();
		stage.setTitle("Users Online");
		this.container.setPrefSize(400,500);
		this.container.setContent(userBox);
		this.root.getChildren().addAll(container);
		scene = new Scene(root,400,500);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(evt -> {
			removeUserFromUserBox(loggedInUser);
			this.client.logOffUser();
		});
		this.userBox.getChildren().add(lv);
		
		addUserToUserBox(loggedInUser);
		
	}
	
	private void addUserToUserBox(String user)
	{
		// The "Platform" class is called as without that, every time a user is added to the 
		// online users list, an exception is thrown.
		Platform.runLater(()->this.names.add(user));
	}
	
	private void removeUserFromUserBox(String user)
	{
		Platform.runLater(() -> this.names.remove(user));
	}
	
	public String getUserLogin()
	{
		return this.client.getUsername();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle(APP_NAME);
		createForm();	
	}
	
	@Override
	public void online(String user)
	{
		addUserToUserBox(user);
	}
	
	@Override
	public void offline(String user)
	{
		removeUserFromUserBox(user);
	}
}
