package com.ip.configs;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ReportLogConfig {
    private static final String REPORT_LOG_FILE = "report.log";

    public static Logger getReportLogger() {
        Logger reportLogger = Logger.getLogger("ReportLogger");

        // Ensure the logger is configured only once
        if (reportLogger.getHandlers().length == 0) {
            try {
                // Set up a file handler for the reporting logger
                FileHandler fileHandler = new FileHandler(REPORT_LOG_FILE, true); // Append mode
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());

                // Configure the logger
                //reportLogger.setUseParentHandlers(false); // Do not propagate to root logger
                reportLogger.addHandler(fileHandler);
                reportLogger.setLevel(Level.ALL);
            } catch (IOException e) {
                Logger.getLogger(ReportLogConfig.class.getName())
                        .log(Level.SEVERE, "Failed to configure report logger", e);
            }
        }

        return reportLogger;
    }
}