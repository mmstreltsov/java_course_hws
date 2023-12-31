package ru.hse.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class GetFiles {
    private List<String> fileNames = List.of(
            "TheCountOfMonteCristo.txt", "ПреступлениеИНаказание.txt", "Рокки.txt", "ВойнаИМир.txt", "Avengers.txt"
    );

//    public static void main(String[] args) {
//        System.out.println(new GetFiles().getRandomFile());
//    }


    private String getFile(String name) {
        if (!fileNames.contains(name)) {
            return "";
        }
        try (InputStream stream = GetFiles.class.getResourceAsStream("/texts/" + name)) {
            assert stream != null;
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRandomFile() {
        Random random = new Random();
        int index = random.nextInt(0, fileNames.size());

        return getFile(fileNames.get(index));
    }
}
