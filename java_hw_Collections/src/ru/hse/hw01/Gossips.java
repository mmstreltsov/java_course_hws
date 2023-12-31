package ru.hse.hw01;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * abstract class - super class for certain-types gossips
 */
abstract class Gossips {
    /**
     * variables for counting getting messages
     */
    protected int countMessage;
    /**
     * gossip's name
     */
    private final String name;
    /**
     * list of listeners
     */
    private final List<Gossips> followers = new ArrayList<>();

    /**
     * constructor using String - gossip's name
     *
     * @param name - gossip's name
     */
    Gossips(String name) {
        countMessage = 0;
        this.name = name;
    }

    /**
     * abstract method, because every gossips-type has different implementation
     *
     * @param message - message which gossip have received
     * @return ArrayList which contain list of gossips whose must get a message
     */
    public abstract List<Gossips> sendMessage(String message);

    /**
     * The implementation of behavior when receiving a message (override if necessary)
     *
     * @param message - message which gossip have received
     * @return ArrayList which contain list of gossips whose must get a message
     */
    public List<Gossips> getMessage(String message) {
        countMessage++;
        System.out.println(name + " get message #" + countMessage + ": " + message);
        return sendMessage(message);
    }

    /**
     * the special realization when gossip have received last message (catching on upper level)
     *
     * @param message - message which gossip have received
     */
    public void getLastMessage(String message) {
        countMessage++;
        System.out.println(name + ": I am tired, I am leaving");
    }

    /**
     * count messages
     *
     * @return the data of private field - countMessage
     */
    public int getCountMessage() {
        return countMessage;
    }

    /**
     * gossip's name
     *
     * @return the data of private field - name
     */
    public String getName() {
        return name;
    }

    /**
     * gossip's listeners
     *
     * @return the data of private field - followers
     */
    public List<Gossips> getFollowers() {
        return followers;
    }

    /**
     * add an object into followers
     *
     * @param other - Gossips instance
     * @return boolean type. True <=> success added
     */
    public boolean link(Gossips other) {
        if (getFollowers().size() < 10) {
            return this.getFollowers().add(other);
        }
        return false;
    }

    /**
     * remove an object into followers
     *
     * @param other - Gossips instance
     * @return boolean type. True <=> success removed
     */
    public boolean unlink(Gossips other) {
        return this.getFollowers().remove(other);
    }

    /**
     * overriding method equals - comparing two objects
     *
     * @param otherObj - Gossips instance
     * @return boolean type. True <=> instances are equals
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!super.equals(otherObj)) return false;
        Gossips obj = (Gossips) otherObj;
        return Objects.equals(this.getName(), obj.getName());
    }

    /**
     * overriding method hash Code - the correspondence of the object and the int
     *
     * @return value of hash-function
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    /**
     * overriding method to string for right showing of gossip-object
     *
     * @return gossip's name
     */
    @Override
    public String toString() {
        return name;
    }
}
