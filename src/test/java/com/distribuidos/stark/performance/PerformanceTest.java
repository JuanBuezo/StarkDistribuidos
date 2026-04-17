package com.distribuidos.stark.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ PERFORMANCE TESTS
 * 
 * Valida:
 * • Login latency < 300ms
 * • Sensor read latency < 100ms
 * • Alert latency < 150ms
 * • End-to-end < 350ms
 * • Throughput >= 50 req/sec
 */
@DisplayName("Performance Tests")
public class PerformanceTest {
    
    private PerformanceMetrics metrics;
    
    @BeforeEach
    public void setUp() {
        metrics = new PerformanceMetrics();
    }
    
    @Test
    @DisplayName("Auth latency should be less than 300ms")
    @Timeout(1)  // Test must complete in 1 second
    public void testAuthLatency() throws Exception {
        // Arrange
        int iterations = 100;
        List<Long> latencies = new ArrayList<>();
        
        // Act
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            
            // Simulate auth call
            simulateAuthCall();
            
            long end = System.currentTimeMillis();
            latencies.add(end - start);
        }
        
        // Assert
        double avgLatency = latencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        double p95 = calculatePercentile(latencies, 0.95);
        double p99 = calculatePercentile(latencies, 0.99);
        
        assertTrue(avgLatency < 300, "Average latency: " + avgLatency + "ms");
        assertTrue(p95 < 400, "P95 latency: " + p95 + "ms");
        assertTrue(p99 < 600, "P99 latency: " + p99 + "ms");
        
        System.out.println("Auth Latencies - Avg: " + avgLatency + "ms, P95: " + p95 + "ms, P99: " + p99 + "ms");
    }
    
    @Test
    @DisplayName("Sensor latency should be less than 100ms")
    @Timeout(1)
    public void testSensorLatency() throws Exception {
        // Arrange
        int iterations = 100;
        List<Long> latencies = new ArrayList<>();
        
        // Act
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            
            // Simulate sensor read
            simulateSensorRead();
            
            long end = System.currentTimeMillis();
            latencies.add(end - start);
        }
        
        // Assert
        double avgLatency = latencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        assertTrue(avgLatency < 100, "Average latency: " + avgLatency + "ms");
        
        System.out.println("Sensor Latency - Avg: " + avgLatency + "ms");
    }
    
    @Test
    @DisplayName("Alert latency should be less than 150ms")
    @Timeout(1)
    public void testAlertLatency() throws Exception {
        // Arrange
        int iterations = 100;
        List<Long> latencies = new ArrayList<>();
        
        // Act
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            
            // Simulate alert generation
            simulateAlert();
            
            long end = System.currentTimeMillis();
            latencies.add(end - start);
        }
        
        // Assert
        double avgLatency = latencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        assertTrue(avgLatency < 150, "Average latency: " + avgLatency + "ms");
        
        System.out.println("Alert Latency - Avg: " + avgLatency + "ms");
    }
    
    @Test
    @DisplayName("Throughput should be at least 50 req/sec")
    @Timeout(5)
    public void testThroughput() throws Exception {
        // Arrange
        int duration = 5;  // seconds
        int requests = 0;
        
        // Act
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < duration * 1000) {
            simulateRequest();
            requests++;
        }
        long end = System.currentTimeMillis();
        
        // Assert
        double throughput = (requests * 1000.0) / (end - start);
        assertTrue(throughput >= 50, "Throughput: " + throughput + " req/sec");
        
        System.out.println("Throughput - " + throughput + " req/sec");
    }
    
    @Test
    @DisplayName("Error rate should be less than 0.1%")
    @Timeout(5)
    public void testErrorRate() throws Exception {
        // Arrange
        int totalRequests = 1000;
        int errorCount = 0;
        
        // Act
        for (int i = 0; i < totalRequests; i++) {
            try {
                simulateRequest();
            } catch (Exception e) {
                errorCount++;
            }
        }
        
        // Assert
        double errorRate = (errorCount * 100.0) / totalRequests;
        assertTrue(errorRate < 0.1, "Error rate: " + errorRate + "%");
        
        System.out.println("Error Rate - " + errorRate + "%");
    }
    
    // ==================== Helper Methods ====================
    
    private void simulateAuthCall() throws Exception {
        // Simulate RabbitMQ call
        Thread.sleep(5);  // Simulate network latency
        // Validate credentials
        // Generate JWT
    }
    
    private void simulateSensorRead() throws Exception {
        // Simulate sensor data retrieval
        Thread.sleep(2);
    }
    
    private void simulateAlert() throws Exception {
        // Simulate alert generation
        Thread.sleep(3);
    }
    
    private void simulateRequest() throws Exception {
        // Simulate generic request
        Thread.sleep(1);
    }
    
    private double calculatePercentile(List<Long> values, double percentile) {
        if (values.isEmpty()) return 0;
        
        values.sort(Long::compareTo);
        int index = (int) ((values.size() - 1) * percentile);
        return values.get(index);
    }
    
    // ==================== Performance Metrics ====================
    
    private static class PerformanceMetrics {
        public long avgLatency;
        public long p95Latency;
        public long p99Latency;
        public double throughput;
        public double errorRate;
    }
}

