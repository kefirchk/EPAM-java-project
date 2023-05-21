package com.klimovich.myRestService.service;

import com.klimovich.myRestService.entity.Counter;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class CounterService {
    private Counter counter = new Counter();

    public synchronized void incrementSynchronizedCounter() {
        counter.incrementSynchronizedCounter();
    }
    public void incrementSimpleCounter() {
        counter.incrementSimpleCounter();
    }
    public void setCounter(Integer synchronizedCounter, Integer simpleCounter)
    {
        counter.setSynchronizedCounter(0);
        counter.setSimpleCounter(0);
    }
}
