package com.ip.services;

import com.ip.App;
import com.ip.model.Reporter;
import com.ip.model.TraceResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class TraceRouter {
    public static void traceRoute(String host) {
        try {
            String command = "tracert " + host; // Load from config for Windows/Linux differentiation
            Process process = Runtime.getRuntime()
                    .exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                        .append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Reporter.report(host, "N/A", "N/A", "Trace route failed");
            }

            TraceResult result = new TraceResult(host, output.toString(), LocalDateTime.now());
            App.results.put(host, result);
        } catch (Exception e) {
            Reporter.report(host, "N/A", "N/A", "Trace route error: " + e.getMessage());
        }
    }
}
