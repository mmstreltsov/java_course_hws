package ru.hse.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    private volatile AtomicLong id = new AtomicLong(0);

    public static void main(String[] args) {
        new Server().initServer();
    }
    private List<Room> rooms = new ArrayList<>();
    private void initServer() {
        try (ServerSocket serverSocket = new ServerSocket(5620)) {
            while (!Thread.currentThread().isInterrupted()) {
                acceptor(serverSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void acceptor(ServerSocket serverSocket) {
        try  {
            Socket socket = serverSocket.accept();
            long id = this.id.getAndIncrement();
            rooms = new ArrayList<>(rooms.stream().filter(room -> !room.isInGame()).toList());
            for (Room room : rooms) {
                if (room.addUser(socket, id)) {
                    return;
                }
            }
            Room newRoom = new Room();
            newRoom.addUser(socket, id);
            rooms.add(newRoom);
            new Thread(newRoom::run).start();
        } catch (IOException e) {
            System.out.println("Не смогли найти комнату юзеру!");
        }
    }

}
