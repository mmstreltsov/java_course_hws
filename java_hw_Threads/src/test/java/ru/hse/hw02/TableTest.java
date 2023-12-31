package ru.hse.hw02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TableTest {

    private static final String TEAM = "Testing";
    private static final List<String> members = new ArrayList<>(List.of("Mem 1", "Mem 2", "Mem3"));

    @Test
    void diceRange() {
        Table table = new Table(TEAM, members, null);
        for (int i = 0; i < 10_000; i++) {
            int tmp = table.dice();
            boolean range = (tmp <= 36 && tmp >= 6);
            Assertions.assertTrue(range);
        }
    }
}