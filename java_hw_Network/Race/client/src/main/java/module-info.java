module ru.hse.client.client {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens ru.hse.client to javafx.fxml;
    exports ru.hse.client;
    exports ru.hse.client.windows;
    opens ru.hse.client.windows to javafx.fxml;
}