package com.klimovich.myRestService.service;

import com.klimovich.myRestService.entity.Equation;
import com.klimovich.myRestService.exception.NotFoundSolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EquationSolverService {
    private static final Logger logger = LoggerFactory.getLogger(EquationSolverService.class);

    public Equation solve(Equation equation) throws NotFoundSolutionException {
        logger.info("Search for a solution to the equation");
        int difference = equation.getNumbers().getSecond() - equation.getNumbers().getFirst();
        for (int x = equation.getSegment().getFirst(); x <= equation.getSegment().getSecond(); ++x) {
            if (x == difference) {
                logger.info("Solution found");
                equation.setAnswer(x);
                return equation;
            }
        }
        logger.error("Solution not found");
        throw new NotFoundSolutionException("No solution found for this equation");
    }


}
