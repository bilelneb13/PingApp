package com.ip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TcpPingResult implements PingResult {
    String url;
    int responseCode;
    long responseTime;
    LocalDateTime timestamp;
}