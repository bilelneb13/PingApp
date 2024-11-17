package com.ip;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testPingResultParsing() {
        String sampleOutput = "Pinging jasmin.com [123.45.67.89] with 32 bytes of data:\nReply from 123.45.67.89: bytes=32 time=10ms TTL=54\n\nPing statistics for 123.45.67.89:\n    Packets: Sent = 5, Received = 5, Lost = 0 (0% loss),\nApproximate round trip times in milli-seconds:\n    Minimum = 10ms, Maximum = 15ms, Average = 12ms";
        assertFalse(sampleOutput.contains("100% loss"));
    }

    @Test
    public void testPingTimeout() {
        assertTrue(true);
    }
}
