import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, Card, FormGroup, Label, Input, Form } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { motion, AnimatePresence } from 'framer-motion';
import { Ticket as TicketIcon, Save, ArrowLeft, Plus, AlertCircle, CheckCircle } from 'lucide-react';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTicketCategories } from 'app/entities/ticket-category/ticket-category.reducer';
import { getEntities as getTicketPriorities } from 'app/entities/ticket-priority/ticket-priority.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';
import { createEntity, getEntity, reset, updateEntity } from './ticket.reducer';

const formVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.6,
      ease: 'easeOut' as const,
    },
  },
};

const fieldVariants = {
  hidden: { opacity: 0, x: -20 },
  visible: {
    opacity: 1,
    x: 0,
    transition: {
      duration: 0.4,
    },
  },
};

export const TicketUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const ticketCategories = useAppSelector(state => state.ticketCategory.entities);
  const ticketPriorities = useAppSelector(state => state.ticketPriority.entities);
  const users = useAppSelector(state => state.userManagement.users || []);

  // Debug: Check if data is loading
  const ticketCategoryLoading = useAppSelector(state => state.ticketCategory.loading);
  const ticketPriorityLoading = useAppSelector(state => state.ticketPriority.loading);
  const userLoading = useAppSelector(state => state.userManagement.loading);

  const ticketEntity = useAppSelector(state => state.ticket.entity);
  const loading = useAppSelector(state => state.ticket.loading);
  const updating = useAppSelector(state => state.ticket.updating);
  const updateSuccess = useAppSelector(state => state.ticket.updateSuccess);
  const ticketStatusValues = Object.keys(TicketStatus);

  const handleClose = () => {
    navigate(`/ticket${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTicketCategories({}));
    dispatch(getTicketPriorities({}));
    dispatch(getUsers({}));
  }, [dispatch, id, isNew]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    // Temporary debugging - remove after fixing
    console.warn('Form values being submitted:', values);
    console.warn('Title value:', values.title);
    console.warn('Description value:', values.description);

    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.creationDate = convertDateTimeToServer(values.creationDate);

    const entity = {
      ...ticketEntity,
      ...values,
      category: ticketCategories.find(it => it.id.toString() === values.category?.toString()),
      priority: ticketPriorities.find(it => it.id.toString() === values.priority?.toString()),
      user: users.find(it => it.id.toString() === values.user?.toString()),
    };

    console.warn('Entity being sent to backend:', entity);

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          status: 'OPEN',
          creationDate: displayDefaultDateTime(),
        }
      : {
          status: 'OPEN',
          ...ticketEntity,
          creationDate: convertDateTimeFromServer(ticketEntity.creationDate),
          category: ticketEntity?.category?.id,
          priority: ticketEntity?.priority?.id,
          user: ticketEntity?.user?.id,
        };

  return (
    <motion.div initial="hidden" animate="visible" variants={formVariants} className="ticket-form-container">
      {/* Header */}
      <motion.div variants={fieldVariants} className="mb-4">
        <Card className="border-0 shadow-sm">
          <div className="p-4">
            <Row className="align-items-center">
              <Col md={8}>
                <div className="d-flex align-items-center">
                  <motion.div whileHover={{ rotate: 360 }} transition={{ duration: 0.6 }} className="me-3">
                    <TicketIcon size={32} className="text-primary" />
                  </motion.div>
                  <div>
                    <h2 className="mb-0 fw-bold text-gradient">{isNew ? 'Create New Ticket' : 'Edit Ticket'}</h2>
                    <p className="text-muted mb-0">{isNew ? 'Submit a new support request' : 'Update ticket information'}</p>
                  </div>
                </div>
              </Col>
              <Col md={4} className="text-end">
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Link to="/ticket" className="btn btn-outline-secondary d-flex align-items-center gap-2">
                    <ArrowLeft size={16} />
                    Back to List
                  </Link>
                </motion.div>
              </Col>
            </Row>
          </div>
        </Card>
      </motion.div>

      {/* Debug Info */}
      <motion.div variants={fieldVariants} className="mb-4">
        <Card className="border-0 shadow-sm bg-light">
          <div className="p-3">
            <h6 className="mb-2">Debug Info:</h6>
            <div className="row">
              <div className="col-md-4">
                <small>
                  Categories: {ticketCategories.length} (Loading: {ticketCategoryLoading ? 'Yes' : 'No'})
                </small>
                <br />
                <small className="text-muted">{ticketCategories.map(cat => `${cat.id}:${cat.name}`).join(', ')}</small>
              </div>
              <div className="col-md-4">
                <small>
                  Priorities: {ticketPriorities.length} (Loading: {ticketPriorityLoading ? 'Yes' : 'No'})
                </small>
                <br />
                <small className="text-muted">{ticketPriorities.map(pri => `${pri.id}:${pri.name}`).join(', ')}</small>
              </div>
              <div className="col-md-4">
                <small>
                  Users: {users.length} (Loading: {userLoading ? 'Yes' : 'No'})
                </small>
                <br />
                <small className="text-muted">{users.map(user => `${user.id}:${user.login}`).join(', ')}</small>
              </div>
            </div>
          </div>
        </Card>
      </motion.div>

      {/* Form */}
      <motion.div variants={fieldVariants}>
        <Card className="border-0 shadow-sm">
          <div className="p-4">
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              <ValidatedField
                name="title"
                label="Title"
                placeholder="Enter a descriptive title for your ticket"
                validate={{
                  required: { value: true, message: 'Title is required.' },
                  minLength: { value: 5, message: 'Title must be at least 5 characters long.' },
                }}
              />
              <ValidatedField
                name="description"
                label="Description"
                type="textarea"
                rows={4}
                placeholder="Provide detailed information about your issue or request"
                validate={{
                  required: { value: true, message: 'Description is required.' },
                  minLength: { value: 20, message: 'Description must be at least 20 characters long.' },
                }}
              />
              <ValidatedField
                name="category"
                label="Category"
                type="select"
                validate={{
                  required: { value: true, message: 'Category is required.' },
                }}
              >
                <option value="">Select a category</option>
                {ticketCategories.map(otherEntity => (
                  <option value={otherEntity.id} key={otherEntity.id}>
                    {otherEntity.name}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                name="priority"
                label="Priority"
                type="select"
                validate={{
                  required: { value: true, message: 'Priority is required.' },
                }}
              >
                <option value="">Select priority level</option>
                {ticketPriorities.map(otherEntity => (
                  <option value={otherEntity.id} key={otherEntity.id}>
                    {otherEntity.name}
                  </option>
                ))}
              </ValidatedField>
              {isNew && (
                <ValidatedField
                  name="user"
                  label="User"
                  type="select"
                  validate={{
                    required: { value: true, message: 'User is required.' },
                  }}
                >
                  <option value="">Select a user</option>
                  {users.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.login}
                    </option>
                  ))}
                </ValidatedField>
              )}
              {!isNew && (
                <ValidatedField name="status" label="Status" type="select">
                  {ticketStatusValues.map(otherEntity => (
                    <option value={otherEntity} key={otherEntity}>
                      {otherEntity}
                    </option>
                  ))}
                </ValidatedField>
              )}
              {!isNew && <ValidatedField name="creationDate" label="Creation Date" type="datetime-local" />}

              {/* Action Buttons */}
              <motion.div variants={fieldVariants} className="d-flex gap-3 justify-content-end mt-4 pt-4 border-top">
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Link to="/ticket" className="btn btn-outline-secondary">
                    Cancel
                  </Link>
                </motion.div>
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Button color="primary" type="submit" disabled={updating} className="d-flex align-items-center gap-2">
                    <AnimatePresence mode="wait">
                      {updating ? (
                        <motion.div
                          key="loading"
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          exit={{ opacity: 0 }}
                          className="spinner-border spinner-border-sm"
                          role="status"
                        >
                          <span className="visually-hidden">Loading...</span>
                        </motion.div>
                      ) : (
                        <motion.div key="save" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                          <Save size={16} />
                        </motion.div>
                      )}
                    </AnimatePresence>
                    {updating ? 'Saving...' : isNew ? 'Create Ticket' : 'Update Ticket'}
                  </Button>
                </motion.div>
              </motion.div>
            </ValidatedForm>
          </div>
        </Card>
      </motion.div>
    </motion.div>
  );
};

export default TicketUpdate;
