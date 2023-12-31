package ru.hse.hw01;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;


/**
 * The Implementation of object which contain a several Gossips (room for gossips)
 */
class GossipsParty {
    /**
     * list for members of our party - where we keep our gossips
     */
    private final List<Gossips> gossips = new ArrayList<>();
    /**
     * field for keeping max amount of massages
     */
    private final int maxCountMessage;

    /**
     * constructor from int
     *
     * @param m - max amount of massages
     */
    GossipsParty(int m) {
        maxCountMessage = m;
    }

    /**
     * method for finding an object into list using its name
     *
     * @param name - gossip's name
     * @return Gossips instance
     */
    public Gossips findGossip(String name) {
        Objects.requireNonNull(name, "name == null");
        Gossips gossip1 = null;

        for (Gossips temp : gossips) {
            if (Objects.equals(temp.getName(), name)) {
                gossip1 = temp;
                break;
            }
        }
        return gossip1;
    }

    /**
     * adding an object into list
     *
     * @param name - gossip's name
     * @param type - gossip's type
     * @throws UnknownTypeException       - if received incorrect gossip's type
     * @throws UndefinedBehaviorException - exception with a description in the message
     */
    public void addGossip(String name, String type) throws UnknownTypeException, UndefinedBehaviorException {

        if (findGossip(name) != null) {
            throw new UndefinedBehaviorException("The gossip with this name already exists");
        }

        Gossips tmp = switch (type) {
            case "null" -> new GossipNull(name);
            case "censor" -> new GossipCensor(name);
            case "spammer" -> new GossipSpammer(name);
            case "simple" -> new GossipSimple(name);
            case "deduplicator" -> new GossipDeduplicator(name);
            default -> throw new UnknownTypeException("the incorrect type for Gossip");
        };
        gossips.add(tmp);
        System.out.println("Success, creating is complete");
    }

    /**
     * linking Gossips instances
     *
     * @param name1 name of first object
     * @param name2 name of second object
     * @throws UndefinedBehaviorException - exception with a description in the message
     */
    public void link(String name1, String name2) throws UndefinedBehaviorException {
        Gossips gossip1 = findGossip(name1);
        Gossips gossip2 = findGossip(name2);


        if (gossip1 == null && gossip2 == null) {
            throw new UndefinedBehaviorException("gossip with this name \"" + name1 + "\" are not defined yet\n" +
                    "gossip with this name \"" + name2 + "\" are not defined yet");
        }
        if (gossip1 == null) {
            throw new UndefinedBehaviorException("gossip with this name \"" + name1 + "\" are not defined yet");
        }
        if (gossip2 == null) {
            throw new UndefinedBehaviorException("gossip with this name \"" + name2 + "\" are not defined yet");
        }
        if (Objects.equals(name1, name2)) {
            throw new UndefinedBehaviorException("self-follower is forbidden");
        }
        if (gossip1.getFollowers().contains(gossip2)) {
            throw new UndefinedBehaviorException("double linking is forbidden");
        }
        if (gossip1.getFollowers().size() >= 10) {
            throw new UndefinedBehaviorException("the gossip has not free space at the followers list");
        }
        if (gossip1.link(gossip2)) {
            System.out.println("Success, linking is complete");
        }
    }

    /**
     * unlinking Gossips instances
     *
     * @param name1 name of first object
     * @param name2 name of second object
     * @throws UndefinedBehaviorException - exception with a description in the message
     */
    public void unlink(String name1, String name2) throws UndefinedBehaviorException {
        Gossips gossip1 = findGossip(name1);
        Gossips gossip2 = findGossip(name2);

        if (gossip1 == null && gossip2 == null) {
            throw new UndefinedBehaviorException("gossip with this name \"" + name1 + "\" are not defined yet\n" +
                    "gossip with this name \"" + name2 + "\" are not defined yet");
        }
        if (gossip1 == null) {
            throw new UndefinedBehaviorException("gossip with this name \"" + name1 + "\" are not defined yet");
        }
        if (gossip2 == null) {
            throw new UndefinedBehaviorException("gossip with this name \"" + name2 + "\" are not defined yet");
        }
        if (Objects.equals(name1, name2)) {
            throw new UndefinedBehaviorException("self-follower was forbidden");
        }
        if (gossip1.unlink(gossip2)) {
            System.out.println("Success, unlinking is complete");
        } else {
            System.out.println("object already unlinked");
        }
    }

    /**
     * printing all Gossips in lexicographical order
     */
    public void gossips() {
        gossips.sort(Comparator.comparing(Gossips::getName));
        for (Gossips tmp : gossips) {
            System.out.println(tmp);
        }
        if (gossips.isEmpty()) {
            System.out.println("Nobody here");
        }
    }

    /**
     * printing all Gossips' listeners in lexicographical order
     *
     * @param name gossip's name
     * @throws UndefinedBehaviorException - exception with a description in the message
     */
    public void listeners(String name) throws UndefinedBehaviorException {
        Gossips gossip1 = findGossip(name);

        List<Gossips> listeners = gossip1.getFollowers();
        listeners.sort(Comparator.comparing(Gossips::getName));

        for (Gossips tmp : listeners) {
            System.out.println(tmp);
        }
    }

    /**
     * Preamble. Using another function on this arguments
     *
     * @param name    gossip's name
     * @param message message which gossip have received
     * @throws UndefinedBehaviorException - exception with a description in the message
     */
    public void sendMessage(String name, String message) throws UndefinedBehaviorException {
        Gossips gossip = findGossip(name);
        if (gossip == null) {
            throw new UndefinedBehaviorException("sending message to unknown gossip, because it is not defined yet");
        }
        sendMessageMainPart(gossip, message);
    }

    /**
     * the implementation of processing and sending a message
     *
     * @param gossip  Gossips instance
     * @param message message which gossip have received
     */
    public void sendMessageMainPart(Gossips gossip, String message) {
        List<Gossips> sending = new ArrayList<>();       //list of followers we must send message.
        Map<Gossips, ArrayList<Gossips>> alreadyGot = new HashMap<>();
        Gossips head = gossip;
        while (true) {
            int count = gossip.getCountMessage();
            if (count < maxCountMessage) {
                List<Gossips> temp = gossip.getMessage(message);
                for (Gossips i : temp) {
                    if (!(i.equals(head))) {
                        if (!(alreadyGot.containsKey(i))) {
                            ArrayList<Gossips> arr = new ArrayList<>();
                            arr.add(gossip);
                            alreadyGot.put(i, arr);

                            sending.add(i);
                        }
                        else if (!((alreadyGot.get(i)).contains(gossip))) {
                            alreadyGot.get(i).add(gossip);

                            sending.add(i);
                        }
                    }
                }
            }
            if (gossip.getCountMessage() == maxCountMessage) {
                gossip.getLastMessage(message);
            }
            if (!sending.isEmpty()) {
                gossip = sending.get(0);
                sending.remove(0);
                continue;
            }
            break;
        }
    }
}
