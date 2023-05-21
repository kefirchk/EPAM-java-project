package com.klimovich.myRestService.controller;

import com.klimovich.myRestService.database.DBEquation;
import com.klimovich.myRestService.entity.*;
import com.klimovich.myRestService.exception.NotFoundSolutionException;
import com.klimovich.myRestService.memory.Cache;
import com.klimovich.myRestService.memory.CacheCharacteristics;
import com.klimovich.myRestService.service.CounterService;
import com.klimovich.myRestService.service.DataBaseService;
import com.klimovich.myRestService.service.EquationSolverService;
import com.klimovich.myRestService.validation.MyValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class EquationControllerTest {

    private AutoCloseable closeable;
    @Mock
    private MyValidator validator;
    @Mock
    private EquationSolverService equationSolverService;
    @Mock
    private Cache cache;
    @Mock
    private CounterService counterService;
    @Mock
    private DataBaseService dataBaseService;
    @InjectMocks
    private EquationController controller;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }


    @Test
    void testWhenCacheHasNotEquation() throws NotFoundSolutionException {
        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        MyResponse response = new MyResponse();
        Equation equation = new Equation(new Pair(1, 2), new Pair(0, 3));

        when(cache.getResponse(equation)).thenReturn(null);
        when(validator.isValidEquation(equation))
                .thenReturn(new MyResponseStatus(200, HttpStatus.OK));
        response.setResponseStatus(validator.isValidEquation(equation));
        when(equationSolverService.solve(equation))
                .thenReturn(new Equation(new Pair(1, 2), new Pair(0, 3), 1));
        response.setEquation(equationSolverService.solve(equation));
        doNothing().when(dataBaseService).saveEquationInDataBase(response.getEquation());
        doNothing().when(cache).saveEquation(response);

        assertEquals(controller.solveEquation("1", "2", "0", "3"),
                new ResponseEntity<>(new MyResponse(
                        new Equation(new Pair(1, 2), new Pair(0, 3), 1),
                        new MyResponseStatus(200, HttpStatus.OK)
                ), HttpStatus.OK));
        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
        verify(cache, times(1)).saveEquation(response);
        verify(dataBaseService, times(1)).saveEquationInDataBase(response.getEquation());
    }


    @Test
    void testWhenCacheHasEquation() throws NotFoundSolutionException {
        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        Equation equation = new Equation(new Pair(1, 2), new Pair(0, 3));

        when(cache.getResponse(equation)).thenReturn(
                new MyResponse(
                        new Equation(new Pair(1, 2), new Pair(0, 3), 1),
                        new MyResponseStatus(200, HttpStatus.OK))
        );
        assertEquals(controller.solveEquation("1", "2", "0", "3"),
                new ResponseEntity<>(new MyResponse(
                        new Equation(new Pair(1, 2), new Pair(0, 3), 1),
                        new MyResponseStatus(200, HttpStatus.OK)
                ), HttpStatus.OK));
        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
    }


    @Test
        //catching exception: begin > end
    void testCatchException() throws NotFoundSolutionException {
        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        MyResponse response = new MyResponse();
        Equation equation = new Equation(new Pair(1, 2), new Pair(3, 0)); //incorrect equation

        when(cache.getResponse(equation)).thenReturn(null);
        when(validator.isValidEquation(equation))
                .thenReturn(new MyResponseStatus(500, HttpStatus.INTERNAL_SERVER_ERROR, List.of("Parameter 'begin' cannot be greater than parameter 'end'")));
        response.setResponseStatus(validator.isValidEquation(equation));
        assertEquals(controller.solveEquation("1", "2", "3", "0"),
                new ResponseEntity<>(new MyResponse(
                        null,
                        new MyResponseStatus(500, HttpStatus.INTERNAL_SERVER_ERROR, List.of("Parameter 'begin' cannot be greater than parameter 'end'"))
                ), HttpStatus.INTERNAL_SERVER_ERROR));
        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
    }


    @Test
        //cache size = 1
    void testGetCacheSize() {
        when(cache.getCacheSize()).thenReturn(1);
        assertEquals(new CacheCharacteristics(1), controller.getCacheCharacteristics());
    }


    @Test
    void testGetAllQueriesOfTheCache() {
        List<MyResponse> list = new ArrayList<>();
        list.add(new MyResponse(
                new Equation(new Pair(1, 2), new Pair(0, 3), 1),
                new MyResponseStatus(200, HttpStatus.OK))
        );
        when(cache.getAllSavedEquations()).thenReturn(list);
        assertEquals(controller.getCache(), list);
    }


    @Test
    void testGetCounterValue() {
        when(counterService.getCounter()).thenReturn(new Counter(1, 1));
        assertEquals(controller.getCounterValue(), new Counter(1, 1));
    }


    @Test
    void testCleanCounterValue() {
        doNothing().when(counterService).setCounter(0, 0);
        when(counterService.getCounter()).thenReturn(new Counter());
        assertEquals(controller.cleanCounterValue(), new Counter());
        verify(counterService, times(1)).setCounter(0, 0);
    }


    @Test
    void testSolveBulkEquationWhenCacheHasResponse() throws NotFoundSolutionException {
        List<BulkParams> paramList = new ArrayList<>();
        paramList.add(new BulkParams("1", "2", "0", "4"));
        Equation equation = new Equation(new Pair(1, 2), new Pair(0, 4));
        MyResponse response = new MyResponse(equation, new MyResponseStatus(201, HttpStatus.CREATED));

        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        List<MyResponse> responses = new ArrayList<>();
        List<MyResponse> results = new ArrayList<>();
        BulkResponse bulkResponse = new BulkResponse();
        responses.add(paramList.get(0).parseParameters());
        when(cache.getResponse(responses.get(0).getEquation()))
                .thenReturn(response);
        results.add(response);
        bulkResponse.initValues(results);
        assertEquals(new ResponseEntity<>(bulkResponse, HttpStatus.CREATED), controller.solveBulkEquation(paramList));

        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
    }


    @Test
    void testSolveBulkEquationWhenCacheHasNotResponse() throws NotFoundSolutionException {
        List<BulkParams> paramList = new ArrayList<>();
        paramList.add(new BulkParams("1", "2", "0", "4"));
        Equation equation = new Equation(new Pair(1, 2), new Pair(0, 4), 1);
        MyResponse response = new MyResponse(equation, new MyResponseStatus(201, HttpStatus.CREATED));

        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        List<MyResponse> responses = new ArrayList<>();
        List<MyResponse> results = new ArrayList<>();
        BulkResponse bulkResponse = new BulkResponse();

        responses.add(paramList.get(0).parseParameters());
        when(cache.getResponse(responses.get(0).getEquation()))
                .thenReturn(null);
        when(validator.isValidEquation(response.getEquation()))
                .thenReturn(new MyResponseStatus(201, HttpStatus.CREATED));
        response.setResponseStatus(validator.isValidEquation(response.getEquation()));
        when(equationSolverService.solve(any()))//response.getEquation()))
                .thenReturn(new Equation(new Pair(1, 2), new Pair(0, 4), 1));

        response.setResponseStatus(new MyResponseStatus(201, HttpStatus.CREATED));
        response.setEquation(equationSolverService.solve(response.getEquation()));
        response.setEquation(new Equation(new Pair(1, 2), new Pair(0, 4), 1));
        results.add(response);

        doNothing().when(cache).saveEquation(response);
        doNothing().when(dataBaseService).saveEquationInDataBase(response.getEquation());

        bulkResponse.initValues(results);

        assertEquals(new ResponseEntity<>(bulkResponse, HttpStatus.CREATED),
                controller.solveBulkEquation(paramList));

        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
        verify(cache, times(1)).saveEquation(response);
        verify(dataBaseService, times(1)).saveEquationInDataBase(response.getEquation());
    }


    @Test
    void getDataBaseTest() {
        Equation equation = new Equation(new Pair(0, 4), new Pair(0, 4), 4);
        dataBaseService.saveEquationInDataBase(equation);
        List<DBEquation> dbEquations = new ArrayList<>();
        dbEquations.add(new DBEquation(0, 4, 0, 4, 4));
        when(dataBaseService.getAllEquations()).thenReturn(dbEquations);
        assertEquals(dbEquations, controller.getDataBase());
    }

    @Test
    void testSolveBulkEquationWhenBadValidation() throws NotFoundSolutionException {
        List<BulkParams> paramList = new ArrayList<>();
        paramList.add(new BulkParams("1", "2", "4", "0"));
        Equation equation = new Equation(new Pair(1, 2), new Pair(4, 0));
        MyResponse response = new MyResponse(equation, new MyResponseStatus(500, HttpStatus.INTERNAL_SERVER_ERROR));

        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        List<MyResponse> responses = new ArrayList<>();
        List<MyResponse> results = new ArrayList<>();
        BulkResponse bulkResponse = new BulkResponse();

        responses.add(paramList.get(0).parseParameters());
        when(cache.getResponse(responses.get(0).getEquation()))
                .thenReturn(null);
        when(validator.isValidEquation(response.getEquation()))
                .thenReturn(new MyResponseStatus(500, HttpStatus.INTERNAL_SERVER_ERROR));
        response.setResponseStatus(validator.isValidEquation(response.getEquation()));
        response.getResponseStatus().addMessage("Parameter 'begin' cannot be greater than parameter 'end'");
        response.setEquation(new Equation(new Pair(1, 2), new Pair(4, 0)));
        results.add(response);

        bulkResponse.initValues(results);

        assertEquals(new ResponseEntity<>(bulkResponse, HttpStatus.BAD_REQUEST),
                controller.solveBulkEquation(paramList));

        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
    }


    @Test
    void testSolveBulkEquationWhenNotFoundException() throws NotFoundSolutionException {
        List<BulkParams> paramList = new ArrayList<>();
        paramList.add(new BulkParams("1", "2", "3", "4"));
        Equation equation = new Equation(new Pair(1, 2), new Pair(3, 4));
        MyResponse response = new MyResponse(equation, new MyResponseStatus(201, HttpStatus.CREATED));

        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        List<MyResponse> responses = new ArrayList<>();
        List<MyResponse> results = new ArrayList<>();
        BulkResponse bulkResponse = new BulkResponse();

        responses.add(paramList.get(0).parseParameters());
        when(cache.getResponse(responses.get(0).getEquation()))
                .thenReturn(null);
        when(validator.isValidEquation(response.getEquation()))
                .thenReturn(new MyResponseStatus(201, HttpStatus.CREATED));
        response.setResponseStatus(validator.isValidEquation(response.getEquation()));
        when(equationSolverService.solve(any()))
                .thenThrow(NotFoundSolutionException.class);

        response.setResponseStatus(new MyResponseStatus(404, HttpStatus.NOT_FOUND));
        response.getResponseStatus().addMessage("No solution found for this equation");
        results.add(response);

        bulkResponse.initValues(results);

        assertEquals(new ResponseEntity<>(bulkResponse, HttpStatus.PARTIAL_CONTENT),
                controller.solveBulkEquation(paramList));

        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
    }

    @Test
    public void testCallRestServiceAsync() throws Exception {

        String inA = "1";
        String inB = "2";
        String inBegin = "0";
        String inEnd = "4";

        doNothing().when(counterService).incrementSimpleCounter();
        doNothing().when(counterService).incrementSynchronizedCounter();

        Equation equation = new Equation(new Pair(1, 2), new Pair(0, 4));
        MyResponse response = new MyResponse();

        when(validator.isValidEquation(equation)).thenReturn(new MyResponseStatus(200, HttpStatus.OK));
        when(dataBaseService.getLastId()).thenReturn(0);

        AsyncResponse asyncResponse = new AsyncResponse(1);
        asyncResponse.getMessages().add("Equation is processed...");

        when(equationSolverService.solve(any()))//response.getEquation()))
                .thenReturn(new Equation(new Pair(1, 2), new Pair(0, 4), 1));
        response.setEquation(equationSolverService.solve(equation));

        doNothing().when(cache).saveEquation(response);
        doNothing().when(dataBaseService).saveEquationInDataBase(response.getEquation());

        AsyncResponse expectedAsyncResponse = new AsyncResponse(1);
        expectedAsyncResponse.getMessages().add("Equation is processed...");

        CompletableFuture<ResponseEntity<AsyncResponse>> futureResponseEntity = controller.callRestServiceAsync(inA, inB, inBegin, inEnd);
        ResponseEntity<AsyncResponse> responseEntity = futureResponseEntity.get();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(asyncResponse);
        assertEquals(expectedAsyncResponse.getFutureId(), asyncResponse.getFutureId());
        assertEquals(expectedAsyncResponse.getMessages(), asyncResponse.getMessages());

        verify(counterService, times(1)).incrementSimpleCounter();
        verify(counterService, times(1)).incrementSynchronizedCounter();
        verify(validator, times(1)).isValidEquation(equation);
        verify(dataBaseService, times(1)).getLastId();
        verify(cache, times(1)).saveEquation(any(MyResponse.class));
        verify(dataBaseService, times(1)).saveEquationInDataBase(any(Equation.class));
    }
}
