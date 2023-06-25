package jobs.rec.jobrecsoftware;

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
import java.util.List;
import java.util.ResourceBundle;

public class CompanyApplicationsController implements Initializable {

    @FXML
    private Button btnAccept;

    @FXML
    private Button btnApplicantInfo;

    @FXML
    private Button btnReject;

    @FXML
    private Button btnReload;

    @FXML
    private ProgressBar pbLoading;

    @FXML
    private TableView<Application> tableView;

    @FXML
    private TableColumn<Application, String> tcAdtype;

    @FXML
    private TableColumn<Application, String> tcCategory;

    @FXML
    private TableColumn<Application, String> tcDescription;

    @FXML
    private TableColumn<Application, String> tcJobTitle;

    @FXML
    private TableColumn<Application, String> tcProcess;

    @FXML
    private TableColumn<Application, Integer> tcNumber;

    @FXML
    private TableColumn<Application, Integer> tcPrice;

    @FXML
    private Text txtMessage;

    private ObservableList<Application> appsList = FXCollections.observableArrayList();

    IDs iDs = IDs.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            iDs.setCompanyID(DatabaseDao.getCompanyID(iDs.getUserID()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        // Initiating the table columns
        tcNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        tcJobTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tcCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tcAdtype.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tcProcess.setCellValueFactory(new PropertyValueFactory<>("processString"));


        // Adding the observableList to the tableView
        tableView.setItems(appsList);



        // Loading all applications in database
        try {
            loadApplicationAds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        btnApplicantInfo.setOnAction(e ->{
            // Get the selected item from the table view
            Application selectedJob = tableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                Profile p = null;
                Credential c = null;
                try {
                    p = DatabaseDao.getUserInfo(selectedJob.getUser_id());
                    c= DatabaseDao.getUserCredentials(selectedJob.getUser_id());

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                Alert userInfoDialog = new Alert(Alert.AlertType.INFORMATION);
                userInfoDialog.setTitle("View Credentials");
                userInfoDialog.setHeaderText("Applicant's Credentials");
                userInfoDialog.setContentText("StuApplicant's Name: "+p.getFull_name()+"\n"+
                        "Gender: "+p.getGender()+"\n"+
                        "Education: "+c.getEducation()+"\n"+
                        "Area of Expertise: "+c.getCategory()+"\n"+
                        "Experience Level: "+c.getExperience()
                );

                userInfoDialog.showAndWait();

            }else{
                txtMessage.setFill(Color.RED);
                txtMessage.setText("No Application selected");
            }
        });


        btnReload.setOnAction(e ->{
            appsList.clear();
            // Loading all applications in database
            try {
                loadApplicationAds();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnAccept.setOnAction(e ->{
            Application selectedJob = tableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                int row = 0;
                try {
                    row = DatabaseDao.acceptApplication(selectedJob.getId());
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if(row > 0){
                    txtMessage.setFill(Color.GREEN);
                    txtMessage.setText("Application accepted successfully");
                }else{
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Oops! Error occured");
                }

            }else{
                txtMessage.setFill(Color.RED);
                txtMessage.setText("No Application selected");
            }
        });

        btnReject.setOnAction( e->{
            Application selectedJob = tableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                int row = 0;
                try {
                    row = DatabaseDao.rejectApplication(selectedJob.getId());
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if(row > 0){
                    txtMessage.setFill(Color.GREEN);
                    txtMessage.setText("Application rejected successfully");
                }else{
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Oops! Error occured");
                }

            }else{
                txtMessage.setFill(Color.RED);
                txtMessage.setText("No Application selected");
            }
        });

    }

    public void loadApplicationAds() throws SQLException, ClassNotFoundException {
        // Query all Application ads  in the database that belongs to this representative
        QueryCompanyApplicationTask queryCompanyApplicationTask  = new QueryCompanyApplicationTask(iDs.getCompanyID());


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading your applications.........Please Wait");
        pbLoading.progressProperty().bind(queryCompanyApplicationTask.progressProperty());

        queryCompanyApplicationTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        queryCompanyApplicationTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<Application> apps = queryCompanyApplicationTask.getValue();

            if(apps.size() > 0){
                for (Application app : apps) {
                    appsList.add(app);
                }

                txtMessage.setText("");

            }else{
                txtMessage.setFill(Color.GREEN);
                txtMessage.setText("No application received");
            }

        });


        queryCompanyApplicationTask.setOnFailed((event) -> {
            pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");

        });

        Thread taskThread = new Thread(queryCompanyApplicationTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }

}
