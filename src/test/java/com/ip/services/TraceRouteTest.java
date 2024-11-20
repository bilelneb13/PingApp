package com.ip.services;

import com.ip.App;
import com.ip.model.TraceResult;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TraceRouteTest {

    @Test
    public void testTraceRoute_successfulTrace() throws Exception {
        // Arrange
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        Process mockedProcess = mock(Process.class);
        Supplier<ProcessBuilder> mockProcessBuilderSupplier = () -> mockedProcessBuilder;

        // Simulated output of the traceroute command
        String simulatedTraceOutput = "Tracing route to example.com\n1 192.168.1.1\n2 10.0.0.1\n3 example.com";
        InputStream simulatedInputStream = new ByteArrayInputStream(simulatedTraceOutput.getBytes(StandardCharsets.UTF_8));
        when(mockedProcess.getInputStream()).thenReturn(simulatedInputStream);
        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString())).thenReturn(
                mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedProcess.waitFor()).thenReturn(0); // Simulate successful execution

        TraceRouter traceRouter = new TraceRouter(mockProcessBuilderSupplier);

        // Act
        traceRouter.traceRoute("example.com", 30);

        // Assert
        assertTrue(App.results.containsKey("example.com-TRACE"));
        TraceResult result = (TraceResult) App.results.get("example.com-TRACE");
        assertNotNull(result);
        assertEquals(simulatedTraceOutput,
                     result.getResult()
                             .trim());
        assertEquals("example.com", result.getHost());
    }

    @Test
    public void testTraceRoute_unsuccessfulTrace() throws Exception {
        // Arrange
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        Process mockedProcess = mock(Process.class);
        Supplier<ProcessBuilder> mockProcessBuilderSupplier = () -> mockedProcessBuilder;

        // Simulated output of the traceroute command
        String simulatedTraceOutput = "Tracing route to example.com\nRequest Timed Out";
        InputStream simulatedInputStream = new ByteArrayInputStream(simulatedTraceOutput.getBytes(StandardCharsets.UTF_8));
        when(mockedProcess.getInputStream()).thenReturn(simulatedInputStream);
        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString())).thenReturn(
                mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedProcess.waitFor()).thenReturn(1); // Simulate failure (non-zero exit code)

        TraceRouter traceRouter = new TraceRouter(mockProcessBuilderSupplier);

        // Act
        traceRouter.traceRoute("example.com", 30);

        // Assert
        assertTrue(App.results.containsKey("example.com-TRACE"));
        TraceResult result = (TraceResult) App.results.get("example.com-TRACE");
        assertNotNull(result);
        assertEquals(simulatedTraceOutput,
                     result.getResult()
                             .trim());
        assertEquals("example.com", result.getHost());
        // Check if a report was triggered due to failure
    }

    @Test
    public void testTraceRoute_emptyOutput() throws Exception {
        // Arrange
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        Process mockedProcess = mock(Process.class);
        Supplier<ProcessBuilder> mockProcessBuilderSupplier = () -> mockedProcessBuilder;

        // Simulated empty output of the traceroute command
        String simulatedTraceOutput = "";
        InputStream simulatedInputStream = new ByteArrayInputStream(simulatedTraceOutput.getBytes(StandardCharsets.UTF_8));
        when(mockedProcess.getInputStream()).thenReturn(simulatedInputStream);
        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString())).thenReturn(
                mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedProcess.waitFor()).thenReturn(0); // Simulate successful execution

        TraceRouter traceRouter = new TraceRouter(mockProcessBuilderSupplier);

        // Act
        traceRouter.traceRoute("example.com", 30);

        // Assert
        assertTrue(App.results.containsKey("example.com-TRACE"));
        TraceResult result = (TraceResult) App.results.get("example.com-TRACE");
        assertNotNull(result);
        assertEquals(simulatedTraceOutput,
                     result.getResult()
                             .trim());
        assertEquals("example.com", result.getHost());
    }

    @Test
    public void testTraceRoute_exceptionHandling() throws Exception {
        // Arrange
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        Process mockedProcess = mock(Process.class);
        Supplier<ProcessBuilder> mockProcessBuilderSupplier = () -> mockedProcessBuilder;

        // Simulate an IOException when starting the process
        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString())).thenReturn(
                mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenThrow(new IOException("Simulated IOException"));

        TraceRouter traceRouter = new TraceRouter(mockProcessBuilderSupplier);

        // Act
        traceRouter.traceRoute("example.com", 30);

    }

    @Test
    public void testGetOSCommand() {
        // Arrange
        TraceRouter traceRouter = new TraceRouter(() -> new ProcessBuilder());

        System.setProperty("os.name", "Windows 10");
        assertEquals("tracert", traceRouter.getOSCommand());

        System.setProperty("os.name", "Linux");
        assertEquals("traceroute", traceRouter.getOSCommand());
    }

    @Test
    public void testTraceRoute_maxHops() throws Exception {
        // Arrange
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        Process mockedProcess = mock(Process.class);
        Supplier<ProcessBuilder> mockProcessBuilderSupplier = () -> mockedProcessBuilder;

        // Simulate output of the traceroute command
        String simulatedTraceOutput = "Tracing route to example.com\n1 192.168.1.1\n2 10.0.0.1\n3 example.com";
        InputStream simulatedInputStream = new ByteArrayInputStream(simulatedTraceOutput.getBytes(StandardCharsets.UTF_8));
        when(mockedProcess.getInputStream()).thenReturn(simulatedInputStream);
        when(mockedProcessBuilder.command(anyString(), anyString(), anyString(), anyString())).thenReturn(
                mockedProcessBuilder);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedProcess.waitFor()).thenReturn(0); // Simulate successful execution

        TraceRouter traceRouter = new TraceRouter(mockProcessBuilderSupplier);

        // Act
        traceRouter.traceRoute("example.com", 50);  // Set a custom maxHops

        // Assert
        verify(mockedProcessBuilder).command("traceroute", "-m", "50", "example.com");  // Ensure correct command
    }
}
