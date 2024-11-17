package com.ip;

import com.ip.configs.Config;
import com.ip.model.PingResult;
import com.ip.services.IcmpPinger;
import com.ip.services.TcpPinger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {
    private static final List<String> HOSTS = Config.getHosts();

    public static final Map<String, PingResult> results = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        // Schedule Icmp Pings
/*        new Thread(() -> {
            while (true) {
                HOSTS.forEach(host -> new Thread(() -> IcmpPinger.icmpPing("http://" + host)).start());
                try {
                    Thread.sleep(10000); // Load from config
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        int delay = Config.getIcmpDelayProperty(); // Get delay from config
        int count = Config.getIcmpCountProperty(); // Get count from config
        ScheduledExecutorService schedulerIcmp = Executors.newScheduledThreadPool(HOSTS.size());
        ScheduledExecutorService schedulerTcp = Executors.newScheduledThreadPool(HOSTS.size());
        try {

            for (String host : HOSTS) {
                schedulerIcmp.scheduleAtFixedRate(() -> IcmpPinger.icmpPing(count, host),
                                                  0,
                                                  delay,
                                                  TimeUnit.MILLISECONDS);
            }

/*        // Schedule TCP/IP Pings
        new Thread(() -> {
            while (true) {
                HOSTS.forEach(host -> new Thread(() -> TcpPinger.tcpPing("http://" + host)).start());
                try {
                    Thread.sleep(10000); // Load from config
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
            int timeout = Config.getTcpTimeout(); // Get delay from config
            int delayTcp = Config.getTcpDelay(); // Get delay from config

            for (String host : HOSTS) {
                schedulerTcp.scheduleAtFixedRate(() -> TcpPinger.tcpPing(host, timeout),
                                                 0,
                                                 delayTcp,
                                                 TimeUnit.MILLISECONDS);
            }
        } finally {
            schedulerIcmp.shutdown();
            schedulerTcp.shutdown();
        }
        /*// Schedule Trace Routes
        new Thread(() -> {
            while (true) {
                HOSTS.forEach(host -> new Thread(() -> TraceRouter.traceRoute(host)).start());
                try {
                    Thread.sleep(15000); // Load from config
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }
}
