package ru.hse.producer;

import java.io.IOException;
import java.net.Socket;

public class LastDance {
    public LastDance() {
        init();
    }

    private void init() {
        try {
            Socket socket = new Socket("localhost", 4242);
            socket.close();
        } catch (IOException e) {
            System.out.println("LastDance не удался");
        }
    }
}
