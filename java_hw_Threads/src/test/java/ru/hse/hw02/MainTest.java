package ru.hse.hw02;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {

    private static final String[] args = new String[]{""};

    @Test
    void incorrectArgNotANumber() {
        args[0] = "dsds";
        Assertions.assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void incorrectArgNotANumber2() {
        args[0] = "";
        Assertions.assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void incorrectArgNotANumber3() {
        args[0] = "10 0";
        Assertions.assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void incorrectArgNumberNotInInterval() {
        args[0] = "-1";
        Assertions.assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void incorrectArgNumberNotInInterval2() {
        args[0] = "324324";
        Assertions.assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void incorrectArgPutNothing() {
        Assertions.assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}