package com.appbito.appbito.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private static final String PARAMETER_WITHOUT_DOT = "parameter";

    private static final String ERROR = "error";

    private static final String TEST = "test";

    @Test
    void handleConstraintWithoutDot() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn(PARAMETER_WITHOUT_DOT);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(ERROR);

        ConstraintViolationException ex = new ConstraintViolationException(TEST, Set.of(violation));

        ResponseEntity<?> response = handler.handleConstraint(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals(ERROR, body.get(PARAMETER_WITHOUT_DOT));
    }

}
