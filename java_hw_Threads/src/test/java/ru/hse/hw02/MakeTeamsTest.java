package ru.hse.hw02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MakeTeamsTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void countMembersWithCertainAmountOfTeams(int t) {
        MakeTeams makeTeams = new MakeTeams();
        Map<String, List<String>> making = makeTeams.making(t);
        Assertions.assertAll(() -> {
            Assertions.assertEquals(t, making.keySet().size());
            for (List<String> value : making.values()) {
                Assertions.assertEquals(3, value.size());
            }
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2324243, -1243423})
    void illegalAmountOfTeams(int t) {
        MakeTeams makeTeams = new MakeTeams();
        Map<String, List<String>> making = makeTeams.making(t);
        Assertions.assertEquals(0, making.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void checkRepeatedNames(int t) {
        MakeTeams makeTeams = new MakeTeams();
        Map<String, List<String>> making = makeTeams.making(t);
        Set<String> names = new HashSet<>();
        for (String s : making.keySet()) {
            names.addAll(making.get(s));
        }
        Assertions.assertEquals(3 * t, names.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void checkRepeatedTeams(int t) {
        MakeTeams makeTeams = new MakeTeams();
        Map<String, List<String>> making = makeTeams.making(t);
        Set<String> names = new HashSet<>(making.keySet());
        Assertions.assertEquals(t, names.size());
    }
}