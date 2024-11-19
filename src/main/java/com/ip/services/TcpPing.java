package com.ip.services;

import com.ip.App;
import com.ip.model.IcmpPingResult;
import com.ip.model.Reporter;
import com.ip.model.TcpPingResult;
import lombok.*;

import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@RequiredArgsConstructor
@Builder
public class TcpPing {
    private static final int MAX_RESPONSE_TIME = 1000; // Load from config
    private static final Logger logger = Logger.getLogger(TcpPing.class.getName());

    public static void tcpPing(String host, int timeout) {
        try {
            logger.log(Level.INFO, "Pinging TCP host: {0}", new Object[]{host});
            long start = System.currentTimeMillis();

            HttpURLConnection conn = (HttpURLConnection) new URI("https://" + host).toURL()
                    .openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            long responseTime = System.currentTimeMillis() - start;

            TcpPingResult result = TcpPingResult.builder()
                    .url(host)
                    .responseCode(responseCode)
                    .responseTime(responseTime)
                    .timestamp(LocalDateTime.now())
                    .build();
            // Log the TCP ping result
            logger.log(Level.FINE, "TCP Ping successful for host {0}. Response code: {1}, Response time: {2} ms",
                       new Object[]{host, responseCode, responseTime});
            App.results.put(host + "-TCP",result);
            if (responseTime > MAX_RESPONSE_TIME) {
                Reporter.report(host, App.results);
                logger.log(Level.WARNING, "TCP Ping response time exceeded for host {0}. Response time: {1} ms",
                           new Object[]{host, responseTime});
            }
        } catch (Exception e) {
            Reporter.report(host, App.results);
            logger.log(Level.SEVERE, "Error during TCP ping to host: " + host, e);
            System.out.println("Error during TCP ping to host: " + host + ": " + e.getMessage()); // Real-time output to console
        }
    }
}
