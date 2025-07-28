import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Ticket from './ticket';
import TicketCategory from './ticket-category';
import TicketPriority from './ticket-priority';
import Debug from 'app/shared/layout/debug/debug';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="ticket/*" element={<Ticket />} />
        <Route path="ticket-category/*" element={<TicketCategory />} />
        <Route path="ticket-priority/*" element={<TicketPriority />} />
        <Route path="debug" element={<Debug />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
