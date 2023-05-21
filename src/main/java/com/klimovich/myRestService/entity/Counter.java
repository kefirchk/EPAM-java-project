package com.klimovich.myRestService.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Counter {
    private Integer synchronizedCounter;
    private Integer simpleCounter;

    public Counter()
    {
        synchronizedCounter = 0;
        simpleCounter = 0;
    }

    public Counter(Integer synchronizedCounter, Integer simpleCounter)
    {
        this.synchronizedCounter = synchronizedCounter;
        this.simpleCounter = simpleCounter;
    }

    public synchronized void incrementSynchronizedCounter() {
        ++synchronizedCounter;
    }

    public void incrementSimpleCounter() {
        ++simpleCounter;
    }
}
