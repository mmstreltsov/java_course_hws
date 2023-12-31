package ru.hse.producer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Fabric {
    public static final int PORT = 4242;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static class ConnectionHandler implements Runnable {

        private final Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            new MonoThreadServer(socket);
        }
    }


    private static class ServerStart implements Runnable {

        private final ServerSocket serverSocket;

        public ServerStart(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while (true) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    System.out.println("Видимо, все");
                    return;
                }
                executorService.execute(new ConnectionHandler(socket));
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен. Порт " + PORT);
            new Thread(new ServerStart(serverSocket)).start();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if ("exit".equals(line)) {
                    serverSocket.close();
                    executorService.shutdown();
                    executorService.awaitTermination(1, TimeUnit.HOURS);
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Завершаемся!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


