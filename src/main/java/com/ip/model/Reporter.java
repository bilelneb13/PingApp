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
    static Logger logger = Logger.getLogger(Reporter.class.getName());
    private static final String REPORT_URL = "http://example.com/report";


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
            logger.log(Level.WARNING,"Reporting for host: " + host + ", Payload: " + payload);
        } catch (Exception e) {
            logger.warning("Error reporting for host " + host + ": " + e.getMessage());
        }
    }
}