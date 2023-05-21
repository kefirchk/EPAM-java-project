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
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/equations")
public class EquationController {
    private static final Logger logger = LoggerFactory.getLogger(EquationController.class);
    @Autowired
    private CounterService counterService;
    @Autowired
    private EquationSolverService equationSolver;
    @Autowired
    private MyValidator validator;
    @Autowired
    private Cache cache;
    @Autowired
    private DataBaseService dataBaseService;
    @GetMapping(path = "/solve")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MyResponse> solveEquation(@RequestParam("a") String inA,
                                    @RequestParam("b") String inB,
                                    @RequestParam("begin") String inBegin,
                                    @RequestParam("end") String inEnd) throws NotFoundSolutionException {

        counterService.incrementSimpleCounter();
        counterService.incrementSynchronizedCounter();

        Integer a = Integer.parseInt(inA);
        Integer b = Integer.parseInt(inB);
        Integer begin = Integer.parseInt(inBegin);
        Integer end = Integer.parseInt(inEnd);
        logger.info("Get request");

        MyResponse response = new MyResponse();
        Equation equation = new Equation(new Pair(a, b), new Pair(begin, end));
        logger.info("Search query in the cache");
        if(cache.getResponse(equation) != null) {
            logger.info("This request is already in the cache");
            return new ResponseEntity<>(cache.getResponse(equation), HttpStatus.OK);
        }
        logger.info("This request is not in the cache");
        logger.info("Validation of the parameters");
        response.setResponseStatus(validator.isValidEquation(equation));
        if(response.getResponseStatus() == null || response.getResponseStatus().getMessages().isEmpty()) {
            logger.info("Solving equation");
            response.setEquation(equationSolver.solve(equation));
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Saving unique query in the cache");
        cache.saveEquation(response);
        dataBaseService.saveEquationInDataBase(response.getEquation());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/all")
    @ResponseStatus(HttpStatus.OK)
    public List<MyResponse> getCache() {
        logger.info("Getting all saved equations (cache)");
        return cache.getAllSavedEquations();
    }

    @GetMapping(path = "/all/cacheCharacteristics")
    @ResponseStatus(HttpStatus.OK)
    public CacheCharacteristics getCacheCharacteristics() {
        logger.info("Getting of cache characteristics");
        return new CacheCharacteristics(cache.getCacheSize());
    }

    @GetMapping(path = "/counter/value")
    @ResponseStatus(HttpStatus.OK)
    public Counter getCounterValue() {
        logger.info("Getting of counter value");
        return counterService.getCounter();
    }

    @GetMapping(path = "/counter/clean")
    @ResponseStatus(HttpStatus.OK)
    public Counter cleanCounterValue() {
        logger.info("Clean a counter value");
        counterService.setCounter(0, 0);
        return counterService.getCounter();
    }


    @PostMapping(path = "/solve")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BulkResponse> solveBulkEquation(@RequestBody List<BulkParams> paramList) throws NotFoundSolutionException {
        logger.info("Post request");
        logger.info("Increment counters");
        counterService.incrementSimpleCounter();
        counterService.incrementSynchronizedCounter();

        List<MyResponse> responses = new ArrayList<>();
        List<MyResponse> results = new ArrayList<>();
        BulkResponse bulkResponse = new BulkResponse();
        int[] status = new int[2];  // if status[0]  == 1 - http status is 400: Bad Request,
                                    // if status[1] == 1 - http status is 201: Created/206: Partial Content

        logger.info("Parsing strings to integers");
        paramList.forEach(param -> {
            responses.add(param.parseParameters());
        });

        responses.forEach(response -> {
            logger.info("New Request:");
            if(response.getEquation() != null)
            {
                logger.info("Search query in the cache");
                if(cache.getResponse(response.getEquation()) != null) {
                    logger.info("This request is already in the cache");
                    results.add(cache.getResponse(response.getEquation()));
                    status[1] = 1;
                } else {
                    logger.info("This request is not in the cache");
                    logger.info("Validation of the parameters");
                    response.setResponseStatus(validator.isValidEquation(response.getEquation()));
                    try {
                        if (response.getResponseStatus() == null || response.getResponseStatus().getMessages().isEmpty()) {
                            logger.info("Solving equation");
                            status[1] = 1;
                            response.setEquation(equationSolver.solve(response.getEquation()));
                            response.setResponseStatus(new MyResponseStatus(201, HttpStatus.CREATED));
                            results.add(response);
                            logger.info("Saving unique query in the cache");
                            cache.saveEquation(response);
                            logger.info("Saving unique query in the database");
                            dataBaseService.saveEquationInDataBase(response.getEquation());
                        } else {
                            status[0] = 1;
                            results.add(response);
                        }
                    }
                    catch (NotFoundSolutionException e)
                    {
                        status[0] = 1;
                        response.setResponseStatus(new MyResponseStatus(404, HttpStatus.NOT_FOUND));
                        response.getResponseStatus().addMessage("No solution found for this equation");
                        results.add(response);
                    }
                }
            } else {
                results.add(response);
            }
        });

        logger.info("Initialization of Bulk Response");
        bulkResponse.initValues(results);
        if(status[0] == 0 && status[1] == 1) {
            return new ResponseEntity<>(bulkResponse, HttpStatus.CREATED);
        } else if(status[0] == 1 && status[1] == 1) {
            return new ResponseEntity<>(bulkResponse, HttpStatus.PARTIAL_CONTENT);
        } else {//if(status[0] == 1 && status[1] == 0){
            return new ResponseEntity<>(bulkResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "/database")
    @ResponseStatus(HttpStatus.OK)
    public List<DBEquation> getDataBase() {
        logger.info("Get equations from database");
        return dataBaseService.getAllEquations();
    }

    @GetMapping(path = "/asyncSolve")
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<ResponseEntity<AsyncResponse>> callRestServiceAsync(@RequestParam("a") String inA,
                                     @RequestParam("b") String inB,
                                     @RequestParam("begin") String inBegin,
                                     @RequestParam("end") String inEnd) throws NotFoundSolutionException {

        logger.info("Async get call.");

        counterService.incrementSimpleCounter();
        counterService.incrementSynchronizedCounter();

        Integer a = Integer.parseInt(inA);
        Integer b = Integer.parseInt(inB);
        Integer begin = Integer.parseInt(inBegin);
        Integer end = Integer.parseInt(inEnd);
        logger.info("Get request");

        MyResponse response = new MyResponse();
        Equation equation = new Equation(new Pair(a, b), new Pair(begin, end));
        logger.info("Validation of the parameters");
        response.setResponseStatus(validator.isValidEquation(equation));

        AsyncResponse asyncResponse = new AsyncResponse(dataBaseService.getLastId() + 1);
        asyncResponse.getMessages().add("Equation is processed...");

        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                logger.info("Start calculating");

                if (response.getResponseStatus() == null || response.getResponseStatus().getMessages().isEmpty()) {
                    logger.info("Solving equation");
                    try {
                        response.setEquation(equationSolver.solve(equation));
                    } catch (Exception e) {
                        logger.info("Not found solution exception");
                        return;
                    }
                } else {
                    response.setResponseStatus(new MyResponseStatus(500, HttpStatus.INTERNAL_SERVER_ERROR));
                    return;
                }
                logger.info("Saving query in the cache");
                cache.saveEquation(response);
                dataBaseService.saveEquationInDataBase(response.getEquation());
                logger.info("End calculating");

            }
        });

        logger.info("Return answer");
        return CompletableFuture.completedFuture(new ResponseEntity<>(asyncResponse, HttpStatus.OK));
    }
}
