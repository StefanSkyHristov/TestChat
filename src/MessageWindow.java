import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MessageWindow extends Application implements MessageListener{
		private Stage window;
		
		private Button send = new Button("Send");
		
		private ObservableList<Label>messageContainer = FXCollections.observableArrayList();
		private ScrollPane messageView = new ScrollPane();
		private VBox messageLayout = new VBox(5);
		
		private Client client;
		private String otherUser;
		private TextField inputMessage = new TextField();
		private int messageIndex = 0;
		private Pane root = new Pane();
		private BorderPane positionPane;
		
		public MessageWindow(Client client, String otherUser)
		{
			this.client = client;
			this.otherUser = otherUser;
			client.addMessageListener(this);
		}
		
		public void createMessageWindow()
		{
			this.messageView.setPrefSize(400,500);
			this.messageView.setContent(messageLayout);
			
			
			this.send.setOnAction(evt -> {
				String messageBody = inputMessage.getText();
				String userToSend = this.otherUser;
				this.messageContainer.add(new Label(this.client.getUsername() + ": " + messageBody));
				this.client.sendMessage(userToSend, messageBody);
				this.inputMessage.setText("");
				
				if(this.messageIndex % 2 == 0)
				{
					messageContainer.get(this.messageIndex).setAlignment(Pos.BASELINE_LEFT);
				}
				else
				{
					messageContainer.get(this.messageIndex).setAlignment(Pos.BASELINE_RIGHT);
				}
				
				this.messageLayout.getChildren().addAll(this.messageContainer.get(messageIndex));
				this.messageIndex++;
			});
		}
		
	@Override
	public void start(Stage arg0) throws Exception {
		createMessageWindow();
		
		this.positionPane = new BorderPane();
		this.positionPane.setPrefSize(700, 500);
		this.positionPane.setLeft(messageView);
		this.positionPane.setBottom(inputMessage);
		this.positionPane.setCenter(send);
	
		root.getChildren().add(positionPane);
		window = new Stage();
		window.setTitle("Chat window");
		
		Scene scene = new Scene(root,700,500);
		window.setScene(scene);
		window.show();
	}

	@Override
	public void onMessage(String fromUser, String message)
	{
		String messageToAdd = fromUser + " " + message;
		this.messageContainer.add(new Label(messageToAdd));
	}
}
