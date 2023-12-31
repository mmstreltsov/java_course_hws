package ru.hse.client;

import ru.hse.client.windows.GameWindow;
import ru.hse.client.windows.InitWindowController;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final Client client = new Client();
    public static Client fabric() {
        return client;
    }
    private Client() {
    }

    private InitWindowController controller;
    private GameWindow gameWindow;
    private String host;
    private String port;
    private String username;

    private void newWindow() {
        assert gameWindow != null;
        controller.close();
        gameWindow.setWindow();
    }

    public void initClientFromInitWindow(InitWindowController controller) {
        this.controller = controller;

        this.host = controller.getAddress().getText();
        this.port = controller.getPort().getText();
        this.username = controller.getUsername().getText();

        connection();
    }

    private void connection() {
        Socket socket;
        try {
            socket = new Socket(host, Integer.parseInt(port));
        } catch (IOException e) {
            //Server Error
            controller.errorServerNotFound();
            return;
        }

        gameWindow = new GameWindow(socket);

        newWindow();
        Thread game = new Thread(() -> {
            try {
                new Game(gameWindow.getController(), socket, username);
            } catch (IOException e) {
                try {
                    socket.close();
                    throw new RuntimeException(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        game.start();

    }
}
