package com.dispassionproject.gutterball;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;

public class GutterballBanner implements Banner {

    private static final String BANNER = "\n" +
            " _______  __   __  _______  _______  _______  ______    _______  _______  ___      ___      __ \n" +
            "|       ||  | |  ||       ||       ||       ||    _ |  |  _    ||   _   ||   |    |   |    |  |\n" +
            "|    ___||  | |  ||_     _||_     _||    ___||   | ||  | |_|   ||  |_|  ||   |    |   |    |  |\n" +
            "|   | __ |  |_|  |  |   |    |   |  |   |___ |   |_||_ |       ||       ||   |    |   |    |  |\n" +
            "|   ||  ||       |  |   |    |   |  |    ___||    __  ||  _   | |       ||   |___ |   |___ |__|\n" +
            "|   |_| ||       |  |   |    |   |  |   |___ |   |  | || |_|   ||   _   ||       ||       | __ \n" +
            "|_______||_______|  |___|    |___|  |_______||___|  |_||_______||__| |__||_______||_______||__|\n" +
            "\n" +
            "Arguments: %s\n\n\n";

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        final String arguments = String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments());
        out.printf(BANNER, arguments);
    }

}
