import ticket from 'app/entities/ticket/ticket.reducer';
import ticketCategory from 'app/entities/ticket-category/ticket-category.reducer';
import ticketPriority from 'app/entities/ticket-priority/ticket-priority.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  ticket,
  ticketCategory,
  ticketPriority,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
