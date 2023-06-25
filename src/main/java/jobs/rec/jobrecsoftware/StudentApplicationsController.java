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

public class StudentApplicationsController implements Initializable {

    @FXML
    private Button btnCompanyInfo;

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

        System.out.println(iDs.getUserID());
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



        // Loading all users in database
        try {
            loadApplicationAds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        btnCompanyInfo.setOnAction(e ->{
            // Get the selected item from the table view
            Application selectedJob = tableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                try {
                    Alert userInfoDialog = new Alert(Alert.AlertType.INFORMATION);
                    userInfoDialog.setTitle("View Company Info");
                    userInfoDialog.setHeaderText("Company Info");
                    userInfoDialog.setContentText("Company Name: "+DatabaseDao.getCompanyInfo(selectedJob.getJob_id()));

                    userInfoDialog.showAndWait();

                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }

    public void loadApplicationAds() throws SQLException, ClassNotFoundException {
        // Query all Application ads  in the database that belongs to this representative
        QueryApplicationTask queryApplicationAdsTask  = new QueryApplicationTask(iDs.getUserID());


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading your applications.........Please Wait");
        pbLoading.progressProperty().bind(queryApplicationAdsTask.progressProperty());

        queryApplicationAdsTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        queryApplicationAdsTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<Application> apps = queryApplicationAdsTask.getValue();

            if(apps.size() > 0){
                for (Application app : apps) {
                    appsList.add(app);
                }

                txtMessage.setText("");

            }else{
                txtMessage.setFill(Color.GREEN);
                txtMessage.setText("No application sent");
            }

        });


        queryApplicationAdsTask.setOnFailed((event) -> {
            pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");

        });

        Thread taskThread = new Thread(queryApplicationAdsTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }
}
