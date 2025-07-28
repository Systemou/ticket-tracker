package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TicketPriority;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketPriority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketPriorityRepository extends JpaRepository<TicketPriority, Long> {}
