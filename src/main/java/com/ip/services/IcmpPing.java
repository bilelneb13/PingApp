package com.ip.services;

import com.ip.App;
import com.ip.model.IcmpPingResult;
import com.ip.model.Reporter;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@RequiredArgsConstructor
@Builder
public class IcmpPing {

    private static final Logger logger = Logger.getLogger(IcmpPing.class.getName());

    public static void icmpPing(int count, String host) {
        try {
            logger.log(Level.INFO, "Pinging host: {0} with {1} attempts.", new Object[]{host, count});
            ProcessBuilder pingCmd = new ProcessBuilder("ping" , getOSCommand() ,String.valueOf(count), host);
            Process process = pingCmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                        .append(System.lineSeparator());
                logger.log(Level.FINE, "Pinging to host {0}. result: {1}", new Object[]{host, output.toString()});
            }

            int exitCode = process.waitFor();
            boolean success = !output.toString()
                    .contains("100% loss") && exitCode == 0;
            if (!success) {
                Reporter.report(host, App.results);
                logger.log(Level.WARNING, "Ping failed for host {0}. Output: {1}", new Object[]{host, output.toString()});
            }else {
                // Log the successful ping result
                logger.log(Level.INFO, "Ping successful for host {0}. Output: {1}", new Object[]{host, output.toString()});
            }

            App.results.put(host + "-ICMP",
                            IcmpPingResult.builder()
                                    .result(output.toString())
                                    .timestamp(LocalDateTime.now())
                                    .build());
        } catch (Exception e) {
            Reporter.report(host, App.results);
            logger.log(Level.SEVERE, "Error during ICMP ping to host: " + host, e);
        }
    }

    private static String getOSCommand() {
        String os = System.getProperty("os.name")
                .toLowerCase();
        String command = "";
        if (os.contains("win")) {
            return "-n";  // Windows uses '-n' for number of pings
        } else {
            return "-c";  // Unix-based systems use '-c' for number of pings
        }
    }
}
