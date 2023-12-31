package ru.hse.hw01;

import java.util.ArrayList;
import java.util.List;

/**
 * The Implementation of Censor-type gossips
 */
class GossipCensor extends Gossips {

    /**
     * constructor using String
     *
     * @param name gossip's name
     */
    GossipCensor(String name) {
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
        if ((message.toLowerCase()).contains("java")) {
            return getFollowers();
        } else {
            return new ArrayList<>();
        }
    }


}
