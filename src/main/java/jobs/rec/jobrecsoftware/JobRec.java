package jobs.rec.jobrecsoftware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JobRec extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JobRec.class.getResource("login.fxml"));

        Parent loader = fxmlLoader.load();

        LoginController.setStage(stage);

        Scene scene = new Scene(loader);
        stage.setTitle("Job Rec | Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}