package ru.hse.hw01;


import java.util.Scanner;

/**
 * @author Maksim Streltsov
 * the program implements a game about Gossips.
 * @version 1.0.0
 */
public class Main {

    /**
     * Initialize Scanner from System.in once. Further only to reuse
     */
    public static final Scanner in = new Scanner(System.in);

    /**
     * the Implementation
     *
     * @param args parameter of command line. Using it for initialize max amount of calls
     */
    public static void main(String[] args) {
//        try {
//            int m = Integer.parseInt(args[0]);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("you did not put argument: m - max number of calls");
//            System.exit(0);
//        }
        //int m = Integer.parseInt(args[0]);
        int m = 5;
        GossipsParty gossipsParty = new GossipsParty(m);

        while (in.hasNextLine()) {
            try {
                var answer = in.next();
                if (answer.equals("about")) {
                    Main.about();
                } else if (answer.equals("create")) {
                    String name = in.next();
                    String type = in.next();
                    gossipsParty.addGossip(name, type);
                } else if (answer.equals("link")) {
                    String firstName = in.next();
                    String secondName = in.next();
                    gossipsParty.link(firstName, secondName);
                } else if (answer.equals("unlink")) {
                    String firstName = in.next();
                    String secondName = in.next();
                    gossipsParty.unlink(firstName, secondName);
                } else if (answer.equals("message")) {
                    String name = in.next();
                    String message = in.nextLine();
                    gossipsParty.sendMessage(name, message);
                } else if (answer.equals("gossips")) {
                    gossipsParty.gossips();
                } else if (answer.equals("listeners")) {
                    String name = in.next();
                    gossipsParty.listeners(name);
                } else if (answer.equals("help")) {
                    System.out.println("""
                            These commands are allowed:
                                -create <name> <type>
                                -link <name1> <name2>
                                -unlink <name1> <name2>
                                -message <name> <message>
                                -gossips
                                -listeners <name>
                                -quit
                                -about
                                -help""");
                } else if (answer.equals("quit")) {
                    Main.quit();
                    break;
                } else {
                    Main.unknownCommand();
                }
            } catch (UndefinedBehaviorException | UnknownTypeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * methods prints information if unknown command come
     */
    public static void unknownCommand() {
        System.out.println("Unknown command");
    }

    /**
     * method prints information about program
     */
    public static void about() {
        System.out.println("""
                author - Maksim Streltsov
                group - 211, subgroup - 2
                                
                the program implements a gossips-game""");
    }

    /**
     * finishing the program if exit-command come
     */
    public static void quit() {
        System.out.println("Bye!");
    }
}

