package ru.hse.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

final class Client {
    private final long id;
    private final String name;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Client(Socket socket, long id) throws IOException {
        this.id = id;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        this.name = inputStream.readUTF();

        System.out.println(isAlive());

        System.out.println("Клиент " + name + " идентифицирован");
    }

    public synchronized boolean isAlive() {
        return !socket.isClosed();
    }

    public void sendString(String str) throws IOException {
        outputStream.writeUTF(str);
        outputStream.flush();
    }

    public void sendLong(long val) throws IOException {
        outputStream.writeLong(val);
        outputStream.flush();
    }
    public void close() throws IOException {
        try (this.socket;
             this.inputStream;
             this.outputStream;) {
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(name, client.name) && Objects.equals(socket, client.socket) && Objects.equals(inputStream, client.inputStream) && Objects.equals(outputStream, client.outputStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, socket, inputStream, outputStream);
    }

    public String name() {
        return name;
    }

    public long getId() {
        return id;
    }

    public DataInputStream inputStream() {
        return inputStream;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", socket=" + socket +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                '}';
    }
}