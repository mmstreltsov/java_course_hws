package ru.hse.consumer;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Consumer {

    private static final int GET_FILES = 1;
    private static final int DOWNLOAD = 2;

    private static final int CHUNK_SIZE = 1 << 10;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Некорректный ввод. Ожидаем: \n" +
                    "адрес + порт + директория");
            return;
        }
        String host = args[0];
        String port = args[1];
        String directory = args[2];

        Map<Integer, String> numToNameFile = new HashMap<>();


        try (Socket socket = new Socket(host, Integer.parseInt(port))) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream socketInput = new DataInputStream(socket.getInputStream())) {

                Scanner scanner = new Scanner(System.in);

                System.out.println("Мы подключились успешно");
                System.out.println("Введите " + GET_FILES + " для получения всех файлов, " + DOWNLOAD + "для скачивания файла, \"exit\" для завершения работы");

                System.out.println("Ожидаем ввод");
                while (scanner.hasNextLine()) {
                    String input = scanner.nextLine();

                    if (input.equals("exit")) {
                        System.out.println("Завершаемся");
                        return;
                    }

                    int code;
                    try {
                        code = Integer.parseInt(input);
                        if (!(code == GET_FILES || code == DOWNLOAD)) {
                            System.out.println("Такого кода нет");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Неправильный формат ввода");
                        continue;
                    }
                    dataOutputStream.writeInt(code);
                    dataOutputStream.flush();

                    System.out.println("Отослали код на сервер");

                    if (code == GET_FILES) {
                        System.out.println("Получаем список файлов");
                        String ans = socketInput.readUTF();
                        StringTokenizer stringTokenizer = new StringTokenizer(ans);
                        while (stringTokenizer.hasMoreTokens()) {
                            int num = Integer.parseInt(stringTokenizer.nextToken());
                            String name = stringTokenizer.nextToken();
                            numToNameFile.put(num, name);
                            System.out.println(num + ". " + name);
                        }
                        System.out.println("________________________");
                    } else if (code == DOWNLOAD) {
                        System.out.println("Введите номер файла");
                        int number = scanner.nextInt();
                        if (!numToNameFile.containsKey(number)) {
                            System.out.println("Такого файла нет");
                            continue;
                        }
                        dataOutputStream.writeInt(number);
                        dataOutputStream.flush();

                        System.out.println("Ожидаем: " + numToNameFile.get(number));

                        long length = socketInput.readLong();
                        if (length != -1) {
                            System.out.println("Файл записывается...");
                            Path path = Paths.get(directory + numToNameFile.get(number));
                            try (OutputStream outputStream = Files.newOutputStream(path)) {
                                byte[] chunk;
                                while (length > 0) {
                                    System.out.println("Поехали");
                                    if (length < CHUNK_SIZE) {
                                        chunk = new byte[(int) length];
                                        System.out.println("Начинаем считывать");
                                        int read = socketInput.read(chunk);
                                        System.out.println("Заканчиваем считывать");
                                        outputStream.write(chunk, 0, read);
                                        length -= read;
                                    } else {
                                        chunk = new byte[CHUNK_SIZE];
                                        System.out.println("Начинаем считывать");
                                        length -= socketInput.read(chunk);
                                        System.out.println("Заканчиваем считывать");
                                        outputStream.write(chunk);
                                    }
                                }
                                outputStream.flush();
                            }
                            System.out.println("Файл " + path.toString() + " Записан");
                        } else {
                            System.out.println("Проблема с чтением файла (Вероятно, такого файла не существует)");
                        }
                    }
                    System.out.println("Ожидаем ввод");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.out.println("Socket не открылся. Возможно, сервер закрыт");
            return;
        }
    }
}