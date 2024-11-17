package com.ip.services;

import com.ip.App;
import com.ip.model.IcmpPingResult;
import com.ip.model.Reporter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IcmpPinger {

    private static final Logger logger = Logger.getLogger(IcmpPinger.class.getName());

    public static void icmpPing(int count, String host) {
        try {

            ProcessBuilder pingCmd = new ProcessBuilder(getOSCommand(count, host));
            Process process = pingCmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                        .append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            boolean success = !output.toString()
                    .contains("100% loss") && exitCode == 0;
            if (!success) {
                Reporter.report(host, "N/A", output.toString(), "N/A");
            }

            App.results.put(host, new IcmpPingResult(output.toString(), LocalDateTime.now()));
        } catch (Exception e) {
            Reporter.report(host, "Timeout or unreachable", "N/A", "N/A");


        }
    }

    private static String getOSCommand(int count, String host) {
        String os = System.getProperty("os.name").toLowerCase();
        String command = "";

        // Determine the appropriate ping command based on the OS
        if (os.contains("win")) {
            // Windows
            command = String.format("ping -n %d %s", count, host);
            return command;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Unix/Linux/macOS
            command = String.format("ping -c %d %s", count, host);
            return command;
        } else {
            System.out.println("Unsupported operating system: " + os);
            return command;
        }
    }
}
