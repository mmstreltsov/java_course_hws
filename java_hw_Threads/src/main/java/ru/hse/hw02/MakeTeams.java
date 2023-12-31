package ru.hse.hw02;


import java.util.*;

class MakeTeams {

    private static final Random random = new Random();
    private static final List<String> TEAMS = List.of("Помидорчики", "Кабачки", "Баклажаны", "Картофелины",
            "Морковки", "Яблочки", "Сливы", "Ягодки", "Абрикосы", "Персики");
    private static final int COUNT_MEMBERS = 3;
    private final List<String> names = new ArrayList<>(List.of("Аношин", "Бейшембиев", "Бывальцева",
            "Варенов", "Воропаев", "Гамаюнов", "Григорьева", "Гусев", "Дежин", "Дунаев", "Ермишин",
            "Ершов", "Кириллов", "Кожевников", "Кононенко", "Никифорова", "Печёнкин", "Пудовкин",
            "Рыжков", "Сальников", "Самсонов", "Свитковский", "Смирнов", "Степанов", "Стрельцов",
            "Строганов", "Хабибрахманова", "Хабибулина", "Шабад", "Шнипов", "Шубин"));


    /**
     * @param t 1 <= t <= 10: amount of teams
     * @return map:    Team -> list of members
     */
    public Map<String, List<String>> making(int t) {
        if (t < 0 || t > 10) {return new HashMap<>();}
        Map<String, List<String>> ret = new HashMap<>();
        for (int i = 0; i < t; i++) {
            List<String> members = new ArrayList<>();
            for (int j = 0; j < COUNT_MEMBERS; j++) {
                int index = random.nextInt(0, names.size());
                String name = names.remove(index);
                members.add(name);
            }
            ret.put(TEAMS.get(i), members);
        }
        return ret;
    }
}
