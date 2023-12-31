package ru.hse.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Room {
    private final String text;

    public Room() {
        text = new GetFiles().getRandomFile();
    }

    private volatile boolean inGame = false;

    public synchronized boolean isInGame() {
        return inGame;
    }

    private volatile List<MonoThreadServer> players = new ArrayList<>();

    public synchronized boolean addUser(Socket socket, long id) {
        if (players.size() >= 3) {
            return false;
        }
        try {
            MonoThreadServer monoThreadServer = new MonoThreadServer(new Client(socket, id));
            players.add(monoThreadServer);
            System.out.println(monoThreadServer.client().name() + " добавлен");
        } catch (IOException e) {
            System.out.println("Не смогли зарегистрировать пользователя!");
            return false;
        }
        return true;
    }

    public void run() {
        Thread checker = new Thread(this::checkerActive);
        checker.start();
        waiting25sec();

        System.out.println("Отключаем чекер");
        checker.interrupt();
        inGame = true;
        System.out.println("Игра начинается, готовность 5 секунд!");

        sendingTextToEveryone(text);

        ResultTable resultTable = new ResultTable(players, text.length());
        Thread updateTable = new Thread(resultTable);
        updateTable.setDaemon(true);
        updateTable.start();

        Thread start = new Thread(() ->  starting(resultTable));
        start.start();

        Thread checkerIsFinal = new Thread(() -> {
           while (true) {
               try {
                   Thread.sleep(1_000);
               } catch (InterruptedException e) {
                   return;
               }
               if (resultTable.checker()) {
                   start.interrupt();
                   return;
               }
           }
        });

        checkerIsFinal.setDaemon(true);
        checkerIsFinal.start();

        try {
            start.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        closeEveryOne();
    }

    private void closeEveryOne() {
        players.forEach(it -> {
            if (it.client().isAlive()) {
                try {
                    it.client().sendLong(3);
                } catch (IOException ignored) {}
            }
        });
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        players.forEach(it -> {
            if (it.client().isAlive()) {
                try {
                    it.client().close();
                } catch (IOException ignored) {}
            }
        });
    }

    public void checkerActive() {
        System.out.println("Чекер подключения включен");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
                return;
            }
            System.out.println("Проверка подключения пользователей");
            synchronized (this) {
                players = new ArrayList<>(players.stream()
                        .filter(it -> {
                            if (!it.client().isAlive()) {
                                try {
                                    System.out.println(it.client().name() + " отключился. Мы тоже отключаем его!");
                                    it.client().close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return false;
                            }
                            return true;
                        }).toList());
            }
        }
    }

    private synchronized void sendingStatsAndTimerToEveryone(String stats, String timer) {
        List<MonoThreadServer> thisTime = new ArrayList<>(players);

        thisTime.forEach(it -> {
            try {
                if (it.client().isAlive()) {
                    it.sendStatsAndTimer(stats, timer);
                }
            } catch (IOException ignored) {
                try {
                    it.client().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Не отправилось!!!");
            }
        });

        thisTime.forEach(it -> {
            try {
                if (it.client().isAlive()) {
                    it.client().inputStream().readLong();
                }
            } catch (IOException ignored) {
                System.out.println("Упали без ответа клиента");
            }
        });
    }

    private synchronized void sendingTextToEveryone(String text) {
        List<MonoThreadServer> thisTime = new ArrayList<>(players);

        thisTime.forEach(it -> {
            try {
                it.sendText(text);
            } catch (IOException ignored) {
            }
        });

        thisTime.forEach(it -> {
            try {
                if (it.client().isAlive()) {
                    it.client().inputStream().readLong();
                }
                System.out.println("Получили ответ");
            } catch (IOException ignored) {
                System.out.println("Упали без ответа клиента");
            }
        });
    }

    private String initTimer(long timeStay) {
        return "До завершения \nэтапа осталось \n" + timeStay / 1000 + " сек";
    }

    private void waiting25sec() {
        System.out.println("Пустили таймер 25 секунд");
        long start = System.currentTimeMillis();
        long cur;
        while ((cur = System.currentTimeMillis() - start) <= 25_000) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            StringBuilder stringBuilder = new StringBuilder();
            players.forEach(it -> stringBuilder.append(it.client().name()).append(", id:").append(it.client().getId()).append("\n"));
            long timeStay = 25000 - cur;
            sendingStatsAndTimerToEveryone(stringBuilder.toString(), initTimer(timeStay));
        }
    }

    private void starting(ResultTable resultTable) {
        System.out.println("Запустили таймер 5 секунд");
        sendingTextToEveryone(text);
        long start = System.currentTimeMillis();
        long cur;
        while ((cur = System.currentTimeMillis() - start) <= 5_000) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            long timeStay = 5_000 - cur;
            sendingStatsAndTimerToEveryone(resultTable.toString(), initTimer(timeStay));
        }

        start = System.currentTimeMillis();
        while (((cur = System.currentTimeMillis() - start) <= 180_000) && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            long timeStay = 180_000 - cur;
            sendingStatsAndTimerToEveryone(resultTable.toString(), initTimer(timeStay));
        }
        sendingStatsAndTimerToEveryone(resultTable.endNow(), "FINISH");
    }
}

