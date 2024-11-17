package com.ip.configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getHosts() {
        String hosts = properties.getProperty("hosts", "jasmin.com,oranum.com");
        return Arrays.asList(hosts.split(","));
    }
    public static int getIcmpDelayProperty() {
        return Integer.parseInt(properties.getProperty("icmp.delay", "5000")); // Default to 5000 ms
    }

    public static int getIcmpCountProperty() {
        return Integer.parseInt(properties.getProperty("icmp.count", "5")); // Default to 5000 ms
    }

    public static int getTcpTimeout() {
        return Integer.parseInt(properties.getProperty("tcp.timeout", "5000")); // Default to 5000 ms
    }
    public static int getTcpDelay() {
        return Integer.parseInt(properties.getProperty("tcp.delay", "5000")); // Default to 5000 ms
    }
}