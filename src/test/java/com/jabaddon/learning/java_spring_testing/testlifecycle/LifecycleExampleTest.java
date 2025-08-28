package com.jabaddon.learning.java_spring_testing.testlifecycle;

import org.junit.jupiter.api.*;

/*
 * mvn test -Dtest=LifecycleExampleTest
 */
public class LifecycleExampleTest {

    @BeforeEach
    void setUp() {
        System.out.println("Setting up before each test...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Tearing down after each test...");
    }

    @BeforeAll
    static void initAll() {
        System.out.println("# Initializing before all tests...");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("# Cleaning up after all tests...");
    }

    @Test
    void exampleTest1() {
        System.out.println("\t> Running example test 1...");
    }

    @Test
    void exampleTest2() {
        System.out.println("\t> Running example test 2...");
    }
}
