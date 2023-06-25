package jobs.rec.jobrecsoftware;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminRecommendationController implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnRecommend;

    @FXML
    private Button btnViewCreds;

    @FXML
    private ComboBox<String> cbxStudents;

    @FXML
    private ProgressBar pbLoading;

    @FXML
    private TableView<Job> tableView;

    @FXML
    private TableColumn<Job, String> tcAdtype;

    @FXML
    private TableColumn<Job, String> tcCategory;

    @FXML
    private TableColumn<Job, String> tcDescription;

    @FXML
    private TableColumn<Job, String> tcJobTitle;

    @FXML
    private TableColumn<Job, String> tcKeywords;

    @FXML
    private TableColumn<Job, Integer> tcNumber;

    @FXML
    private TableColumn<Job, Integer> tcPrice;

    @FXML
    private Text txtMessage;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Job> jobList = FXCollections.observableArrayList();

    private ObservableList<String> emailList = FXCollections.observableArrayList();

    private List<String> emails = new ArrayList<>();

    private String email = null;

    private int user_id = 0;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadStudents();


        cbxStudents.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                email = newValue;

                try {
                    user_id = DatabaseDao.getUserID(email);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        // Initiating the table columns
        tcNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        tcJobTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tcCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tcAdtype.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tcKeywords.setCellValueFactory(new PropertyValueFactory<>("keyword"));

        // Adding the observableList to the tableView
        tableView.setItems(jobList);

        // Loading all users in database
        try {
            loadJobAds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }





        cbxStudents.setItems(emailList);



        btnViewCreds.setOnAction(e ->{
            if(cbxStudents.getSelectionModel().getSelectedItem() != null){
                Profile p = null;
                Credential c = null;

                try {
                    p = DatabaseDao.getUserInfo(user_id);
                    c= DatabaseDao.getUserCredentials(user_id);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                Alert userInfoDialog = new Alert(Alert.AlertType.INFORMATION);
                userInfoDialog.setTitle("View Credentials");
                userInfoDialog.setHeaderText("Student Credentials");
                userInfoDialog.setContentText("Student Name: "+p.getFull_name()+"\n"+
                        "Gender: "+p.getGender()+"\n"+
                        "Education: "+c.getEducation()+"\n"+
                        "Area of Expertise: "+c.getCategory()+"\n"+
                        "Experience Level: "+c.getExperience()
                        );

                userInfoDialog.showAndWait();

            }
        });


        // Setting the selection to single selection for the table
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        btnRecommend.setOnAction(e -> {
            // Get the selected item from the table view
            Job selectedJob = tableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                try {
                    int row = DatabaseDao.recommendToStudent(user_id,selectedJob.getId());

                    if(row > 0){
                        txtMessage.setFill(Color.GREEN);
                        txtMessage.setText("Job recommendended to student with email: "+email);
                    }else{
                        txtMessage.setFill(Color.RED);
                        txtMessage.setText("Oops! Error occured or Application sent for this ad already"+email);
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }


            }else{
                txtMessage.setFill(Color.RED);
                txtMessage.setText("No ad selected");
            }
        });


        btnClear.setOnAction(e ->{
            txtMessage.setText("");
            cbxStudents.getSelectionModel().clearSelection();
            cbxStudents.setPromptText("Student Emails");
        });

    }


    public void loadJobAds() throws SQLException, ClassNotFoundException {
        // Query all job ads  in the database that belongs to this representative
        QueryAllAdsTask queryJobAdsTask  = new QueryAllAdsTask();


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading ads.........Please Wait");
        pbLoading.progressProperty().bind(queryJobAdsTask.progressProperty());

        queryJobAdsTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        queryJobAdsTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<Job> jobs = queryJobAdsTask.getValue();

            if(jobs.size() > 0){
                for (Job job : jobs) {
                    jobList.add(job);
                }

                txtMessage.setText("");

            }else{
                txtMessage.setFill(Color.GREEN);
                txtMessage.setText("No ad added");
            }

        });


        queryJobAdsTask.setOnFailed((event) -> {
            pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");

        });

        Thread taskThread = new Thread(queryJobAdsTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }


    public void loadStudents(){
        // Query all the users in the database that are not admin
        QueryStudentsTask queryStudentsTask  = new QueryStudentsTask();


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading users.........Please Wait");
        pbLoading.progressProperty().bind(queryStudentsTask.progressProperty());

        queryStudentsTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        queryStudentsTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<User> users = queryStudentsTask.getValue();
            System.out.println("In loadStudents: "+users.size());
            if(users.size() > 0){
                for (User user : users) {
                    userList.add(user);
                }

                for(User u : userList){
                    emails.add(u.getEmail());
                }

                for (String email : emails){
                    emailList.add(email);
                }

            }else{
                txtMessage.setFill(Color.GREEN);
                txtMessage.setText("No user added");
            }

        });


        queryStudentsTask.setOnFailed((event) -> {
            pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");

        });

        Thread taskThread = new Thread(queryStudentsTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }
}
