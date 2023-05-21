package com.klimovich.myRestService.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyResponseStatusTest {
    private final MyResponseStatus responseStatus = new MyResponseStatus();

    @Test
    void testAddMessage() {
        responseStatus.addMessage("status message");
        List<String> list = new ArrayList<>();
        list.add("status message");
        assertEquals(list, responseStatus.getMessages());
    }
}