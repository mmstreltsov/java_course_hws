package ru.hse.hw01;

import java.util.List;

/**
 * The Implementation of Simple-type gossips
 */
class GossipSimple extends Gossips {
    /**
     * constructor using String
     *
     * @param name gossip's name
     */
    GossipSimple(String name) {
        super(name);
    }

    /**
     * the Implementation of abstract method
     *
     * @param message message which gossip have received
     * @return ArrayList which contain list of gossips whose must get a message
     */
    @Override
    public List<Gossips> sendMessage(String message) {
        return getFollowers();
    }

}
