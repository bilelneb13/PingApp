package com.ip.services;

import com.ip.App;
import com.ip.model.Reporter;
import com.ip.model.TcpPingResult;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@RequiredArgsConstructor
@Builder
public class TcpPing {
    private static final int MAX_RESPONSE_TIME = 1000;
    static Logger logger = Logger.getLogger(TcpPing.class.getName());

    public void tcpPing(String host, int timeout) {
        try {
            logger.log(Level.INFO, "Pinging TCP host: {0}", new Object[]{host});
            long start = System.currentTimeMillis();
            URL url = createUrl(host);
            HttpURLConnection conn = openConnection(url);
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
            logger.log(Level.INFO,
                       "TCP Ping successful for host {0}. Response code: {1}, Response time: {2} ms",
                       new Object[]{host, responseCode, responseTime});
            App.results.put(host + "-TCP", result);
            logger.log(Level.INFO, "Result for host {0}. Result: {1}", new Object[]{host, App.results});
            if (responseTime > MAX_RESPONSE_TIME) {
                Reporter.report(host, App.results);
                logger.log(Level.WARNING,
                           "TCP Ping response time exceeded for host {0}. Response time: {1} ms",
                           new Object[]{host, responseTime});
            }
        } catch (Exception e) {
            Reporter.report(host, App.results);
            logger.log(Level.SEVERE, "Error during TCP ping to host: " + host, e);
            System.out.println("Error during TCP ping to host: " + host + ": " + e.getMessage()); // Real-time output to console
        }
    }


    public HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    public URL createUrl(String host) throws MalformedURLException {
        return new URL("https://" + host);
    }

}
