import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketCategory from './ticket-category';
import TicketCategoryDetail from './ticket-category-detail';
import TicketCategoryUpdate from './ticket-category-update';
import TicketCategoryDeleteDialog from './ticket-category-delete-dialog';

const TicketCategoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketCategory />} />
    <Route path="new" element={<TicketCategoryUpdate />} />
    <Route path=":id">
      <Route index element={<TicketCategoryDetail />} />
      <Route path="edit" element={<TicketCategoryUpdate />} />
      <Route path="delete" element={<TicketCategoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketCategoryRoutes;
