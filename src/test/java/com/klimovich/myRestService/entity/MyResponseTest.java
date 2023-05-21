package com.klimovich.myRestService.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class MyResponseTest {
    private AutoCloseable closeable;
    @Mock
    private Equation equation;
    @Mock
    private MyResponseStatus responseStatus = new MyResponseStatus();
    @InjectMocks
    private MyResponse myResponse;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }


    @Test
    void testConstructor() {

        myResponse = new MyResponse(new MyResponseStatus(200, HttpStatus.OK));
        assertEquals(new MyResponse(new MyResponseStatus(200, HttpStatus.OK)), myResponse);
    }
}