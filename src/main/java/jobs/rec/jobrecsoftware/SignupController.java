package jobs.rec.jobrecsoftware;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML
    private Button btnSignUp;

    @FXML
    private ComboBox<String> cbxGender;

    @FXML
    private ComboBox<String> cbxUserType;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private PasswordField pfPassword1;

    @FXML
    private TextField tfUserEmail;

    @FXML
    private TextField tfUserFullName;

    @FXML
    private Text txtMessage;

    Stage stage;

    @FXML
    private Hyperlink hlLogin;

    IDs iDs = IDs.getInstance();

    private String [] genders = {"Male","Female","Other"};
    private String [] types = {"Student","Company Representative"};

    private final ObservableList<String> genderList = FXCollections.observableArrayList(genders);
    private final ObservableList<String> typeList = FXCollections.observableArrayList(types);

    private String gender = null;
    private String type = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Loading the application stage for scene navigation
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) tfUserEmail.getScene().getWindow();
            }
        });

        // Populating the gender combo box
        cbxGender.setItems(genderList);

        // Populating the userType combo box
        cbxUserType.setItems(typeList);


        // Listening to a change in the gendere combo box and assigning a value to gender variable
        cbxGender.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(cbxGender.getSelectionModel().getSelectedIndex() == 0){
                    gender = "M";
                }else  if(cbxGender.getSelectionModel().getSelectedIndex() == 1){
                    gender = "F";
                }else if(cbxGender.getSelectionModel().getSelectedIndex() == 2){
                    gender = "N";
                }

                System.out.println(gender);
            }
        });


        // Listening to a change in the user type combo box and assigning a value to type variable
        cbxUserType.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(cbxUserType.getSelectionModel().getSelectedIndex() == 0){
                    type = "S";
                }else  if(cbxUserType.getSelectionModel().getSelectedIndex() == 1){
                    type = "R";
                }


            }
        });


        // Listens to a email textfield inputs and verify if email already exists
        tfUserEmail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if(DatabaseDao.emailExist(newValue))
                    {
                        txtMessage.setFill(Color.RED);
                        txtMessage.setText("Email already exists");
                    }else{
                        txtMessage.setText("");
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });



        // This triggers when a user clicks on the login button
        btnSignUp.setOnAction(e ->{
            if(tfUserFullName.getText().isEmpty() || tfUserEmail.getText().isEmpty() || pfPassword.getText().isEmpty() ||
            pfPassword1.getText().isEmpty() || gender == null || type == null){
                txtMessage.setFill(Color.RED);
                txtMessage.setText("Empty field(s) detected!!");
                return;
            }

            if( !pfPassword.getText().equals(pfPassword1.getText())){
                txtMessage.setFill(Color.RED);
                txtMessage.setText("Passwords does not match!!");
                return;
            }

            try {
                if(DatabaseDao.emailExist(tfUserEmail.getText())){
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Email Already Exists");
                    return;
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }


            try {
                int row = DatabaseDao.insertUser2(tfUserFullName.getText(),tfUserEmail.getText(),pfPassword.getText(),gender,type);
                if(row > 0){
                    try {

                        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

                        stage.setTitle("Login");

                        Scene scene = new Scene(root);
                        //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                        stage.setScene(scene);


                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }else{
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Couldn't add you");
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });

        hlLogin.setOnAction(e ->{
            try {

                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

                stage.setTitle("Login");

                Scene scene = new Scene(root);
                //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                stage.setScene(scene);


            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }






}
