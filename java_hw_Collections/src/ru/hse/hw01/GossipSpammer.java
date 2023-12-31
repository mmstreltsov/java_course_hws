package ru.hse.hw01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Implementation of Spammer-type gossips
 */
class GossipSpammer extends Gossips {
    /**
     * variable for getting a random value
     */
    public static final Random random = new Random();

    /**
     * constructor using String
     *
     * @param name gossip's name
     */
    GossipSpammer(String name) {
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
        List<Gossips> ans = new ArrayList<>();
        List<Gossips> listeners = getFollowers();
        for (Gossips tmp : listeners) {
            int count = random.nextInt(2, 6);
            for (int i = 0; i < count; i++) {
                ans.add(tmp);
            }
        }
        return ans;
    }

}
