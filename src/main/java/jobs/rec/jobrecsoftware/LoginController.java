package jobs.rec.jobrecsoftware;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button btnLogIn;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private TextField tfEmail;

    @FXML
    private Text txtMessage;

    @FXML
    private Hyperlink hlSignUp;

    //Stage stage;

    IDs iDs = IDs.getInstance();

    public static Stage stageController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

       /* // Loading the application stage for scene navigation
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) tfEmail.getScene().getWindow();
            }
        });
*/



        try {
            DatabaseDao.createAdmin();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Listens to a email textfield inputs and verify if email already exists
        /*tfEmail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if(!DatabaseDao.emailExist(newValue))
                    {
                        txtMessage.setFill(Color.RED);
                        txtMessage.setText("Email Does Not Exists");
                    }else{
                        txtMessage.setText("");
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });*/


        // This triggers when a user clicks on the login button
      btnLogIn.setOnAction(e ->{
          try {
              if(DatabaseDao.login(tfEmail.getText(),pfPassword.getText())){

                            if(DatabaseDao.getUserType(tfEmail.getText()).equals("A")){
                                try {

                                    Parent root = FXMLLoader.load(getClass().getResource("admin_dashboard.fxml"));

                                    LoginController.stageController.setTitle("Admin Panel");

                                    Scene scene = new Scene(root);
                                    //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                                    LoginController.stageController.setScene(scene);


                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }else if(DatabaseDao.getUserType(tfEmail.getText()).equals("R")){
                                iDs.setUserID(DatabaseDao.getUserID(tfEmail.getText()));
                                Parent root = FXMLLoader.load(getClass().getResource("representative_dashboard.fxml"));

                                LoginController.stageController.setTitle("Company Representative Panel");

                                Scene scene = new Scene(root);
                                //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                                LoginController.stageController.setScene(scene);
                            }else{
                                iDs.setUserID(DatabaseDao.getUserID(tfEmail.getText()));
                                Parent root = FXMLLoader.load(getClass().getResource("student_dashboard.fxml"));

                                LoginController.stageController.setTitle("Student Panel");

                                Scene scene = new Scene(root);
                                //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                                LoginController.stageController.setScene(scene);

                            }



              }else{
                  txtMessage.setFill(Color.RED);
                  txtMessage.setText("Email or password is wrong");
              }
          } catch (ClassNotFoundException ex) {
              throw new RuntimeException(ex);
          } catch (SQLException ex) {
              throw new RuntimeException(ex);
          } catch (IOException ex) {
              throw new RuntimeException(ex);
          }
      });

      hlSignUp.setOnAction(e ->{
          try {

              Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));

              LoginController.stageController.setTitle("Sign Up");

              Scene scene = new Scene(root);
              //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
              LoginController.stageController.setScene(scene);


          } catch (IOException ex) {
              throw new RuntimeException(ex);
          }
      });

    }


    public static void setStage(Stage stage){

        LoginController.stageController = stage;

    }


}
