import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-priority.reducer';

export const TicketPriorityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketPriorityEntity = useAppSelector(state => state.ticketPriority.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketPriorityDetailsHeading">Ticket Priority</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{ticketPriorityEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{ticketPriorityEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/ticket-priority" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket-priority/${ticketPriorityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketPriorityDetail;
