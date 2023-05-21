package com.klimovich.myRestService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyResponseStatus {
    private int statusCode;
    private HttpStatus status;
    private List<String> messages = new ArrayList<>();
    public MyResponseStatus(int statusCode, HttpStatus status) {
        this.statusCode = statusCode;
        this.status = status;
    }
    public void addMessage(String message){
        messages.add(message);
    }
}
