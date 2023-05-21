package com.klimovich.myRestService.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsyncResponse {
    private List<String> messages = new ArrayList<>();
    private Integer futureId;

    public AsyncResponse(Integer id) {
        this.futureId = id;
    }
}
