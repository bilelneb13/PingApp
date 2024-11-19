package com.ip.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    private static final Properties PROPERTIES = loadConfig();
    private static final String HOSTS_KEY = "hosts";
    private static final String ICMP_DELAY_KEY = "icmp.delay";
    private static final String ICMP_COUNT_KEY = "icmp.count";
    private static final String TCP_TIMEOUT_KEY = "tcp.timeout";
    private static final String TCP_DELAY_KEY = "tcp.delay";
    private static final String TRACE_DELAY_KEY = "trace.delay";
    private static final String TRACE_MAX_HOPS = "trace.maxHops";
    private static final String DEFAULT_HOSTS = "jasmin.com,oranum.com";
    private static final String DEFAULT_DELAY = "5000";
    private static final String DEFAULT_COUNT = "5";

    public static Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = Config.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                System.err.println("Sorry, unable to find config.properties");
                return properties; // Return empty properties if file is not found
            }
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace(); // Handle error appropriately
        }
        return properties;
    }

    public static List<String> getHosts() {
        String hosts = PROPERTIES.getProperty(HOSTS_KEY, DEFAULT_HOSTS);
        return Arrays.asList(hosts.split(","));
    }

    public static int getIcmpDelayProperty() {
        return Integer.parseInt(PROPERTIES.getProperty(ICMP_DELAY_KEY, DEFAULT_DELAY)); // Default to 5000 ms
    }

    public static int getIcmpCountProperty() {
        return Integer.parseInt(PROPERTIES.getProperty(ICMP_COUNT_KEY, DEFAULT_COUNT)); // Default to 5
    }

    public static int getTcpTimeout() {
        return Integer.parseInt(PROPERTIES.getProperty(TCP_TIMEOUT_KEY, DEFAULT_DELAY)); // Default to 5000 ms
    }

    public static int getTcpDelay() {
        return Integer.parseInt(PROPERTIES.getProperty(TCP_DELAY_KEY, DEFAULT_DELAY)); // Default to 5000 ms
    }

    public static int getTraceDelay() {
        return Integer.parseInt(PROPERTIES.getProperty(TRACE_DELAY_KEY, DEFAULT_DELAY)); // Default to 5000 ms
    }

    public static int getMaxHops() {
        return Integer.parseInt(PROPERTIES.getProperty(TRACE_MAX_HOPS, DEFAULT_DELAY)); // Default to 5000 ms
    }
}