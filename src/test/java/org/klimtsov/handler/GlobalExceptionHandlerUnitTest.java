package org.klimtsov.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.klimtsov.dto.ErrorResponse;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturnBadRequest() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("email", "String");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleMissingParameter(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .contains("Отсутствует обязательный параметр: email");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(new PathImpl("sendWelcomeEmail.email"));
        when(violation.getMessage()).thenReturn("must be a well-formed email address");

        violations.add(violation);

        ConstraintViolationException exception =
                new ConstraintViolationException("Validation failed", violations);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleValidationExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .contains("Ошибка валидации: {sendWelcomeEmail.email=must be a well-formed email address}");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("testMethod", String.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("object", "email", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleMethodArgumentNotValid(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .contains("Ошибка валидации полей: {email=must not be blank}");
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception =
                mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("page");
        when(exception.getRequiredType()).thenReturn((Class) Integer.class);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleTypeMismatch(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .contains("Неверный тип параметра 'page'. Ожидается: Integer");
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Database connection failed");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleAllExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .contains("Внутренняя ошибка сервера: Database connection failed");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
    }

    @Test
    void handleGenericException_WithNullMessage_ShouldReturnInternalServerError() {
        Exception exception = new NullPointerException();

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleAllExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .contains("Внутренняя ошибка сервера: null");
    }

    static class TestController {
        public void testMethod(String email) {
        }
    }

    static class PathImpl implements Path {
        private final String path;

        PathImpl(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return path;
        }

        @Override
        public Iterator<Node> iterator() {
            return null;
        }
    }
}