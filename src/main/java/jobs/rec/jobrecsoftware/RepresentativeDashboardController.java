package jobs.rec.jobrecsoftware;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RepresentativeDashboardController implements Initializable {

    @FXML
    private BorderPane bp;

    @FXML
    private Button btnInfo;

    @FXML
    private Button btnJobAds;

    @FXML
    private Button btnJobApps;

    @FXML
    private Button btnLogOut;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Text txtCompanyName;

    @FXML
    private Text txtCompanyName2;

    @FXML
    private Text txtEmail;

    @FXML
    private Text txtGender;

    @FXML
    private Text txtRepName;

    @FXML
    private Text txtType;
    @FXML
    private VBox vbox;

    @FXML
    void editClicked(ActionEvent event) throws SQLException, ClassNotFoundException {

        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();




        if(buttonId.equals("name")){
            TextInputDialog textInputDialog1 = new TextInputDialog();
            textInputDialog1.setTitle("User Full Name");
            textInputDialog1.setHeaderText("Enter your name");
            textInputDialog1.setContentText("Full Name: ");

            Optional<String> result1 = textInputDialog1.showAndWait();
            if(result1.isPresent()){
                txtRepName.setText("Representative Name: "+result1.get());
                DatabaseDao.updateName(iDs.getUserID(),result1.get());
            }
        }else if(buttonId.equals("gender")){
            List<String> choices = Arrays.asList("Male", "Female","Other");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Male", choices);
            dialog.setTitle("Gender Input");
            dialog.setHeaderText("Please select your gender:");
            dialog.setContentText("Gender:");

            // Show the dialog and wait for a response.
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String gender = result.get();
                txtGender.setText("Gender: " + gender);
                DatabaseDao.updateGender(iDs.getUserID(),gender);
            }

        }else if(buttonId.equals("email")){
            TextInputDialog textInputDialog1 = new TextInputDialog();
            textInputDialog1.setTitle("User Email");
            textInputDialog1.setHeaderText("Enter your email");
            textInputDialog1.setContentText("Email: ");
            Optional<String> result1 = textInputDialog1.showAndWait();
            if(result1.isPresent()){
                txtEmail.setText("Email: "+result1.get());
                DatabaseDao.updateEmail(iDs.getUserID(),result1.get());
            }
        }else{
            TextInputDialog textInputDialog1 = new TextInputDialog();
            textInputDialog1.setTitle("Company Name");
            textInputDialog1.setHeaderText("Enter your company's name");
            textInputDialog1.setContentText("Company name: ");
            Optional<String> result1 = textInputDialog1.showAndWait();
            if(result1.isPresent()){
                txtCompanyName.setText(result1.get());
                txtCompanyName2.setText("Company Name: "+result1.get());
                DatabaseDao.insertUpdate(iDs.getUserID(),result1.get());
            }
        }
    }

    IDs iDs = IDs.getInstance();

    Profile p = null;

    Stage stage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            p = DatabaseDao.getUserInfo(iDs.getUserID());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        txtRepName.setText("Representative Name: "+p.getFull_name());
        txtEmail.setText("Email: "+p.getEmail());
        txtGender.setText("Gender: "+p.getGender());

        try {
            if(!DatabaseDao.getCompanyName(iDs.getUserID()).equals("")){
               txtCompanyName.setText(DatabaseDao.getCompanyName(iDs.getUserID()));
               txtCompanyName2.setText(DatabaseDao.getCompanyName(iDs.getUserID()));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




        // When Job Ads button is clicked, below code is executed
        btnJobAds.setOnAction( e ->{
            loadPage("job_ads");
        });

        btnInfo.setOnAction( e ->{
            bp.setCenter(vbox);
        });


        btnJobApps.setOnAction(e ->{
            loadPage("company_applications");
        });


        btnLogOut.setOnAction(e ->{
            try {

                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

                LoginController.stageController.setTitle("Job Rec | Login");

                Scene scene = new Scene(root);
                //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                LoginController.stageController.setScene(scene);


            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


    }

    private void loadPage(String page){
        Parent root = null;

        try{
            root = FXMLLoader.load(getClass().getResource(page+".fxml"));
        }catch (Exception e){

        }

        bp.setCenter(root);
    }
}
