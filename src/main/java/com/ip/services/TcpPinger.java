package com.ip.services;

import com.ip.App;
import com.ip.model.Reporter;
import com.ip.model.TcpPingResult;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class TcpPinger {
    private static final int MAX_RESPONSE_TIME = 1000; // Load from config

    public static void tcpPing(String host, int timeout) {
        try {
            long start = System.currentTimeMillis();

            HttpURLConnection conn = (HttpURLConnection) new URI(host).toURL().openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            long responseTime = System.currentTimeMillis() - start;

            TcpPingResult result = new TcpPingResult(host, responseCode, responseTime, LocalDateTime.now());
            App.results.put(host, result);

            if (responseTime > MAX_RESPONSE_TIME) {
                Reporter.report(host, "N/A", result.toString(), "N/A");
            }
        } catch (Exception e) {
            Reporter.report(host, "N/A", "Timeout or unreachable", "N/A");
        }
    }
}
