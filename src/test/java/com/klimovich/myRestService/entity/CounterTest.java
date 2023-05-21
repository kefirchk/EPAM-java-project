package com.klimovich.myRestService.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CounterTest {
    private final Counter counter = new Counter();

    @Test
    void testIncrementSynchronizedCounter()
    {
        counter.incrementSynchronizedCounter();
        assertEquals(1, counter.getSynchronizedCounter());
    }


    @Test
    void testIncrementSimpleCounter()
    {
        counter.incrementSimpleCounter();
        assertEquals(1, counter.getSimpleCounter());
    }
}
