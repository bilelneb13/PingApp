package com.ip.model;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;

public class Reporter {
    private static final Logger logger = Logger.getLogger(Reporter.class.getName());
    private static final String REPORT_URL = "http://example.com/report"; // Load from config
    private static final String LOG_FILE = "ping-report.log"; // Load from config

    static {
        try {
            LogManager.getLogManager()
                    .reset();
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.WARNING);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.WARNING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void report(String host, String icmpPing, String tcpPing, String trace) {
        String payload = String.format("{\"host\":\"%s\",\"icmp_ping\":\"%s\",\"tcp_ping\":\"%s\",\"trace\":\"%s\"}",
                                       host,
                                       icmpPing,
                                       tcpPing,
                                       trace);

        try {
            URL url = new URL(REPORT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.warning("Failed to report for host " + host + ". HTTP Response Code: " + responseCode);
            } else {
                logger.warning("Report successfully sent for host " + host);
            }
        } catch (Exception e) {
            logger.warning("Error reporting for host " + host + ": " + e.getMessage());
        }
    }
}