package com.klimovich.myRestService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class Pair {
    private Integer first;
    private Integer second;

    public Pair()
    {
        first = 0;
        second = 0;
    }
}
