package ru.hse.server;

import java.io.IOException;
import java.util.Objects;

public final class MonoThreadServer {
    private final Client client;

    public MonoThreadServer(Client client) {
        this.client = client;
    }

    public String makeIsYou(String str) {
        return str.replaceAll("id:" + client.getId(), "id:" + client.getId() + " (YOU)");
    }

    public void sendStatsAndTimer(String stats, String timer) throws IOException {
        stats = makeIsYou(stats);
        synchronized (this) {
            client.sendLong(0);
            client.sendString(stats);
            client.sendString(timer);
        }
    }

    public void sendText(String text) throws IOException {
        synchronized (this) {
            client.sendLong(1);
            client.sendString(text);
        }
    }

    public long getErrors() throws IOException {
        return client.inputStream().readLong();
    }

    public long getSymbols() throws IOException {
        return client.inputStream().readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonoThreadServer that = (MonoThreadServer) o;
        return Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client);
    }

    public Client client() {
        return client;
    }

    @Override
    public String toString() {
        return "MonoThreadServer[" +
                "client=" + client + ']';
    }

}
