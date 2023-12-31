package ru.hse.client.windows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class GameWindow extends Application {

    private volatile GameWindowController controller = null;

    public synchronized GameWindowController getController() {
        return controller;
    }

    private final Stage stage = new Stage();
    private final Socket socket;

    public GameWindow(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameWindow.class.getResource("gameWindow.fxml"));

        Parent load = fxmlLoader.load();
        this.controller = fxmlLoader.getController();

        Scene scene = new Scene(load, 640, 620);
        stage.setTitle("Game!");
        stage.setScene(scene);
        stage.show();
    }

    public void setWindow() {
        try {
            start(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void stop() throws Exception {
        socket.close();
        super.stop();
    }
}