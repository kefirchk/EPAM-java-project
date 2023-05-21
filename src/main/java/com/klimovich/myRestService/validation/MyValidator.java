package com.klimovich.myRestService.validation;

import com.klimovich.myRestService.entity.Equation;
import com.klimovich.myRestService.entity.MyResponseStatus;
import com.klimovich.myRestService.entity.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class MyValidator {
    private static final Logger logger = LoggerFactory.getLogger(MyValidator.class);
    public MyResponseStatus isValidEquation(Equation equation) {
        MyResponseStatus responseStatus = new MyResponseStatus();
        if(!isValidSegment(equation.getSegment())) {
            logger.error("Parameter of the segment is bad (begin > end)");
            responseStatus.setStatusCode(500);
            responseStatus.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseStatus.addMessage("Parameter 'begin' cannot be greater than parameter 'end'");
        }
        if(!isValidDiffNumbers(equation.getNumbers())) {
            logger.error("Parameters of the numbers are bad (difference != value of [INT_MIN; INT_MAX])");
            responseStatus.setStatusCode(500);
            responseStatus.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseStatus.addMessage("The difference between the values of parameters 'a' and 'b' cannot be more or less than the allowed values for the type Integer");
        }
        if(responseStatus.getMessages().isEmpty()) {
            logger.info("Validation completed successfully");
            responseStatus.setStatus(HttpStatus.OK);
            responseStatus.setStatusCode(200);
        }
        return responseStatus;
    }

    @Ignore
    private boolean isValidDiffNumbers(@NotNull Pair numbers) {
        long difference = numbers.getSecond().longValue() - numbers.getFirst().longValue();
        return difference <= Integer.MAX_VALUE && difference >= Integer.MIN_VALUE;
    }
    @Ignore
    private boolean isValidSegment(@NotNull Pair segment) {
        return segment.getFirst() <= segment.getSecond();
    }

    @Ignore
    public boolean isIntNumber(String s)
    {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
