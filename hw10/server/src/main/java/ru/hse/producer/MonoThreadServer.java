package ru.hse.producer;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.*;


public class MonoThreadServer {

    private static final int GET_FILES = 1;
    private static final int DOWNLOAD = 2;
    private final List<String> FILES = new ArrayList<>(List.of("a.txt", "b.txt"));

    private final Map<String, Long> fileToLength = initMapFileToLength();

    private Map<String, Long> initMapFileToLength() {
        Map<String, Long> map = new HashMap<>();

        for (var s : FILES) {
            URL url = Fabric.class.getResource("/files/" + s);
            URLConnection connection = null;
            try {
                connection = Objects.requireNonNull(url)
                        .openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            map.put(s, connection.getContentLengthLong());
        }
        return map;
    }

    private String initMessage1() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < FILES.size(); ++i) {
            stringBuilder.append(i + 1).append(" ").append(FILES.get(i)).append("\n");
        }
        return stringBuilder.toString();
    }

    private final String message1 = initMessage1();

    public MonoThreadServer(Socket socket) {
        init(socket);
    }

    private void init(Socket socket) {
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            int code;
            while (true) {
                code = dataInputStream.readInt();
                System.out.println("Получили код");

                if (code == GET_FILES) {
                    file_names(dataOutputStream);
                } else if (code == DOWNLOAD) {
                    downland(dataInputStream, dataOutputStream);
                }
            }
        } catch (EOFException ignored) {
            System.out.println("Клиент все");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downland(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        System.out.println("Пытаемся скачать");
        int number = dataInputStream.readInt();
        System.out.println("Получили номер файла");
        if (number > FILES.size() || number <= 0) {
            dataOutputStream.writeLong(-1);
            dataOutputStream.flush();
            System.out.println("Неверный номер файла");
            return;
        }
        Path path = Path.of("/files/" + FILES.get(number - 1));
        try (InputStream inputStream = Objects.requireNonNull(Fabric.class.getResourceAsStream(path.toString()))) {
            dataOutputStream.writeLong(fileToLength.get(FILES.get(number - 1)));
            System.out.println("Пытаемся отправить файл");
            inputStream.transferTo(dataOutputStream);
            dataOutputStream.flush();
            System.out.println("Отправили файл");
        }
    }

    private void file_names(DataOutputStream dataOutputStream) throws IOException {
        System.out.println("Пытаемся записать имена файлов");
        dataOutputStream.writeUTF(message1);
        dataOutputStream.flush();
        System.out.println("Записали все доступные файлы");
    }
}
