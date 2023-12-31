package ru.hse.client.windows;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GameWindowController {
    @FXML
    private TextArea statistic;

    @FXML
    private TextArea timer;

    @FXML
    private TextArea text;

    @FXML
    private TextField input;
    @FXML
    private TextArea hint;


    public TextArea getStatistic() {
        return statistic;
    }

    public TextArea getTimer() {
        return timer;
    }

    public TextArea getText() {
        return text;
    }

    public TextField getInput() {
        return input;
    }

    public TextArea getHint() {
        return hint;
    }

}