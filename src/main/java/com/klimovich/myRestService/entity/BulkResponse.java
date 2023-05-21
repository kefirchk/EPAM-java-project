package com.klimovich.myRestService.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkResponse {
    private List<MyResponse> requests = new ArrayList<>();
    private Integer minAnswer;
    private Integer maxAnswer;
    private Double averageAnswer;

    public void initValues(List<MyResponse> results)
    {
        requests = results;
        minAnswer = minAnswer(results);
        maxAnswer = maxAnswer(results);
        averageAnswer = averageAnswer(results);
    }

    public Integer minAnswer(@NotNull List<MyResponse> list) {
        List<Equation> equations = new ArrayList<>();
        list.forEach(response ->{
            if(response.getEquation() != null && response.getEquation().getAnswer() != null)
                equations.add(response.getEquation());
        });
        if(equations.isEmpty()) {
            return null;
        }
        return equations.stream().mapToInt(Equation::getAnswer).min().getAsInt();
    }

    public Integer maxAnswer(@NotNull List<MyResponse> list) {
        List<Equation> equations = new ArrayList<>();
        list.forEach(response -> {
            if(response.getEquation() != null && response.getEquation().getAnswer() != null)
                equations.add(response.getEquation());
        });
        if(equations.isEmpty()) {
            return null;
        }
        return equations.stream().mapToInt(Equation::getAnswer).max().getAsInt();
    }

    public Double averageAnswer(@NotNull List<MyResponse> list) {
        List<Equation> equations = new ArrayList<>();
        list.forEach(response -> {
            if(response.getEquation() != null && response.getEquation().getAnswer() != null)
                equations.add(response.getEquation());
        });
        if(equations.isEmpty()) {
            return null;
        }
        return equations.stream().mapToInt(Equation::getAnswer).average().getAsDouble();
    }

}
