package ru.hse.hw02;

import java.util.*;

class Table {
    private final Object lock = new Object();
    private final String team;
    private final List<String> members;


    public String getTeam() {
        return team;
    }

    private final Map<String, Integer> personalPoints;

    public Map<String, Integer> getPersonalPoints() {
        return new HashMap<>(personalPoints);
    }
    private final Queue<String> queue = new ArrayDeque<>();
    private final Map<Table, Integer> pointsTable;
    Table(String team, List<String> members, Map<Table, Integer> pointsTable) {
        this.team = team;
        this.members = new ArrayList<>(members);
        this.pointsTable = pointsTable;
        queue.addAll(members);

        personalPoints = new HashMap<>();
        for (String member : members) {
            personalPoints.put(member, 0);
        }
    }


    public List<Thread> players() {
        List<Thread> ret = new ArrayList<>();
        for (String member : members) {
            ret.add(new Thread(() -> player(member)));
        }
        return new ArrayList<>(ret);
    }


    public void player(String name) {
        Main.pw.println("Привет, я - " + name + ", с команды \"" + team + "\"");
        while (!Thread.currentThread().isInterrupted()) {
            int get;
            synchronized (lock) {
                while (queue.isEmpty() || !name.equals(queue.peek())) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Main.pw.println(name + ": до встречи!");
                        return;
                    }
                }
                queue.poll();
                get = dice();
                lock.notifyAll();
            }
            recording(name, get);
            try {
                rest(name);
            } catch (InterruptedException e) {
                Main.pw.println(name + ": до встречи!");
                return;
            }
        }
        Main.pw.println(name + ": до встречи!");
    }

    private void recording(String player, int get) {
        synchronized (pointsTable) {
            pointsTable.replace(this, pointsTable.get(this) + get);
        }
        personalPoints.replace(player, personalPoints.get(player) + get);
    }

    public void rest(String name) throws InterruptedException {
        int ms = random.nextInt(100, 1000);
        Thread.sleep(ms);
        synchronized (lock) {
            queue.add(name);
            lock.notifyAll();
        }
    }

    private static final Random random = new Random();

    public int dice() {
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            sum += random.nextInt(1, 7);
        }
        return sum;
    }

}
