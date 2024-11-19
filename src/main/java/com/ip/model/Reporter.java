package com.ip.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.*;


@Data
@RequiredArgsConstructor
@Builder
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

    public static void report(String host, Map map) {
        String payload = String.format("{\"host\":\"%s\",\"icmp_ping\":\"%s\",\"tcp_ping\":\"%s\",\"trace\":\"%s\"}",
                                       host,
                                       map.containsKey(host + "-ICMP") ? map.get(host + "-ICMP") : "N/A",
                                       map.containsKey(host + "-TCP") ? map.get(host + "-TCP") : "N/A",
                                       map.containsKey(host + "-TRACE") ? map.get(host + "-TRACE") : "N/A");

        try {
            URL url = new URL(REPORT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }
            logger.warning("report to  " + REPORT_URL + "with payload " +  payload);
        } catch (Exception e) {
            logger.warning("Error reporting for host " + host + ": " + e.getMessage());
        }
    }
}