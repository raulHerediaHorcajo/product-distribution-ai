package com.productdistribution.backend.unit.exceptions;

import com.productdistribution.backend.exceptions.ConfigurationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationExceptionUnitTest {

    @Test
    void constructor_whenMessage_shouldConfigurationExceptionWithMessage() {
        String message = "Configuration error";

        ConfigurationException exception = new ConfigurationException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_whenMessageAndCause_shouldConfigurationExceptionWithMessageAndCause() {
        String message = "Configuration error";
        Throwable cause = new RuntimeException("Cause of error");

        ConfigurationException exception = new ConfigurationException(message, cause);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}