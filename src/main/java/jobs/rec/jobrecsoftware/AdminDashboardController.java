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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private BorderPane bp;

    @FXML
    private Button btnAddUser;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelAcc;

    @FXML
    private Button btnJobRec;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnReload;

    @FXML
    private Button btnUsers;

    @FXML
    private ComboBox<String> cbxGender;

    @FXML
    private ComboBox<String> cbxUserType;

    @FXML
    private ProgressBar pbLoading;

    @FXML
    private ScrollPane scrollUsers;
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> tcEmail;

    @FXML
    private TableColumn<User, String> tcFullName;

    @FXML
    private TableColumn<User, String> tcGender;

    @FXML
    private TableColumn<User, Integer> tcNumber;

    @FXML
    private TableColumn<User , String > tcUserType;

    @FXML
    private TextField tfUserEmail;

    @FXML
    private TextField tfUserFullName;

    @FXML
    private Text txtMessage;

    private String [] genders = {"Male","Female","Other"};
    private String [] types = {"Student","Company Representative"};

    private final ObservableList<String> genderList = FXCollections.observableArrayList(genders);
    private final ObservableList<String> typeList = FXCollections.observableArrayList(types);

    private ObservableList<User> userList = FXCollections.observableArrayList();

    private String gender = null;
    private String type = null;

    Stage stage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Hiding the progressbar until an operation of database is triggered
        pbLoading.setProgress(-1);
        pbLoading.setVisible(false);

        // Populating the gender combo box
        cbxGender.setItems(genderList);

        // Populating the userType combo box
        cbxUserType.setItems(typeList);


        // Initiating the table columns
        tcNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        tcFullName.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        tcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tcGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        tcUserType.setCellValueFactory(new PropertyValueFactory<>("usertype"));

        // Adding the observableList to the tableView
        tableView.setItems(userList);

        // Loading all users in database
        loadUsers();

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



        // When the "Add User" button is clicked, this function is executed
        btnAddUser.setOnAction(e ->{
            if(tfUserFullName.getText().isEmpty() || tfUserEmail.getText().isEmpty() || gender == null || type == null){
                txtMessage.setFill(Color.RED);
                txtMessage.setText("Empty field(s) detected!!");
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

            String full_name = tfUserFullName.getText();
            String email = tfUserEmail.getText();


            try {
                int row = DatabaseDao.insertUser(full_name,email,gender,type);
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

        // When the "Clear" button is clicked, this function is executed
        btnClear.setOnAction(e ->{
            tfUserFullName.clear();
            tfUserEmail.clear();
            cbxGender.getSelectionModel().clearSelection();
            cbxGender.setPromptText("Gender");
            cbxUserType.getSelectionModel().clearSelection();
            cbxUserType.setPromptText("User Type");
            txtMessage.setText("");
        });


        // Setting the selection to single selection for the table
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        // Function is executed when "Delete Account" button is clicked
        btnDelAcc.setOnAction(e ->{
            // Get the selected item from the table view
            User selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {

                DeleteUserTask deleteUserTask = new DeleteUserTask(selectedUser.getId());
                pbLoading.progressProperty().bind(deleteUserTask.progressProperty());


                deleteUserTask.setOnRunning(ex ->{
                    pbLoading.setVisible(true);
                });

                deleteUserTask.setOnSucceeded(ex ->{
                    pbLoading.setVisible(false);

                    Boolean deleted = deleteUserTask.getValue();

                    if(deleted) {
                        txtMessage.setFill(Color.GREEN);
                        txtMessage.setText("User removed successfully");
                        userList.remove(selectedUser);
                    }else{
                        txtMessage.setFill(Color.GREEN);
                        txtMessage.setText("Removal Unsuccessful");
                    }

                });
                deleteUserTask.setOnFailed((event) -> {
                    pbLoading.setVisible(false);
                    txtMessage.setFill(Color.RED);
                    txtMessage.setText("Error Occurred");
                });

                Thread taskThread = new Thread(deleteUserTask);
                taskThread.setDaemon(true);
                taskThread.start();


            }else{
                txtMessage.setFill(Color.RED);
                txtMessage.setText("No user selected");
            }
        });

        // Reloads the page to show new table
        btnReload.setOnAction(e ->{
            userList.clear();
            loadUsers();
        });


        btnUsers.setOnAction(e ->{
            bp.setCenter(scrollUsers);
        });


        btnJobRec.setOnAction(e ->{
            loadPage("admin_recommendation");
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

    public void loadUsers(){
        // Query all the users in the database that are not admin
        QueryUsersTask queryUsersTask  = new QueryUsersTask();


        txtMessage.setFill(Color.BLACK);
        txtMessage.setText("Loading users.........Please Wait");
        pbLoading.progressProperty().bind(queryUsersTask.progressProperty());

        queryUsersTask.setOnRunning(ex ->{
            pbLoading.setVisible(true);
        });

        queryUsersTask.setOnSucceeded(ex ->{
            pbLoading.setVisible(false);
            List<User> users = queryUsersTask.getValue();

            if(users.size() > 0){
                for (User user : users) {
                    userList.add(user);
                }

                txtMessage.setText("");

            }else{
                txtMessage.setFill(Color.GREEN);
                txtMessage.setText("No user added");
            }

        });


        queryUsersTask.setOnFailed((event) -> {
            pbLoading.setVisible(false);
            txtMessage.setFill(Color.RED);
            txtMessage.setText("Error Occurred");

        });

        Thread taskThread = new Thread(queryUsersTask);
        taskThread.setDaemon(true);
        taskThread.start();
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
