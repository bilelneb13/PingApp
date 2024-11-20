package com.ip.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ip.App;
import com.ip.model.IcmpPingResult;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.*;
import java.util.function.Supplier;
import java.util.logging.*;

public class IcmpPingTest {

    @Test
    void testIcmpPing_withSuccessfulPing() throws Exception {
        // Arrange
        Process mockedProcess = mock(Process.class);
        BufferedReader mockedReader = mock(BufferedReader.class);
        InputStream mockedInputStream = new ByteArrayInputStream("Reply from example.com: bytes=32 time=20ms TTL=56".getBytes());

        // Simulate Process behavior
        when(mockedProcess.getInputStream()).thenReturn(mockedInputStream);
        when(mockedProcess.waitFor()).thenReturn(0);

        // Mock ProcessBuilder behavior
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockedProcessBuilder); // Ensure it returns itself
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);

        // Inject the mock ProcessBuilder
        IcmpPing icmpPing = new IcmpPing(() -> mockedProcessBuilder);

        // Act
        icmpPing.icmpPing(4, "example.com");

        // Assert
        assertTrue(App.results.containsKey("example.com-ICMP"));
    }
    @Test
    public void testIcmpPing_hostUnreachable() throws Exception {
        // Arrange
        Process mockedProcess = mock(Process.class);
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);

        InputStream mockedInputStream = new ByteArrayInputStream("Request timed out.".getBytes());

        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedProcess.getInputStream()).thenReturn(mockedInputStream);
        when(mockedProcess.waitFor()).thenReturn(1); // Non-zero exit code for failure

        IcmpPing icmpPing = new IcmpPing(() -> mockedProcessBuilder);

        // Act
        icmpPing.icmpPing(4, "nonexistent.example.com");

        // Assert
        assertTrue(App.results.containsKey("nonexistent.example.com-ICMP"));
        IcmpPingResult result = (IcmpPingResult) App.results.get("nonexistent.example.com-ICMP");
        assertNotNull(result);
        assertTrue(result.getResult().contains("Request timed out."));
    }
    @Test
    public void testGetOSCommand_forWindows() {
        // Arrange
        System.setProperty("os.name", "Windows 10");

        IcmpPing icmpPing = new IcmpPing();

        // Act
        String command = icmpPing.getOSCommand();

        // Assert
        assertEquals("-n", command);
    }
    @Test
    public void testGetOSCommand_forUnix() {
        // Arrange
        System.setProperty("os.name", "Linux");

        IcmpPing icmpPing = new IcmpPing();

        // Act
        String command = icmpPing.getOSCommand();

        // Assert
        assertEquals("-c", command);
    }
    @Test
    public void testIcmpPing_withException() throws Exception {
        // Arrange
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);

        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenThrow(new IOException("Mocked IOException"));

        IcmpPing icmpPing = new IcmpPing(() -> mockedProcessBuilder);

        // Act
        icmpPing.icmpPing(4, "example.com");

        // Assert
        assertFalse(App.results.containsKey("example.com-ICMP")); // No result should be added
    }
    @Test
    public void testIcmpPing_withMultilineOutput() throws Exception {
        // Arrange
        Process mockedProcess = mock(Process.class);
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);

        String multilineOutput = """
            Pinging example.com [93.184.216.34] with 32 bytes of data:
            Reply from 93.184.216.34: bytes=32 time=20ms TTL=56
            Reply from 93.184.216.34: bytes=32 time=22ms TTL=56
            Reply from 93.184.216.34: bytes=32 time=21ms TTL=56
            Reply from 93.184.216.34: bytes=32 time=19ms TTL=56
            """;

        InputStream mockedInputStream = new ByteArrayInputStream(multilineOutput.getBytes());

        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedProcess.getInputStream()).thenReturn(mockedInputStream);
        when(mockedProcess.waitFor()).thenReturn(0); // Successful exit code

        IcmpPing icmpPing = new IcmpPing(() -> mockedProcessBuilder);

        // Act
        icmpPing.icmpPing(4, "example.com");

        // Assert
        assertTrue(App.results.containsKey("example.com-ICMP"));
        IcmpPingResult result = (IcmpPingResult) App.results.get("example.com-ICMP");
        assertNotNull(result);
        assertTrue(result.getResult().contains("Pinging example.com"));
        assertTrue(result.getResult().contains("Reply from 93.184.216.34"));
    }
}
