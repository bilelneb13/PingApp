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
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@RequiredArgsConstructor
@Builder
public class TraceRouter {
    private static final Logger logger = Logger.getLogger(TraceRouter.class.getName());

    public static void traceRoute(String host, int maxHops) {
        try {
            logger.log(Level.INFO, "Traceroute host: {0}", new Object[]{host});
            ProcessBuilder traceCmd = new ProcessBuilder(getOSCommand() , "-m" ,String.valueOf(maxHops), host);
            Process process = traceCmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                        .append(System.lineSeparator());
                logger.log(Level.FINE, "Traceroute for host {0}. \n {1}", new Object[]{host, output.toString()});
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
            App.results.put(host + "-TRACE",result);
        } catch (Exception e) {
            Reporter.report(host, App.results);
            logger.log(Level.SEVERE, "Error trace routing to host: " + host, e);
            System.out.println("Error during trace routing to host: " + host + ": " + e.getMessage());
        }
    }

    private static String getOSCommand() {
        String os = System.getProperty("os.name")
                .toLowerCase();
        if (os.contains("win")) {
            return "tracert";
        } else {
            return "traceroute";
        }
    }
}
