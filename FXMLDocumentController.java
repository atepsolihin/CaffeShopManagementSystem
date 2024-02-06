package caffeshopmanagementsystem;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;

public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane si_loginForm;

    @FXML
    private TextField si_username;

    @FXML
    private PasswordField si_password;

    @FXML
    private Button si_loginBtn;

    @FXML
    private AnchorPane su_signupForm;

    @FXML
    private TextField su_username;

    @FXML
    private PasswordField su_password;

    @FXML
    private ComboBox<String> su_question;

    @FXML
    private TextField su_answer;

    @FXML
    private Button su_signupBtn;

    @FXML
    private AnchorPane side_form;

    @FXML
    private Button side_CreateBtn;

    @FXML
    private Button side_alreadyHave;

    private Alert alert;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        regLquestionList();
        si_loginBtn.setOnAction(event -> loginBtn());
        su_signupBtn.setOnAction(event -> regBtn(event));
    }

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public void loginBtn() {

        if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Error Message", null, "Incorrect Username/Password");
        } else {

            String selctData = "SELECT username, password FROM employee WHERE username = ? and password = ?";

            connect = database.connectDB();

            try {

                prepare = connect.prepareStatement(selctData);
                prepare.setString(1, si_username.getText());
                prepare.setString(2, si_password.getText());

                result = prepare.executeQuery();

                if (result.next()) {

                    showAlert(AlertType.INFORMATION, "Information Message", null, "Successfully Login!");

                    // LINK YOUR MAIN FORM
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("mainForm.fxml"));
                    Parent root = loader.load();
                    mainDashboardController controller = loader.getController();

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    stage.setTitle("Cafe Shop Management System");
                    stage.setMinWidth(1100);
                    stage.setMinHeight(600);

                    stage.setScene(scene);
                    stage.show();

                    si_loginBtn.getScene().getWindow().hide();

                } else {
                    showAlert(AlertType.ERROR, "Error Message", null, "Incorrect Username/Password");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void regBtn(ActionEvent event) {
        if (su_username.getText().isEmpty() || su_password.getText().isEmpty() || su_answer.getText().isEmpty() || su_question.getSelectionModel().isEmpty()) {
            showAlert(AlertType.ERROR, "Error Message", null, "Please fill all fields");
        } else {
            String regData = "INSERT INTO employee (username, password, question, answer, date) VALUES (?, ?, ?, ?, ?)";

            try (Connection connect = database.connectDB(); PreparedStatement prepare = connect.prepareStatement(regData)) {

                // Check if the username is already recorded
                String checkUsername = "SELECT username FROM employee WHERE username = ?";
                try (PreparedStatement checkPrepare = connect.prepareStatement(checkUsername)) {
                    checkPrepare.setString(1, su_username.getText());
                    try (ResultSet result = checkPrepare.executeQuery()) {
                        if (result.next()) {
                            showAlert(AlertType.ERROR, "Error Message", null, su_username.getText() + " is already taken");
                            return;
                        }
                    }
                }

                if (su_password.getText().length() < 8) {
                    showAlert(AlertType.ERROR, "Error Message", null, "Password must be at least 8 characters long.");
                    return;
                }

                prepare.setString(1, su_username.getText());
                prepare.setString(2, su_password.getText());
                prepare.setString(3, su_question.getSelectionModel().getSelectedItem());
                prepare.setString(4, su_answer.getText());

                Date date = new Date();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                prepare.setDate(5, sqlDate);

                prepare.executeUpdate();

                showAlert(AlertType.INFORMATION, "Information Message", null, "Successfully registered Account!");

                // Clear input fields
                su_username.clear();
                su_password.clear();
                su_question.getSelectionModel().clearSelection();
                su_answer.clear();

                // Slide back to login form
                TranslateTransition slider = new TranslateTransition();
                slider.setNode(side_form);
                slider.setToX(0);
                slider.setDuration(Duration.seconds(.5));
                slider.setOnFinished((ActionEvent e) -> {
                    side_alreadyHave.setVisible(false);
                    side_CreateBtn.setVisible(true);
                });
                slider.play();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error Message", null, "Failed to register account. Please try again.");
            }
        }
    }

    private String[] questionList = {"What is your favorite Color?", "What is your favorite food?", "What is your birth date?"};

    public void regLquestionList() {
        ObservableList<String> listData = FXCollections.observableArrayList(questionList);
        su_question.setItems(listData);
    }

    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();
        if (event.getSource() == side_CreateBtn) {
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_CreateBtn.setVisible(false);
                regLquestionList();
            });
            slider.play();
        } else if (event.getSource() == side_alreadyHave) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_CreateBtn.setVisible(true);
            });
            slider.play();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
