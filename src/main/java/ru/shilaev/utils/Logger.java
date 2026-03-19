package ru.shilaev.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/*
    Логирование в консоль
 */
public class Logger {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void info(String message) {
        log("INFO", BLUE, message);
    }

    public static void success(String message) {
        log("OK", GREEN, message);
    }

    public static void warning(String message) {
        log("WARN", YELLOW, message);
    }

    public static void error(String message) {
        log("ERROR", RED, message);
    }

    public static void debug(String message) {
        log("DEBUG", CYAN, message);
    }

    public static void step(String message) {
        System.out.println("\n" + "─".repeat(50));
        log("STEP", CYAN, message);
        System.out.println("─".repeat(50));
    }

    public static void header(String message) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  " + message);
        System.out.println("=".repeat(50));
    }

    public static void subStep(String message) {
        System.out.println("  " + message);
    }

    private static void log(String level, String color, String message) {
        String time = LocalTime.now().format(TIME_FORMATTER);
        System.out.println(color + time + " [" + level + "] " + message + RESET);
    }
}