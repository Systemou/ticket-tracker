package com.mycompany.myapp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.myapp.domain.Ticket;
import com.mycompany.myapp.domain.TicketCategory;
import com.mycompany.myapp.domain.TicketPriority;

/**
 * Unit tests for the {@link TicketValidationService}.
 */
class TicketValidationServiceTest {

    private TicketValidationService ticketValidationService;
    private Ticket ticket;
    private TicketCategory category;
    private TicketPriority priority;

    @BeforeEach
    void setUp() {
        ticketValidationService = new TicketValidationService();

        category = new TicketCategory();
        category.setName("Test Category");

        priority = new TicketPriority();
        priority.setName("High");

        ticket = new Ticket();
        ticket.setTitle("Valid Ticket Title");
        ticket.setDescription("This is a valid ticket description that meets the minimum length requirement");
        ticket.setCategory(category);
        ticket.setPriority(priority);
    }

    @Test
    void shouldValidateValidTicket() {
        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        // Given
        ticket.setTitle(null);

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Given
        ticket.setTitle("");

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        // Given
        ticket.setTitle("   ");

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsTooShort() {
        // Given
        ticket.setTitle("Hi");

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title must be at least 5 characters long");
    }

    @Test
    void shouldAcceptTitleWithMinimumLength() {
        // Given
        ticket.setTitle("12345");

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldAcceptTitleWithLeadingAndTrailingSpaces() {
        // Given
        ticket.setTitle("  Valid Title  ");

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        // Given
        ticket.setDescription(null);

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description is required");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        // Given
        ticket.setDescription("");

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description is required");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        // Given
        ticket.setDescription("   ");

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description is required");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooShort() {
        // Given
        ticket.setDescription("Short desc");

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description must be at least 20 characters long");
    }

    @Test
    void shouldAcceptDescriptionWithMinimumLength() {
        // Given
        ticket.setDescription("12345678901234567890");

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldAcceptDescriptionWithLeadingAndTrailingSpaces() {
        // Given
        ticket.setDescription("  This is a valid description with spaces  ");

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        // Given
        ticket.setCategory(null);

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Category is required");
    }

    @Test
    void shouldAcceptValidCategory() {
        // Given
        TicketCategory validCategory = new TicketCategory();
        validCategory.setName("Another Category");
        ticket.setCategory(validCategory);

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldThrowExceptionWhenPriorityIsNull() {
        // Given
        ticket.setPriority(null);

        // When & Then
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Priority is required");
    }

    @Test
    void shouldAcceptValidPriority() {
        // Given
        TicketPriority validPriority = new TicketPriority();
        validPriority.setName("Low");
        ticket.setPriority(validPriority);

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldValidateMultipleValidationFailures() {
        // Given
        ticket.setTitle(null);
        ticket.setDescription(null);
        ticket.setCategory(null);
        ticket.setPriority(null);

        // When & Then - Should fail on first validation (title)
        assertThatThrownBy(() -> ticketValidationService.validateTicket(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
    }

    @Test
    void shouldAcceptLongTitleAndDescription() {
        // Given
        ticket.setTitle("This is a very long title that should be perfectly valid for testing purposes");
        ticket.setDescription("This is a very long description that should be perfectly valid for testing purposes. " +
            "It contains multiple sentences and should meet all validation requirements without any issues.");

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }

    @Test
    void shouldAcceptSpecialCharactersInTitleAndDescription() {
        // Given
        ticket.setTitle("Title with special chars: !@#$%^&*()");
        ticket.setDescription("Description with special characters: !@#$%^&*() and numbers 1234567890 and unicode: ñáéíóú");

        // When & Then - should not throw any exception
        ticketValidationService.validateTicket(ticket);
    }
}
