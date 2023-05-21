package com.klimovich.myRestService.memory;

import com.klimovich.myRestService.entity.Equation;
import com.klimovich.myRestService.entity.MyResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Cache {
    private final Map<Equation, MyResponse> dataStorage = new HashMap<Equation, MyResponse>();

    public void saveEquation(@NotNull MyResponse response) {
        Equation equation = new Equation();
        equation.setNumbers(response.getEquation().getNumbers());
        equation.setSegment(response.getEquation().getSegment());
        equation.setAnswer(null);
        dataStorage.put(equation, response);
    }
    public MyResponse getResponse(Equation equation) {
        return dataStorage.get(equation);
    }
    public Integer getCacheSize() {
        return dataStorage.size();
    }
    public List<MyResponse> getAllSavedEquations() {
        List<MyResponse> listOfResponses = new ArrayList<MyResponse>();
        dataStorage.forEach((key, value) -> listOfResponses.add(value));
        return listOfResponses;
    }
}
