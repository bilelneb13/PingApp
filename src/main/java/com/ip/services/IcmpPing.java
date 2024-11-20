package com.ip.services;

import com.ip.App;
import com.ip.model.IcmpPingResult;
import com.ip.model.Reporter;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data

public class IcmpPing {

    static Logger logger = Logger.getLogger(IcmpPing.class.getName());

    private final Supplier<ProcessBuilder> processBuilderSupplier;


    public IcmpPing() {
        this.processBuilderSupplier = ProcessBuilder::new; // Use default behavior
    }

    // Constructor for testing or custom suppliers
    public IcmpPing(Supplier<ProcessBuilder> processBuilderSupplier) {
        this.processBuilderSupplier = processBuilderSupplier;
    }

    public void icmpPing(int count, String host) {
        try {
            logger.log(Level.INFO, "Pinging host: {0} with {1} attempts.", new Object[]{host, count});
            ProcessBuilder pingCmd = processBuilderSupplier.get().command("ping", getOSCommand(), String.valueOf(count), host);
            Process process = pingCmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                        .append(System.lineSeparator());
                //logger.log(Level.INFO, "Pinging to host {0}. result: {1}", new Object[]{host, output.toString()});
            }

            int exitCode = process.waitFor();
            boolean success = !output.toString()
                    .contains("100% loss") && exitCode == 0;
            if (!success) {
                logger.log(Level.INFO, "Ping failed for host {0}. Output: {1}", new Object[]{host, output.toString()});
                Reporter.report(host, App.results);
            } else {
                // Log the successful ping result
                logger.log(Level.INFO,
                           "Ping successful for host {0}. Output: {1}",
                           new Object[]{host, output.toString()});
            }

            App.results.put(host + "-ICMP",
                            IcmpPingResult.builder()
                                    .result(output.toString())
                                    .timestamp(LocalDateTime.now())
                                    .build());
            logger.log(Level.INFO, "Result for host {0}. Result: {1}", new Object[]{host, App.results});
        } catch (Exception e) {
            Reporter.report(host, App.results);
            logger.log(Level.SEVERE, "Error during ICMP ping to host: " + host, e);
        }
    }

    public String getOSCommand() {
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
