package com.klimovich.myRestService.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Equation {
    private Pair numbers;
    private Pair segment;
    private Integer answer;
    public Equation(Pair numbers, Pair segment) {
        this.numbers = numbers;
        this.segment = segment;
    }
    public Equation(Pair numbers, Pair segment, Integer answer) {
        this.numbers = numbers;
        this.segment = segment;
        this.answer = answer;
    }
}
