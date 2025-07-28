package com.mycompany.myapp.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.myapp.domain.Ticket;
import com.mycompany.myapp.domain.enumeration.TicketStatus;
import com.mycompany.myapp.repository.TicketRepository;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Ticket}.
 */
@Service
@Transactional
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final TicketValidationService ticketValidationService;

    public TicketService(TicketRepository ticketRepository, TicketValidationService ticketValidationService) {
        this.ticketRepository = ticketRepository;
        this.ticketValidationService = ticketValidationService;
    }

    /**
     * Save a ticket.
     *
     * @param ticket the entity to save.
     * @return the persisted entity.
     */
    public Ticket save(Ticket ticket) {
        LOG.debug("Request to save Ticket : {}", ticket);

        // Validate ticket according to business rules
        ticketValidationService.validateTicket(ticket);

        // Set default status to OPEN if not provided
        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.OPEN);
        }

        // Set creation date if not provided
        if (ticket.getCreationDate() == null) {
            ticket.setCreationDate(Instant.now());
        }

        return ticketRepository.save(ticket);
    }

    /**
     * Update a ticket.
     *
     * @param ticket the entity to save.
     * @return the persisted entity.
     */
    public Ticket update(Ticket ticket) {
        LOG.debug("Request to update Ticket : {}", ticket);
        return ticketRepository.save(ticket);
    }

    /**
     * Partially update a ticket.
     *
     * @param ticket the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Ticket> partialUpdate(Ticket ticket) {
        LOG.debug("Request to partially update Ticket : {}", ticket);

        return ticketRepository
            .findById(ticket.getId())
            .map(existingTicket -> {
                if (ticket.getTitle() != null) {
                    existingTicket.setTitle(ticket.getTitle());
                }
                if (ticket.getDescription() != null) {
                    existingTicket.setDescription(ticket.getDescription());
                }
                if (ticket.getCreationDate() != null) {
                    existingTicket.setCreationDate(ticket.getCreationDate());
                }
                if (ticket.getStatus() != null) {
                    existingTicket.setStatus(ticket.getStatus());
                }

                return existingTicket;
            })
            .map(ticketRepository::save);
    }

    /**
     * Get all the tickets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Ticket> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tickets");
        return ticketRepository.findAll(pageable);
    }

    /**
     * Get all the tickets with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Ticket> findAllWithEagerRelationships(Pageable pageable) {
        return ticketRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one ticket by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Ticket> findOne(Long id) {
        LOG.debug("Request to get Ticket : {}", id);
        return ticketRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the ticket by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Ticket : {}", id);
        ticketRepository.deleteById(id);
    }
}
