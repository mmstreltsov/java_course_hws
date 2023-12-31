package ru.hse.client.windows;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.hse.client.Client;

public class InitWindowController {

    @FXML
    private Label welcomeText;

    private static final String DEFAULT_ADDRESS = "localhost";
    @FXML
    private TextField address;

    private static final String DEFAULT_PORT = "5619";

    @FXML
    private TextField port;


    private static final String DEFAULT_NAME = "GUEST";
    @FXML
    private TextField username;

    public TextField getAddress() {
        if (address.getText().equals("")) {
            address.setText(DEFAULT_ADDRESS);
        }
        return address;
    }

    public TextField getPort() {
        if (port.getText().equals("")) {
            port.setText(DEFAULT_PORT);
        }
        return port;
    }

    public TextField getUsername() {
        if (username.getText().equals("")) {
            username.setText(DEFAULT_NAME);
        }
        return username;
    }

    @FXML
    private void onPlayButtonClick() {
        Client.fabric().initClientFromInitWindow(this);
    }

    @FXML
    private void onAboutButtonClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("About");
        alert.setTitle("Help");
        alert.setContentText("Program by Streltsov Maksim, 211");
        alert.show();
    }

    public void errorServerNotFound() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Error!");
        alert.setTitle("ServerError");
        alert.setContentText("Problem with connection");
        alert.show();
    }

    public void close() {
        Stage window = (Stage) welcomeText.getScene().getWindow();
        window.close();
    }
}