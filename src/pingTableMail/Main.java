package pingTableMail;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pingTableMail.controllers.MainController;

import java.io.IOException;

public class Main extends Application {
	 private Parent root;

	@Override
	public void start(Stage primaryStage) throws IOException, RuntimeException, InterruptedException {

		root = FXMLLoader.load(getClass().getResource("../resources/fxml/main.fxml"));
		primaryStage.setTitle("PING v1.3");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
