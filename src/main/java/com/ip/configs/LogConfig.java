package com.ip.configs;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {
    private static final String LOG_FILE_PATH = "app.log";

    public static void setupLogging() throws IOException {
        LogManager.getLogManager().reset();

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);

        // Create a file handler
        FileHandler fileHandler = new FileHandler(LOG_FILE_PATH, true);
        fileHandler.setLevel(Level.INFO);
        fileHandler.setFormatter(new SimpleFormatter());

        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        rootLogger.addHandler(consoleHandler);
        rootLogger.addHandler(fileHandler);
    }
}