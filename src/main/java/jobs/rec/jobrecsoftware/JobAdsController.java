package jobs.rec.jobrecsoftware;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JobAdsController implements Initializable {

    @FXML
    private Button btnAddJob;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnReload;

    @FXML
    private Button btnRemoveAd;

    @FXML
    private ComboBox<String> cbxAdType;

    @FXML
    private ComboBox<String> cbxCategory;

    @FXML
    private FlowPane fp;

    @FXML
    private ProgressBar pbLoading;

    @FXML
    private TextArea taDescription;

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
    private TextField tfJobTitle;

    @FXML
    private TextField tfKeywords;

    @FXML
    private TextField tfPrice;

    @FXML
    private Text txtMessage;

    private String [] cats = {"Information Technology (IT)","Healthcare","Education","Finance and Accounting","Engineering","Human Resources"};

    private final ObservableList<String> categories = FXCollections.observableArrayList(cats);

    private String [] ts = {"Premium","Free"};

    private final ObservableList<String> types = FXCollections.observableArrayList(ts);

    private ObservableList<Job> jobList = FXCollections.observableArrayList();
    private List<String> keywordsList = new ArrayList<>();
    private double price;
    IDs iDs = IDs.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Initially setting the price text-field visibility to false
        tfPrice.setVisible(false);



        try {
            iDs.setCompanyID(DatabaseDao.getCompanyID(iDs.getUserID()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Populating the combo boxes
        cbxCategory.setItems(categories);
        cbxAdType.setItems(types);

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





        cbxAdType.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(cbxAdType.getSelectionModel().getSelectedIndex() == 0){
                    tfPrice.setVisible(true);
                }else{
                    price = 0;
                    tfPrice.setVisible(false);
                }
            }
        });



        // Creating a TextFormatter to restrict input to alphabets only
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z]*")) {
                return change;
            }
            return null;
        });

        // Apply the TextFormatter to the TextField
        tfKeywords.setTextFormatter(formatter);


        // Create a TextFormatter to restrict input to numbers or decimal point
        TextFormatter<String> formatter2 = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        });

        // Apply the TextFormatter to the TextField
        tfPrice.setTextFormatter(formatter2);

        tfKeywords.setOnAction( e->{
                if(!tfKeywords.getText().isEmpty()){
                    // Create a new button for the keyword
                    Button keywordButton = new Button(tfKeywords.getText());
                    keywordsList.add(tfKeywords.getText());

                    tfKeywords.clear();
                    // Add the button to the FlowPane
                    fp.getChildren().add(keywordButton);
                }
        });


        btnReload.setOnAction(e ->{
            try {
                jobList.clear();
                loadJobAds();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });


        btnAddJob.setOnAction(e ->{
            if(tfJobTitle.getText().isEmpty() || taDescription.getText().isEmpty() || keywordsList.size() < 1 ||
                    cbxCategory.getSelectionModel().getSelectedItem() == null || cbxAdType.getSelectionModel().getSelectedItem() == null ||
                    (tfPrice.isVisible() && tfPrice.getText().isEmpty())){
                txtMessage.setFill(Color.RED);
                txtMessage.setText("Empty field(s) detected!!");
                return;
            }

            if(cbxAdType.getSelectionModel().getSelectedIndex() == 0){
               price = Double.parseDouble(tfPrice.getText());
            }

           StringBuilder keywords = new StringBuilder();

            for(String keyword : keywordsList){
                keywords.append(keyword+";");
            }

            try {
                int row = DatabaseDao.insertJobAd(iDs.getCompanyID(),tfJobTitle.getText(),taDescription.getText(),cbxCategory.getSelectionModel().getSelectedItem()
                        ,cbxAdType.getSelectionModel().getSelectedItem(),price, String.valueOf(keywords));


                if(row > 0){
                    txtMessage.setFill(Color.GREEN);
                    txtMessage.setText(row+" row(s) affected, Reload Page");

                }else{
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Couldn't add user");
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }



        });


        // Setting the selection to single selection for the table
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        // Function is executed when "Delete Account" button is clicked
        btnRemoveAd.setOnAction(e ->{
            // Get the selected item from the table view
            Job selectedJob = tableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {

                DeleteJobAdTask deleteJobAdTask = new DeleteJobAdTask(selectedJob.getId());
                pbLoading.progressProperty().bind(deleteJobAdTask.progressProperty());


                deleteJobAdTask.setOnRunning(ex ->{
                    pbLoading.setVisible(true);
                });

                deleteJobAdTask.setOnSucceeded(ex ->{
                    pbLoading.setVisible(false);

                    Boolean deleted = deleteJobAdTask.getValue();

                    if(deleted) {
                        txtMessage.setFill(Color.GREEN);
                        txtMessage.setText("Ad removed successfully");
                        jobList.remove(selectedJob);
                    }else{
                        txtMessage.setFill(Color.GREEN);
                        txtMessage.setText("Removal Unsuccessful");
                    }

                });
                deleteJobAdTask.setOnFailed((event) -> {
                    pbLoading.setVisible(false);
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Error Occurred");
                });

                Thread taskThread = new Thread(deleteJobAdTask);
                taskThread.setDaemon(true);
                taskThread.start();


            }else{
                txtMessage.setFill(Color.RED);
                txtMessage.setText("No ad selected");
            }
        });

    }

    public void loadJobAds() throws SQLException, ClassNotFoundException {
        // Query all job ads  in the database that belongs to this representative
        QueryJobAdsTask queryJobAdsTask  = new QueryJobAdsTask(iDs.getCompanyID());


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading your ads.........Please Wait");
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
}
