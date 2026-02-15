package com.productdistribution.backend.unit.exceptions;

import com.productdistribution.backend.exceptions.DataLoadingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataLoadingExceptionUnitTest {

    @Test
    void constructor_whenMessage_shouldDataLoadingExceptionWithMessage() {
        String message = "Failed to load data";

        DataLoadingException exception = new DataLoadingException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_whenMessageAndCause_shouldDataLoadingExceptionWithMessageAndCause() {
        String message = "Failed to load data";
        Throwable cause = new RuntimeException("Cause of error");

        DataLoadingException exception = new DataLoadingException(message, cause);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}