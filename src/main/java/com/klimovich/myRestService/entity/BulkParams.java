package com.klimovich.myRestService.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.klimovich.myRestService.validation.MyValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkParams {
    private String a;
    private String b;
    private String begin;
    private String end;

    public MyResponse parseParameters()
    {
        MyValidator validator = new MyValidator();
        Equation equation = new Equation();
        MyResponse response = new MyResponse();

        if(validator.isIntNumber(a) &&
                validator.isIntNumber(b) &&
                validator.isIntNumber(begin) &&
                validator.isIntNumber(end))
        {
            equation.setNumbers(new Pair(Integer.parseInt(a), Integer.parseInt(b)));
            equation.setSegment(new Pair(Integer.parseInt(begin), Integer.parseInt(end)));
            response.setEquation(equation);
        } else {
            response.setEquation(null);
            response.setResponseStatus(new MyResponseStatus(400, HttpStatus.BAD_REQUEST));
            response.getResponseStatus().addMessage("Cannot be converted to an Integer");
        }
        return response;
    }
}
