import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketPriority from './ticket-priority';
import TicketPriorityDetail from './ticket-priority-detail';
import TicketPriorityUpdate from './ticket-priority-update';
import TicketPriorityDeleteDialog from './ticket-priority-delete-dialog';

const TicketPriorityRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketPriority />} />
    <Route path="new" element={<TicketPriorityUpdate />} />
    <Route path=":id">
      <Route index element={<TicketPriorityDetail />} />
      <Route path="edit" element={<TicketPriorityUpdate />} />
      <Route path="delete" element={<TicketPriorityDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketPriorityRoutes;
