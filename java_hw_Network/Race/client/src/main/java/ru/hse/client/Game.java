package ru.hse.client;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import ru.hse.client.windows.GameWindowController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class Game {
    private final GameWindowController controller;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private StringTokenizer stringTokenizer;

    public Game(GameWindowController controller, Socket socket, String username) throws IOException {
        this.controller = controller;

        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        //say my name
        outputStream.writeUTF(username);

        initStats("");
        initText("There is no any text yet...");
        initTimer("");
        startGame();
    }

    private volatile long symbols = 0;
    private volatile long errors = 0;

    private synchronized void initStats(String text) {
        controller.getStatistic().setText(text);
    }

    private synchronized void initTimer(String text) {
        controller.getTimer().setText(text);
    }

    private synchronized void initText(String text) {
        controller.getText().setText(text);
        stringTokenizer = new StringTokenizer(text);
        assert stringTokenizer.hasMoreTokens();
        current = stringTokenizer.nextToken();
        controller.getHint().setText(current);
    }


    private volatile String current;
    private volatile boolean isError;

    private void input() {
        TextField textField = controller.getInput();
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!current.equals("")) {
                boolean decision = inputLogic(current, newVal);
                if (!decision) {
                    synchronized (this) {
                        if (!isError) {
                            errors++;
                            isError = true;
                        }
                    }
                    textField.setStyle("-fx-text-fill: red;");
                } else {
                    synchronized (this) {
                        if (isError) {
                            isError = false;
                        }
                    }
                    textField.setStyle("-fx-text-fill: #000000;");
                    if (newVal.length() == current.length() + 1) {
                        synchronized (this) {
                            symbols += newVal.length();
                        }
                        Platform.runLater(textField::clear);
                        if (stringTokenizer.hasMoreTokens()) {
                            current = stringTokenizer.nextToken();
                        } else {
                            current = "";
                        }
                        controller.getHint().setText(current);
                    }
                }

            }
        });
    }

    private boolean inputLogic(String excepted, String actual) {
        int l = 0, r = 0;
        while (l < excepted.length() && r < actual.length()) {
            if (excepted.charAt(l) == actual.charAt(r)) {
                l++;
                r++;
            } else {
                return false;
            }
        }
        return actual.length() <= excepted.length() || (actual.length() == excepted.length() + 1 && actual.charAt(actual.length() - 1) == ' ');
    }

    private volatile boolean isNeedSend = false;
    private void sendingStats() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        if (isNeedSend) {
            synchronized (this) {
                try {
                    outputStream.writeLong(errors);
                    outputStream.writeLong(symbols);
                } catch (IOException e) {
                    if (closeGameNow) {
                        return;
                    }
                    System.out.println("Не вышло!");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updating() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                updateTexts();
            } catch (IOException e) {
                if (closeGameNow) {
                    return;
                }
                System.out.println("Не получилось считать");
                System.out.println(e.getMessage());
            }
        }
    }

    private volatile boolean closeGameNow = false;

    private void updateTexts() throws IOException {

        long code = inputStream.readLong();

        if (code == 0) {
            String stats = inputStream.readUTF();
            String timer = inputStream.readUTF();
            initStats(stats);
            initTimer(timer);
        } else if (code == 1) {
            isNeedSend = true;
            String text = inputStream.readUTF();
            initText(text);
            new Thread(() -> {
                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                controller.getInput().setEditable(true);
            }).start();

            controller.getHint().setVisible(true);

        } else if (code == 3) {
            closeGameNow = true;
            disconnectGUI();

            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                return;
            }

            close();
            /// завершить игру
            return;
        }
        else {
            throw new RuntimeException("Unknown code");
        }
        outputStream.writeLong(0);
    }


    private void close() {
        try (socket; inputStream; outputStream) {

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void disconnectGUI() {
        controller.getHint().setVisible(false);
        controller.getInput().setEditable(false);
    }
    private boolean checkIfFinal() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            return false;
        }

        if (current.equals("")) {
            System.out.println("YOU WON");

            disconnectGUI();
            return true;
        }
        return false;
    }

    private void startGame() {
        input();
        Thread updating = new Thread(this::updating);
        updating.setDaemon(true);
        updating.start();

        Thread stats = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                sendingStats();
            }
        });
        stats.setDaemon(true);
        stats.start();

        Thread end = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (checkIfFinal()) {
                    if (closeGameNow) {
                        return;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        });
        end.setDaemon(true);
        end.start();
    }
}
