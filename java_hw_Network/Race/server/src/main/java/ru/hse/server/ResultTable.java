package ru.hse.server;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

public class ResultTable implements Runnable {
    private final Map<MonoThreadServer, Information> resultTable;
    private final Set<MonoThreadServer> finalResult = new CopyOnWriteArraySet<>();
    private final Set<MonoThreadServer> lostConnect = new CopyOnWriteArraySet<>();
    private final long lengthText;

    public ResultTable(List<MonoThreadServer> players, long lengthText) {
        this.resultTable = initResultTable(players);
        this.lengthText = lengthText;
    }

    private Map<MonoThreadServer, Information> initResultTable(List<MonoThreadServer> players) {
        Map<MonoThreadServer, Information> resultTable = new ConcurrentHashMap<>();
        players.forEach(it -> resultTable.put(it, new Information()));
        return resultTable;
    }

    private final String format = "%15s, %10s | %10s %6s %6s %7s %20s\n";

    public boolean checker() {
        return finalResult.size() >= resultTable.size();
    }

    @Override
    public String toString() {
        Map<MonoThreadServer, Information> thisMoment = new HashMap<>(resultTable);
        Set<MonoThreadServer> lost = new HashSet<>(lostConnect);
        Set<MonoThreadServer> finalRes = new HashSet<>(finalResult);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(format, "HEADER", "ID", "race", "error", "speed", "connect", "result"));

        thisMoment.keySet().forEach(it -> {
            String name = it.client().name(), id = "id:" + it.client().getId(), race, errors, speed, connect, result = "";
            if (finalRes.contains(it)) {
                String time, place;
                if (thisMoment.get(it).getPlace() == -1) {
                    time = "NOT FINISHED";
                    place = String.valueOf(this.place.get());
                } else {
                    time = thisMoment.get(it).getTime() / 1000 + " sec. ";
                    place = String.valueOf(thisMoment.get(it).getPlace());
                }
                result = "TIME: " + time + "PLACE " + place;
            }
            if (lost.contains(it)) {
                race = "-";
                errors = "-";
                speed = "-";
                connect = "not OK";
                if (finalRes.contains(it) && thisMoment.get(it).getSymbols() < lengthText) {
                    result = "NOT IN LEADER BOARD";
                }
            } else {
                race = String.format("%.2f", thisMoment.get(it).getSymbols() * 100.0 / lengthText) + "%";
                errors = String.valueOf(thisMoment.get(it).getErrors());
                speed = String.valueOf(thisMoment.get(it).getSpeed());
                connect = "OK";
            }
            stringBuilder.append(String.format(format, name, id, race, errors, speed, connect, result));
        });
        return stringBuilder.toString();
    }

    public String endNow() {
        finalResult.addAll(resultTable.keySet());
        return toString();
    }

    private final AtomicLong place = new AtomicLong(1);

    private void updateTable() {
        resultTable.keySet().forEach(it -> {
            if (!finalResult.contains(it) && !it.client().isAlive()) {
                lostConnect.add(it);
            }
        });

        resultTable.keySet().forEach(it -> {
            if (!finalResult.contains(it) && resultTable.get(it).getSymbols() >= lengthText) {
                finalResult.add(it);
                resultTable.get(it).setLastMoment(System.currentTimeMillis(), place.getAndIncrement());
            }
        });

        resultTable.keySet().forEach(it -> {
            if (!lostConnect.contains(it) && !finalResult.contains(it)) {
                try {
                    long errors = it.getErrors();
                    long symbols = it.getSymbols();
                    resultTable.get(it).insertData(errors, symbols);
                } catch (EOFException ignored) {
                    lostConnect.add(it);
                    System.out.println("Данные потеряны");
                } catch (IOException e) {
                    lostConnect.add(it);
                    System.out.println("Соединение - все!");
                }
            }
        });
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            updateTable();
        }
    }
}
