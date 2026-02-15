package com.productdistribution.backend.unit.exceptions;

import com.productdistribution.backend.dtos.ErrorResponseDTO;
import com.productdistribution.backend.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RestExceptionHandlerUnitTest {

    private RestExceptionHandler restExceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        restExceptionHandler = new RestExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test/uri");
    }

    @Test
    void handleResourceNotFoundException_shouldReturn404ErrorResponse() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Product", "P1");

        ResponseEntity<ErrorResponseDTO> response = restExceptionHandler.handleResourceNotFoundException(exception, request);

        verify(request).getRequestURI(); 
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Product with id 'P1' not found");
        assertThat(response.getBody().path()).isEqualTo("/test/uri");
    }

    @Test
    void handleIllegalArgumentException_shouldReturn400ErrorResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponseDTO> response = restExceptionHandler.handleIllegalArgumentException(exception, request);

        verify(request).getRequestURI();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Invalid argument");
        assertThat(response.getBody().path()).isEqualTo("/test/uri");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturn400ErrorResponse() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("product", "id", "must not be null");
        FieldError fieldError2 = new FieldError("product", "name", "must not be blank");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponseDTO> response = restExceptionHandler.handleMethodArgumentNotValidException(exception, request);

        verify(request).getRequestURI();
        verify(exception).getBindingResult();
        verify(bindingResult).getFieldErrors();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("Validation failed:");
        assertThat(response.getBody().message()).contains("id: must not be null");
        assertThat(response.getBody().message()).contains("name: must not be blank");
        assertThat(response.getBody().path()).isEqualTo("/test/uri");
    }

    @Test
    void handleDataLoadingException_shouldReturn500ErrorResponse() {
        DataLoadingException exception = new DataLoadingException("Failed to load data");

        ResponseEntity<ErrorResponseDTO> response = restExceptionHandler.handleDataLoadingException(exception, request);

        verify(request).getRequestURI();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Failed to load data");
        assertThat(response.getBody().path()).isEqualTo("/test/uri");
    }

    @Test
    void handleConfigurationException_shouldReturn500ErrorResponse() {
        ConfigurationException exception = new ConfigurationException("Configuration error");

        ResponseEntity<ErrorResponseDTO> response = restExceptionHandler.handleConfigurationException(exception, request);

        verify(request).getRequestURI();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Configuration error");
        assertThat(response.getBody().path()).isEqualTo("/test/uri");
    }

    @Test
    void handleGenericException_shouldReturn500ErrorResponse() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponseDTO> response = restExceptionHandler.handleGenericException(exception, request);

        verify(request).getRequestURI();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred: Unexpected error");
        assertThat(response.getBody().path()).isEqualTo("/test/uri");
    }
}