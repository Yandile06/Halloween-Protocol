package Client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

	public static void main(String[] args) {
		
       launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	
		ClientHandler root= new ClientHandler(primaryStage);
		
		Scene scene = new  Scene(root);
		
		primaryStage.setTitle("HALLOWEEN");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}

}
