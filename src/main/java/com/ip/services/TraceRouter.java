package com.ip.services;

import com.ip.App;
import com.ip.model.Reporter;
import com.ip.model.TraceResult;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
public class TraceRouter {
    static Logger logger = Logger.getLogger(TraceRouter.class.getName());
    private final Supplier<ProcessBuilder> processBuilderSupplier;

    public TraceRouter() {
        this.processBuilderSupplier = ProcessBuilder::new; // Use default behavior
    }

    // Constructor for testing or custom suppliers
    public TraceRouter(Supplier<ProcessBuilder> processBuilderSupplier) {
        this.processBuilderSupplier = processBuilderSupplier;
    }
    public void traceRoute(String host, int maxHops) {
        try {
            logger.log(Level.INFO, "Traceroute host: {0}", new Object[]{host});
            ProcessBuilder traceCmd = processBuilderSupplier.get().command(getOSCommand(), "-m", String.valueOf(maxHops), host);
            Process process = traceCmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                        .append(System.lineSeparator());
                logger.log(Level.INFO, "Traceroute for host {0}. \n {1}", new Object[]{host, output.toString()});
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Reporter.report(host, App.results);
            }
            logger.log(Level.INFO, "Traceroute for host {0}. {1}", new Object[]{host, output.toString()});
            TraceResult result = TraceResult.builder()
                    .result(output.toString())
                    .host(host)
                    .timestamp(LocalDateTime.now())
                    .build();
            App.results.put(host + "-TRACE", result);
            logger.log(Level.INFO, "Result for host {0}. Result: {1}", new Object[]{host, App.results});
        } catch (Exception e) {
            Reporter.report(host, App.results);
            logger.log(Level.SEVERE, "Error trace routing to host: " + host, e);
            System.out.println("Error during trace routing to host: " + host + ": " + e.getMessage());
        }
    }

    public String getOSCommand() {
        String os = System.getProperty("os.name")
                .toLowerCase();
        if (os.contains("win")) {
            return "tracert";
        } else {
            return "traceroute";
        }
    }
}
