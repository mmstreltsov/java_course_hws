package ru.hse.hw02;

import java.io.PrintStream;
import java.util.*;

public class Main {
    public static final PrintStream pw = System.out;
    public static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            pw.println("Вы не ввели аргумент. Выход");
            return;
        }
        int t;
        try {
            t = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            pw.println("Вы ввели не число. Выход");
            return;
        }
//        int t = 10;
        if (t < 1 || t > 10) {
            pw.println("Число не в диапазоне, обговоренном правилами. Выход");
            return;
        }
        do {
            MakeTeams makeTeams = new MakeTeams();
            Map<String, List<String>> map = makeTeams.making(t);
            Casino casino = new Casino(map);
            casino.croupier();
            pw.println("Введите exit, чтобы закончить на сегодня, или же любой символ, чтобы начать заново");
            if ("exit".equals(in.next())) {
                pw.println("Всего доброго");
                return;
            }
        } while (in.hasNextLine());
    }
}
