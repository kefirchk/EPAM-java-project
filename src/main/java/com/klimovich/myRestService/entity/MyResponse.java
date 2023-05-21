package com.klimovich.myRestService.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyResponse {
    private Equation equation;
    private MyResponseStatus responseStatus = new MyResponseStatus();
    public MyResponse(MyResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
}
