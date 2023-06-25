package jobs.rec.jobrecsoftware;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StudentRecommendationsController implements Initializable {

    @FXML
    private Button btnApply;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSearch;

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
    private Button btnShowRecommended;

    @FXML
    private TextField tfSearch;

    @FXML
    private Text txtMessage;


    private ObservableList<Job> jobList = FXCollections.observableArrayList();

    IDs iDs = IDs.getInstance();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
            loadRecommendedJobAds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        btnSearch.setOnAction(e ->{
            if(tfSearch.getText().isEmpty()){
                txtMessage.setFill(Color.RED);
                txtMessage.setText("Search query not provided");
                return;
            }

            jobList.clear();

            try {
                loadSearchJobAds(tfSearch.getText());

                txtMessage.setFill(Color.BLACK);
                txtMessage.setText(jobList.size()+" ad(s) found");

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }


        });




        btnApply.setOnAction(e -> {
                    // Get the selected item from the table view
                    Job selectedJob = tableView.getSelectionModel().getSelectedItem();
                    if (selectedJob != null) {
                        try {
                            int row = DatabaseDao.applyJob(iDs.getUserID(),selectedJob.getId());

                            if(row > 0){
                                txtMessage.setFill(Color.GREEN);
                                txtMessage.setText("Application sent successfully");
                            }else{
                                txtMessage.setFill(Color.RED);
                                txtMessage.setText("Oops! Error occurred or Application for this ad already sent");
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

       btnShowRecommended.setOnAction(e ->{
           jobList.clear();
           try {
               loadRecommendedJobAds();
           } catch (SQLException ex) {
               throw new RuntimeException(ex);
           } catch (ClassNotFoundException ex) {
               throw new RuntimeException(ex);
           }
       });

    }

    public void loadRecommendedJobAds() throws SQLException, ClassNotFoundException {

        // Query all job ads  in the database that belongs to this representative
        MyJobRecommendationTask myJobRecommendationTask  = new MyJobRecommendationTask(iDs.getUserID());


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading recommended ads.........Please Wait");
        pbLoading.progressProperty().bind(myJobRecommendationTask.progressProperty());

        myJobRecommendationTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        myJobRecommendationTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<Job> jobs = myJobRecommendationTask.getValue();

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


        myJobRecommendationTask.setOnFailed((event) -> {
            pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");

        });

        Thread taskThread = new Thread(myJobRecommendationTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }


    public void loadSearchJobAds(String query) throws SQLException, ClassNotFoundException {

        // Query all job ads  in the database that belongs to this representative
        SerachJobAdTask serachJobAdTask  = new SerachJobAdTask(query);


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading recommended ads.........Please Wait");
        pbLoading.progressProperty().bind(serachJobAdTask.progressProperty());

        serachJobAdTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        serachJobAdTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<Job> jobs = serachJobAdTask.getValue();



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


        serachJobAdTask.setOnFailed((event) -> {

            try {
                throw new Exception();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            /*pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");*/

        });

        Thread taskThread = new Thread(serachJobAdTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }
}
