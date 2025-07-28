package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.TicketPriority;
import com.mycompany.myapp.repository.TicketPriorityRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.TicketPriority}.
 */
@RestController
@RequestMapping("/api/ticket-priorities")
@Transactional
public class TicketPriorityResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketPriorityResource.class);

    private static final String ENTITY_NAME = "ticketPriority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketPriorityRepository ticketPriorityRepository;

    public TicketPriorityResource(TicketPriorityRepository ticketPriorityRepository) {
        this.ticketPriorityRepository = ticketPriorityRepository;
    }

    /**
     * {@code POST  /ticket-priorities} : Create a new ticketPriority.
     *
     * @param ticketPriority the ticketPriority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketPriority, or with status {@code 400 (Bad Request)} if the ticketPriority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketPriority> createTicketPriority(@Valid @RequestBody TicketPriority ticketPriority)
        throws URISyntaxException {
        LOG.debug("REST request to save TicketPriority : {}", ticketPriority);
        if (ticketPriority.getId() != null) {
            throw new BadRequestAlertException("A new ticketPriority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketPriority = ticketPriorityRepository.save(ticketPriority);
        return ResponseEntity.created(new URI("/api/ticket-priorities/" + ticketPriority.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, ticketPriority.getId().toString()))
            .body(ticketPriority);
    }

    /**
     * {@code PUT  /ticket-priorities/:id} : Updates an existing ticketPriority.
     *
     * @param id the id of the ticketPriority to save.
     * @param ticketPriority the ticketPriority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketPriority,
     * or with status {@code 400 (Bad Request)} if the ticketPriority is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketPriority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketPriority> updateTicketPriority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketPriority ticketPriority
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketPriority : {}, {}", id, ticketPriority);
        if (ticketPriority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketPriority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketPriorityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketPriority = ticketPriorityRepository.save(ticketPriority);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ticketPriority.getId().toString()))
            .body(ticketPriority);
    }

    /**
     * {@code PATCH  /ticket-priorities/:id} : Partial updates given fields of an existing ticketPriority, field will ignore if it is null
     *
     * @param id the id of the ticketPriority to save.
     * @param ticketPriority the ticketPriority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketPriority,
     * or with status {@code 400 (Bad Request)} if the ticketPriority is not valid,
     * or with status {@code 404 (Not Found)} if the ticketPriority is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketPriority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketPriority> partialUpdateTicketPriority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketPriority ticketPriority
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketPriority partially : {}, {}", id, ticketPriority);
        if (ticketPriority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketPriority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketPriorityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketPriority> result = ticketPriorityRepository
            .findById(ticketPriority.getId())
            .map(existingTicketPriority -> {
                if (ticketPriority.getName() != null) {
                    existingTicketPriority.setName(ticketPriority.getName());
                }

                return existingTicketPriority;
            })
            .map(ticketPriorityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ticketPriority.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-priorities} : get all the ticketPriorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketPriorities in body.
     */
    @GetMapping("")
    public List<TicketPriority> getAllTicketPriorities() {
        LOG.debug("REST request to get all TicketPriorities");
        return ticketPriorityRepository.findAll();
    }

    /**
     * {@code GET  /ticket-priorities/:id} : get the "id" ticketPriority.
     *
     * @param id the id of the ticketPriority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketPriority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketPriority> getTicketPriority(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketPriority : {}", id);
        Optional<TicketPriority> ticketPriority = ticketPriorityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ticketPriority);
    }

    /**
     * {@code DELETE  /ticket-priorities/:id} : delete the "id" ticketPriority.
     *
     * @param id the id of the ticketPriority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketPriority(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketPriority : {}", id);
        ticketPriorityRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
