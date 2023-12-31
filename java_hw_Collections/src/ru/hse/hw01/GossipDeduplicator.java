package ru.hse.hw01;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Implementation of Deduplication-type gossips
 */
class GossipDeduplicator extends Gossips {

    /**
     * set for keeping messages, which gossip have received
     */
    Set<String> texts = new HashSet<>();

    /**
     * constructor using String
     *
     * @param name gossip's name
     */
    GossipDeduplicator(String name) {
        super(name);
    }

    /**
     * overriding a method, because there is certain behavior
     *
     * @param message message which gossip have received
     * @return ArrayList which contain list of gossips whose must get a message
     */
    @Override
    public List<Gossips> getMessage(String message) {
        if (texts.add(message)) {
            return super.getMessage(message);
        }
        return new ArrayList<>();
    }

    /**
     * the Implementation of abstract method
     *
     * @param message message which gossip have received
     * @return ArrayList which contain list of gossips whose must get a message
     */
    @Override
    public List<Gossips> sendMessage(String message) {
        if (texts.add(message)) {
            return getFollowers();
        }
        return new ArrayList<>();
    }

}
