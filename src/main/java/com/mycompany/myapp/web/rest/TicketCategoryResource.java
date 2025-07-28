package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.TicketCategory;
import com.mycompany.myapp.repository.TicketCategoryRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TicketCategory}.
 */
@RestController
@RequestMapping("/api/ticket-categories")
@Transactional
public class TicketCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCategoryResource.class);

    private static final String ENTITY_NAME = "ticketCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketCategoryRepository ticketCategoryRepository;

    public TicketCategoryResource(TicketCategoryRepository ticketCategoryRepository) {
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    /**
     * {@code POST  /ticket-categories} : Create a new ticketCategory.
     *
     * @param ticketCategory the ticketCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketCategory, or with status {@code 400 (Bad Request)} if the ticketCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketCategory> createTicketCategory(@Valid @RequestBody TicketCategory ticketCategory)
        throws URISyntaxException {
        LOG.debug("REST request to save TicketCategory : {}", ticketCategory);
        if (ticketCategory.getId() != null) {
            throw new BadRequestAlertException("A new ticketCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketCategory = ticketCategoryRepository.save(ticketCategory);
        return ResponseEntity.created(new URI("/api/ticket-categories/" + ticketCategory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, ticketCategory.getId().toString()))
            .body(ticketCategory);
    }

    /**
     * {@code PUT  /ticket-categories/:id} : Updates an existing ticketCategory.
     *
     * @param id the id of the ticketCategory to save.
     * @param ticketCategory the ticketCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketCategory,
     * or with status {@code 400 (Bad Request)} if the ticketCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketCategory> updateTicketCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketCategory ticketCategory
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketCategory : {}, {}", id, ticketCategory);
        if (ticketCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketCategory = ticketCategoryRepository.save(ticketCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ticketCategory.getId().toString()))
            .body(ticketCategory);
    }

    /**
     * {@code PATCH  /ticket-categories/:id} : Partial updates given fields of an existing ticketCategory, field will ignore if it is null
     *
     * @param id the id of the ticketCategory to save.
     * @param ticketCategory the ticketCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketCategory,
     * or with status {@code 400 (Bad Request)} if the ticketCategory is not valid,
     * or with status {@code 404 (Not Found)} if the ticketCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketCategory> partialUpdateTicketCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketCategory ticketCategory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketCategory partially : {}, {}", id, ticketCategory);
        if (ticketCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketCategory> result = ticketCategoryRepository
            .findById(ticketCategory.getId())
            .map(existingTicketCategory -> {
                if (ticketCategory.getName() != null) {
                    existingTicketCategory.setName(ticketCategory.getName());
                }

                return existingTicketCategory;
            })
            .map(ticketCategoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ticketCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-categories} : get all the ticketCategories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketCategories in body.
     */
    @GetMapping("")
    public List<TicketCategory> getAllTicketCategories() {
        LOG.debug("REST request to get all TicketCategories");
        return ticketCategoryRepository.findAll();
    }

    /**
     * {@code GET  /ticket-categories/:id} : get the "id" ticketCategory.
     *
     * @param id the id of the ticketCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketCategory> getTicketCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketCategory : {}", id);
        Optional<TicketCategory> ticketCategory = ticketCategoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ticketCategory);
    }

    /**
     * {@code DELETE  /ticket-categories/:id} : delete the "id" ticketCategory.
     *
     * @param id the id of the ticketCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketCategory : {}", id);
        ticketCategoryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
