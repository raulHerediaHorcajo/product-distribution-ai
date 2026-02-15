package com.productdistribution.backend.unit.exceptions;

import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionUnitTest {

    @Test
    void constructor_whenMessage_shouldResourceNotFoundExceptionWithMessage() {
        String message = "Resource not found";

        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_whenResourceTypeAndId_shouldResourceNotFoundExceptionWithFormattedMessage() {
        String resourceType = "Product";
        String resourceId = "P1";
        String expectedMessage = "Product with id 'P1' not found";

        ResourceNotFoundException exception = new ResourceNotFoundException(resourceType, resourceId);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }
}