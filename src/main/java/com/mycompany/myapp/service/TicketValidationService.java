package com.mycompany.myapp.service;

import org.springframework.stereotype.Service;

import com.mycompany.myapp.domain.Ticket;
import com.mycompany.myapp.domain.TicketCategory;
import com.mycompany.myapp.domain.TicketPriority;

/**
 * Service for validating ticket business rules.
 */
@Service
public class TicketValidationService {

    private static final int MIN_TITLE_LENGTH = 5;
    private static final int MIN_DESCRIPTION_LENGTH = 20;

    /**
     * Validates a ticket according to business rules.
     *
     * @param ticket the ticket to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateTicket(Ticket ticket) {
        validateTitle(ticket.getTitle());
        validateDescription(ticket.getDescription());
        validateCategory(ticket.getCategory());
        validatePriority(ticket.getPriority());
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (title.trim().length() < MIN_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title must be at least " + MIN_TITLE_LENGTH + " characters long");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (description.trim().length() < MIN_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description must be at least " + MIN_DESCRIPTION_LENGTH + " characters long");
        }
    }

    private void validateCategory(TicketCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
    }

    private void validatePriority(TicketPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority is required");
        }
    }
}
