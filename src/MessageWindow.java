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

public class MessageWindow extends Application{
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
		}
		
		public void createMessageWindow()
		{
			this.messageView.setPrefSize(400,500);
			this.messageView.setContent(messageLayout);
			
			
			this.send.setOnAction(evt -> {
				String messageBody = inputMessage.getText();
				String userToSend = this.client.getUsername();
				
				this.messageContainer.add(new Label(messageBody));
				this.client.sendMsg(userToSend + " " + messageBody);
				
				if(this.messageIndex % 2 == 0)
				{
					messageContainer.get(messageIndex).setAlignment(Pos.BASELINE_LEFT);
				}
				else
				{
					messageContainer.get(messageIndex).setAlignment(Pos.BASELINE_RIGHT);
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

}
