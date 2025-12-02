package com.nisha;  // Add your package

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    public void testGreeting() {
        String expected = "Hello from CI/CD Pipeline with Jenkins + SonarQube + Docker!";
        assertEquals(expected, App.getGreeting());
    }
}
