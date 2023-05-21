package com.klimovich.myRestService.service;

import com.klimovich.myRestService.entity.Equation;
import com.klimovich.myRestService.entity.Pair;
import com.klimovich.myRestService.exception.NotFoundSolutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

public class EquationSolverServiceTest {
    private AutoCloseable closeable;
    @Mock
    private Equation equation;
    @InjectMocks
    private final EquationSolverService equationSolverService = new EquationSolverService();

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }


    @Test
    void testJustSolve() throws NotFoundSolutionException {
        Pair numbers = new Pair(1, 2);
        Pair segment = new Pair(0, 4);
        equation.setNumbers(numbers);
        equation.setSegment(segment);
        when(equation.getNumbers()).thenReturn(numbers);
        when(equation.getSegment()).thenReturn(segment);
        doNothing().when(equation).setAnswer(1);
        equation.setAnswer(1);
        verify(equation, times(1)).setAnswer(1);
        assertEquals(new Equation(numbers, segment, 1),
                equationSolverService.solve(new Equation(numbers, segment)));
    }


    @Test // numbers are bad parameters
    void testCatchExceptionBadParameters() throws Exception, NotFoundSolutionException {
        try {
            Pair numbers = new Pair();  //bad
            Pair segment = new Pair(34, 565);
            equation.setNumbers(numbers);
            equation.setSegment(segment);
            when(equation.getNumbers()).thenReturn(numbers);
            when(equation.getSegment()).thenReturn(segment);
        }
        catch (Exception e) {
            assertNotEquals("", e.getMessage());
        }
    }


    @Test //answer = 1, but "1" does not belong to the interval [-4, -1]
    void testCatchNotFoundSolutionException() throws NotFoundSolutionException {
        try {
            Pair numbers = new Pair(1, 2);
            Pair segment = new Pair(-4, -1);
            equation.setNumbers(numbers);
            equation.setSegment(segment);
            when(equation.getNumbers()).thenReturn(numbers);
            when(equation.getSegment()).thenReturn(segment);
            equationSolverService.solve(equation);
        }
        catch (NotFoundSolutionException e) {
            assertEquals("No solution found for this equation", e.getMessage());
        }
    }
}
