package com.ip;

import com.ip.configs.Config;
import com.ip.configs.LogConfig;
import com.ip.model.PingResult;
import com.ip.services.IcmpPing;
import com.ip.services.TcpPing;
import com.ip.services.TraceRouter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class App {
    private static final List<String> HOSTS = Config.getHosts();

    public static final Map<String, PingResult> results = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        LogConfig.setupLogging(); // Initialize logging
        Logger logger = Logger.getLogger(App.class.getName());
        logger.info("Application started.");

        TcpPing tcpPing = new TcpPing();
        IcmpPing icmpPing = new IcmpPing();
        TraceRouter traceRouter = new TraceRouter();
        int delay = Config.getIcmpDelayProperty();
        int count = Config.getIcmpCountProperty();
        int timeoutTcp = Config.getTcpTimeout();
        int delayTcp = Config.getTcpDelay();
        int delayTrace = Config.getTraceDelay();
        int traceMax = Config.getMaxHops();

        ScheduledExecutorService schedulerIcmp = Executors.newScheduledThreadPool(HOSTS.size());
        ScheduledExecutorService schedulerTcp = Executors.newScheduledThreadPool(HOSTS.size());
        ScheduledExecutorService schedulerTrace = Executors.newScheduledThreadPool(HOSTS.size());

        for (String host : HOSTS) {
            schedulerIcmp.scheduleAtFixedRate(() -> icmpPing.icmpPing(count, host), 0, delay, TimeUnit.MILLISECONDS);
        }
        for (String host : HOSTS) {
            schedulerTcp.scheduleAtFixedRate(() -> tcpPing.tcpPing(host, timeoutTcp),
                                             0,
                                             delayTcp,
                                             TimeUnit.MILLISECONDS);
        }
        for (String host : HOSTS) {
            schedulerTrace.scheduleAtFixedRate(() -> traceRouter.traceRoute(host, traceMax),
                                               0,
                                               delayTrace,
                                               TimeUnit.MILLISECONDS);
        }

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> {
                    System.out.println("\nShutting down...");

                    // Print the results map
                    System.out.println("Results:");
                    App.results.forEach((host, result) -> System.out.printf("Host: %s, Result: %s%n", host, result));

                    // Shutdown executors immediately
                    schedulerIcmp.shutdownNow();
                    schedulerTcp.shutdownNow();
                    schedulerTrace.shutdownNow();

                    System.out.println("Shutdown complete.");
                }));
    }
}
