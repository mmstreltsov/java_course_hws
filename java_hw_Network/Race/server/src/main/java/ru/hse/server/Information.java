package ru.hse.server;

public class Information {
    private long errors = 0;
    private long symbols = 0;
    private long speed = 0;

    private final long firstMoment = System.currentTimeMillis();
    private long lastMoment = -1;
    private long time = firstMoment;


    private long place = -1;
    public synchronized long getErrors() {
        return errors;
    }

    public synchronized long getSymbols() {
        return symbols;
    }

    public synchronized long getSpeed() {
        return speed;
    }

    public synchronized void setLastMoment(long time, long place) {
        this.lastMoment = time;
        this.place = place;
    }
    public synchronized long getTime() {

        //correction
        return lastMoment - (firstMoment - 5_000);
    }

    public synchronized long getPlace() {
        return place;
    }


    public synchronized void insertData(long errors, long symbols) {
        this.errors = errors;
        long time = System.currentTimeMillis();
        this.speed = (long) (((symbols - this.symbols) / ((time - this.time) * 1.0 / 1000)) * 60);
        this.time = time;
        this.symbols = symbols;
    }
}
