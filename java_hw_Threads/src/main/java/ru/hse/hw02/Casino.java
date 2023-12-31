package ru.hse.hw02;

import java.text.DecimalFormat;
import java.util.*;

public class Casino {

    private final static Random random = new Random();
    private final DecimalFormat round = new DecimalFormat("#.##");
    private final List<Table> room = new ArrayList<>();

    private final Map<Table, Integer> pointsTable = new HashMap<>();

    public Map<Table, Integer> getPointsTable() {
        return Collections.unmodifiableMap(pointsTable);
    }

    private final int secret = random.nextInt(1_000_000, 10_000_000 + 1);

    public final static int COUNT_STOPS = 3;

    public Casino(Map<String, List<String>> teams) {
        for (String team : teams.keySet()) {
            List<String> members = teams.get(team);
            Table table = new Table(team, members, pointsTable);
            room.add(table);

            pointsTable.put(table, 0);
        }
    }

    public void croupier() throws InterruptedException {
        List<Thread> threads = makeThreads();
        firstStage(threads);

        for (int i = 0; i < COUNT_STOPS; i++) {
            Thread.sleep(10_000);
            intermediateStage(i);
        }
        Thread.sleep(5_000);
        finalStage(threads);
    }

    private static final List<String> remain = new ArrayList<>(List.of("25 секунд", "15 секунд", "5 секунд"));


    public void firstStage(List<Thread> threads) {
        Main.pw.println("Давайте сыграем");
        Main.pw.println("Сегодняшний банк: ¥" + secret);
        threads.forEach(Thread::start);
    }

    public void intermediateStage(int i) {
        synchronized (pointsTable) {
            Main.pw.println("Внимание, говорит Москва");
            List<Table> leaderboard = leaderboard();
            Main.pw.println("Текущий лидер:");
            for (Table table : leaderboard) {
                Main.pw.println("   " + "\"" + table.getTeam() + "\"" + " с " + pointsTable.get(table) + " очками");
            }
            Main.pw.println("До конца игры остается " + remain.get(i));
            Main.pw.println("Всем сохранять спокойствие, продолжаем");
        }
    }

    public void finalStage(List<Thread> threads) {
        synchronized (pointsTable) {
            Main.pw.println("Конец игры!");
            threads.forEach(Thread::interrupt);
            threads.forEach(it -> {
                try {
                    it.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            Main.pw.println("Наши победители. Сегодня они разделят ¥" + secret);
            List<Table> leaderboard = leaderboard();
            for (Table table : leaderboard) {
                int points = pointsTable.get(table);
                Main.pw.println("\"" + table.getTeam() + "\"" + ": " + points + " очков");
                Map<String, Integer> personalPoints = table.getPersonalPoints();
                for (String s : personalPoints.keySet()) {
                    int personPoints = personalPoints.get(s);
                    double share = personPoints / (points * 1.0);
                    String win = round.format(share * secret / leaderboard.size());
                    Main.pw.println("   " + s + ": " + personPoints + " очков " + "=> доля составляет ¥" + win);
                }
            }
        }
    }

    public List<Table> leaderboard() {
        int maxPoints = 0;
        for (Table table : pointsTable.keySet()) {
            if (maxPoints == 0 || pointsTable.get(table) > maxPoints) {
                maxPoints = pointsTable.get(table);
            }
        }
        List<Table> leaders = new ArrayList<>();
        for (Table tmp : room) {
            if (pointsTable.get(tmp) == maxPoints) {
                leaders.add(tmp);
            }
        }
        return leaders;
    }



    public List<Thread> makeThreads() {
        List<Thread> threads = new ArrayList<>();
        for (Table table : room) {
            threads.addAll(table.players());
        }
        return threads;
    }
}
