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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
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

public class StudentDashboardController implements Initializable {

    @FXML
    private BorderPane bp;

    @FXML
    private Button btnInfo;

    @FXML
    private Button btnJobApps;

    @FXML
    private Button btnJobRecommended;

    @FXML
    private Button btnLogOut;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Text txtCVUpload;

    @FXML
    private Text txtCategory;

    @FXML
    private Text txtCertification;

    @FXML
    private Text txtBigName;

    @FXML
    private Text txtEducation;

    @FXML
    private Text txtEmail;

    @FXML
    private Text txtExperience;

    @FXML
    private Text txtGender;

    @FXML
    private Text txtStudentName;

    @FXML
    private ScrollPane scrollPane;

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
                txtStudentName.setText("Student Name: "+result1.get());
                txtBigName.setText(result1.get());
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

                if(gender.contains("M")){
                    imgLogo.setImage(new Image(getClass().getResourceAsStream("male.png")));
                }else if(gender.contains("F")){
                    imgLogo.setImage(new Image(getClass().getResourceAsStream("female.png")));
                }else{
                    imgLogo.setImage(new Image(getClass().getResourceAsStream("avi.png")));
                }
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
        }else if(buttonId.equals("edu")){
            TextInputDialog textInputDialog1 = new TextInputDialog();
            textInputDialog1.setTitle("Educational Background");
            textInputDialog1.setHeaderText("Enter your educational background");
            textInputDialog1.setContentText("Education: ");
            Optional<String> result1 = textInputDialog1.showAndWait();
            if(result1.isPresent()){
                txtEducation.setText("Education: "+result1.get());
                DatabaseDao.insertUpdateCredentials(iDs.getUserID(),result1.get(),"education");
            }
        }else if(buttonId.equals("category")){
            List<String> choices = Arrays.asList("Information Technology (IT)","Healthcare","Education","Finance and Accounting","Engineering","Human Resources");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Information Technology (IT)", choices);
            dialog.setTitle("Expertise Category");
            dialog.setHeaderText("Please select your category:");
            dialog.setContentText("Category:");

            // Show the dialog and wait for a response.
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String category = result.get();
                txtCategory.setText("Category: " + category);
                DatabaseDao.insertUpdateCredentials(iDs.getUserID(),category,"category");
                //DatabaseDao.updateGender(iDs.getUserID(),gender);
            }
        }else if(buttonId.equals("exp")){
            List<String> choices = Arrays.asList("Beginner","Intermediate","Expert");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Beginner", choices);
            dialog.setTitle("Experience Level");
            dialog.setHeaderText("Please select your experience level:");
            dialog.setContentText("Experience:");

            // Show the dialog and wait for a response.
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String exp = result.get();
                txtExperience.setText("Experience: " + exp);
                DatabaseDao.insertUpdateCredentials(iDs.getUserID(),exp,"experience");
                //DatabaseDao.updateGender(iDs.getUserID(),gender);
            }
        }else if(buttonId.equals("certification")){
            TextInputDialog textInputDialog1 = new TextInputDialog();
            textInputDialog1.setTitle("Certification Info");
            textInputDialog1.setHeaderText("Enter your certification info");
            textInputDialog1.setContentText("Cerification: ");
            Optional<String> result1 = textInputDialog1.showAndWait();
            if(result1.isPresent()){
                txtCertification.setText("Certification: "+result1.get());
                DatabaseDao.insertUpdateCredentials(iDs.getUserID(),result1.get(),"ceification");
                //DatabaseDao.insertUpdate(iDs.getUserID(),result1.get());
            }
        }else{

        }
    }


    IDs iDs = IDs.getInstance();

    Profile p = null;

    Credential c = null;

    Stage stage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {



        try {
            p = DatabaseDao.getUserInfo(iDs.getUserID());
            c = DatabaseDao.getUserCredentials(iDs.getUserID());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        System.out.println(c.getEducation());
        txtStudentName.setText("Student Name: "+p.getFull_name());
        txtBigName.setText(p.getFull_name());
        txtEmail.setText("Email: "+p.getEmail());
        txtGender.setText("Gender: "+p.getGender());


        if(p.getGender().contains("M")){
            imgLogo.setImage(new Image(getClass().getResourceAsStream("male.png")));
        }else if(p.getGender().contains("F")){
            imgLogo.setImage(new Image(getClass().getResourceAsStream("female.png")));
        }else{
            imgLogo.setImage(new Image(getClass().getResourceAsStream("avi.png")));
        }

        if(c.getEducation() != null){
            txtEducation.setText("Education: "+c.getEducation());
        }

        if(c.getEducation() != null){
            txtEducation.setText("Education: "+c.getEducation());
        }

        if(c.getCategory() != null){
            txtCategory.setText("Category: "+c.getCategory());
        }

        if(c.getExperience() != null){
            txtExperience.setText("Experience: "+c.getExperience());
        }

        if(c.getCeification() != null){
            txtCertification.setText("Certification: "+c.getCeification());
        }

        if(c.getDocument() != null){
            txtCVUpload.setText("C.V: Uploaded");
        }else{
            txtCVUpload.setText("C.V: Not Uploaded");
        }




        btnJobRecommended.setOnAction(e ->{
            loadPage("student_recommendations");
        });


        btnInfo.setOnAction(e ->{
            bp.setCenter(scrollPane);
        });

        btnJobApps.setOnAction(e ->{
            loadPage("student_applications");
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
