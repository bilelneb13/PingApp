package com.ip.services;

import com.ip.App;
import com.ip.model.TcpPingResult;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TcpPingTest {

    @Test
    public void testTcpPing_successfulPing() throws Exception {
        // Arrange
        HttpURLConnection mockedConnection = mock(HttpURLConnection.class);
        TcpPing tcpPing = spy(TcpPing.builder()
                                      .build());
        doReturn(new URL("https://example.com")).when(tcpPing)
                .createUrl("example.com");

        when(mockedConnection.getResponseCode()).thenReturn(200);
        when(mockedConnection.getResponseMessage()).thenReturn("OK");
        doReturn(mockedConnection).when(tcpPing)
                .openConnection(any(URL.class));

        // Act
        tcpPing.tcpPing("example.com", 1000);

        // Assert
        assertTrue(App.results.containsKey("example.com-TCP"));
        TcpPingResult result = (TcpPingResult) App.results.get("example.com-TCP");
        assertNotNull(result);
        assertEquals(200, result.getResponseCode());
        assertTrue(result.getResponseTime() >= 0);
    }

    @Test
    public void testTcpPing_timeout() throws Exception {
        // Arrange
        HttpURLConnection mockedConnection = mock(HttpURLConnection.class);
        TcpPing tcpPing = spy(TcpPing.builder()
                                      .build());
        doReturn(new URL("https://example.com")).when(tcpPing)
                .createUrl("example.com");

        when(mockedConnection.getResponseCode()).thenThrow(new SocketTimeoutException("Connection timed out"));
        doReturn(mockedConnection).when(tcpPing)
                .openConnection(any(URL.class));

        // Act
        tcpPing.tcpPing("example.com", 1000);

        // Assert
        assertFalse(App.results.containsKey("example.com-TCP")); // No valid result added
        // Ensure Reporter.report was called (optional verification)
    }

    @Test
    public void testTcpPing_slowResponse() throws Exception {
        // Arrange
        HttpURLConnection mockedConnection = mock(HttpURLConnection.class);
        TcpPing tcpPing = spy(TcpPing.builder()
                                      .build());
        doReturn(new URL("https://example.com")).when(tcpPing)
                .createUrl("example.com");

        when(mockedConnection.getResponseCode()).thenReturn(200);
        doReturn(mockedConnection).when(tcpPing)
                .openConnection(any(URL.class));

        // Simulate a slow response
        doAnswer(invocation -> {
            Thread.sleep(1500); // Delay to simulate slow response
            return 200;
        }).when(mockedConnection)
                .getResponseCode();

        // Act
        tcpPing.tcpPing("example.com", 2000);

        // Assert
        assertTrue(App.results.containsKey("example.com-TCP"));
        TcpPingResult result = (TcpPingResult) App.results.get("example.com-TCP");
        assertNotNull(result);
        assertTrue(result.getResponseTime() > 1000); // Response time exceeds threshold
    }

    @Test
    public void testTcpPing_malformedUrl() throws MalformedURLException {
        // Arrange
        TcpPing tcpPing = spy(TcpPing.builder()
                                      .build());
        doThrow(new MalformedURLException("Malformed URL")).when(tcpPing)
                .createUrl("invalid-url");

        // Act
        tcpPing.tcpPing("invalid-url", 1000);

        // Assert
        assertFalse(App.results.containsKey("invalid-url-TCP")); // No valid result added
    }

    @Test
    public void testCreateUrl() throws MalformedURLException {
        // Arrange
        TcpPing tcpPing = TcpPing.builder()
                .build();

        // Act
        URL url = tcpPing.createUrl("example.com");

        // Assert
        assertEquals("https://example.com", url.toString());
    }
}