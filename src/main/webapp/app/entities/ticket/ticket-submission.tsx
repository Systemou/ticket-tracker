import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ticket-submission.scss';
import { Button, Col, Row, Card, FormGroup, Label, Input, Form, Alert, Progress, Badge } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { motion, AnimatePresence } from 'framer-motion';
import { Ticket, Save, ArrowLeft, AlertCircle, CheckCircle, Clock, FileText, Tag, Flag, User, Calendar } from 'lucide-react';
import { toast } from 'react-toastify';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTicketCategories } from 'app/entities/ticket-category/ticket-category.reducer';
import { getEntities as getTicketPriorities } from 'app/entities/ticket-priority/ticket-priority.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';
import { createEntity, reset } from './ticket.reducer';

const formVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.6,
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

const progressSteps = [
  { id: 1, title: 'Basic Information', icon: <FileText size={20} /> },
  { id: 2, title: 'Category & Priority', icon: <Tag size={20} /> },
  { id: 3, title: 'Review & Submit', icon: <CheckCircle size={20} /> },
];

export const TicketSubmission = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: null,
    priority: null,
  });
  const [validationErrors, setValidationErrors] = useState<{
    title?: string;
    description?: string;
    category?: string;
    priority?: string;
  }>({});

  const ticketCategories = useAppSelector(state => state.ticketCategory.entities);
  const ticketPriorities = useAppSelector(state => state.ticketPriority.entities);
  const users = useAppSelector(state => state.userManagement.users || []);

  const loading = useAppSelector(state => state.ticket.loading);
  const updating = useAppSelector(state => state.ticket.updating);
  const updateSuccess = useAppSelector(state => state.ticket.updateSuccess);
  const ticketStatusValues = Object.keys(TicketStatus);

  const handleClose = () => {
    navigate('/ticket');
  };

  useEffect(() => {
    dispatch(reset());
    dispatch(getTicketCategories({}));
    dispatch(getTicketPriorities({}));
    dispatch(getUsers({}));
  }, [dispatch]);

  useEffect(() => {
    if (updateSuccess) {
      toast.success('Ticket submitted successfully! We will get back to you soon.');
      handleClose();
    }
  }, [updateSuccess]);

  const validateStep = step => {
    const errors: {
      title?: string;
      description?: string;
      category?: string;
      priority?: string;
    } = {};

    switch (step) {
      case 1:
        if (!formData.title || formData.title.trim().length < 5) {
          errors.title = 'Title must be at least 5 characters long';
        }
        if (!formData.description || formData.description.trim().length < 20) {
          errors.description = 'Description must be at least 20 characters long';
        }
        break;
      case 2:
        if (!formData.category) {
          errors.category = 'Please select a category';
        }
        if (!formData.priority) {
          errors.priority = 'Please select a priority';
        }
        break;
      default:
        break;
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleNext = () => {
    if (validateStep(currentStep)) {
      setCurrentStep(prev => Math.min(prev + 1, 3));
    }
  };

  const handlePrevious = () => {
    setCurrentStep(prev => Math.max(prev - 1, 1));
  };

  const handleSubmit = () => {
    if (validateStep(currentStep)) {
      const entity = {
        ...formData,
        category: ticketCategories.find(it => it.id.toString() === formData.category?.toString()),
        priority: ticketPriorities.find(it => it.id.toString() === formData.priority?.toString()),
        user: users.find(it => it.id.toString() === '1'), // Current user
        status: TicketStatus.OPEN,
        creationDate: convertDateTimeToServer(displayDefaultDateTime()),
      };

      dispatch(createEntity(entity));
    }
  };

  const updateFormData = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    // Clear validation error when user starts typing
    if (validationErrors[field]) {
      setValidationErrors(prev => ({ ...prev, [field]: null }));
    }
  };

  const getProgressPercentage = () => {
    return (currentStep / 3) * 100;
  };

  const renderStepContent = () => {
    switch (currentStep) {
      case 1:
        return (
          <motion.div variants={fieldVariants} className="step-content">
            <h4 className="mb-4">
              <FileText className="me-2" />
              Tell us about your issue
            </h4>

            <FormGroup>
              <Label for="title" className="form-label">
                <strong>Title *</strong>
                <small className="text-muted ms-2">(Minimum 5 characters)</small>
              </Label>
              <Input
                id="title"
                type="text"
                value={formData.title}
                onChange={e => updateFormData('title', e.target.value)}
                invalid={!!validationErrors.title}
                placeholder="Brief description of your issue..."
                data-cy="ticket-title-input"
              />
              {validationErrors.title && <div className="invalid-feedback d-block">{validationErrors.title}</div>}
            </FormGroup>

            <FormGroup>
              <Label for="description" className="form-label">
                <strong>Description *</strong>
                <small className="text-muted ms-2">(Minimum 20 characters)</small>
              </Label>
              <Input
                id="description"
                type="textarea"
                rows="6"
                value={formData.description}
                onChange={e => updateFormData('description', e.target.value)}
                invalid={!!validationErrors.description}
                placeholder="Please provide detailed information about your issue, including steps to reproduce, error messages, and any relevant context..."
                data-cy="ticket-description-input"
              />
              {validationErrors.description && <div className="invalid-feedback d-block">{validationErrors.description}</div>}
              <div className="form-text">Characters: {formData.description.length}/500</div>
            </FormGroup>
          </motion.div>
        );

      case 2:
        return (
          <motion.div variants={fieldVariants} className="step-content">
            <h4 className="mb-4">
              <Tag className="me-2" />
              Categorize your request
            </h4>

            <Row>
              <Col md={6}>
                <FormGroup>
                  <Label for="category" className="form-label">
                    <strong>Category *</strong>
                  </Label>
                  <Input
                    id="category"
                    type="select"
                    value={formData.category || ''}
                    onChange={e => updateFormData('category', e.target.value)}
                    invalid={!!validationErrors.category}
                    data-cy="ticket-category-select"
                  >
                    <option value="">Select a category</option>
                    {ticketCategories.map(category => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </Input>
                  {validationErrors.category && <div className="invalid-feedback d-block">{validationErrors.category}</div>}
                </FormGroup>
              </Col>

              <Col md={6}>
                <FormGroup>
                  <Label for="priority" className="form-label">
                    <strong>Priority *</strong>
                  </Label>
                  <Input
                    id="priority"
                    type="select"
                    value={formData.priority || ''}
                    onChange={e => updateFormData('priority', e.target.value)}
                    invalid={!!validationErrors.priority}
                    data-cy="ticket-priority-select"
                  >
                    <option value="">Select priority</option>
                    {ticketPriorities.map(priority => (
                      <option key={priority.id} value={priority.id}>
                        {priority.name}
                      </option>
                    ))}
                  </Input>
                  {validationErrors.priority && <div className="invalid-feedback d-block">{validationErrors.priority}</div>}
                </FormGroup>
              </Col>
            </Row>

            <div className="priority-info mt-4">
              <h6>Priority Guidelines:</h6>
              <div className="row">
                <div className="col-md-3">
                  <Badge color="success" className="w-100 mb-2">
                    LOW
                  </Badge>
                  <small>General questions, feature requests</small>
                </div>
                <div className="col-md-3">
                  <Badge color="warning" className="w-100 mb-2">
                    MEDIUM
                  </Badge>
                  <small>Minor issues, workarounds available</small>
                </div>
                <div className="col-md-3">
                  <Badge color="danger" className="w-100 mb-2">
                    HIGH
                  </Badge>
                  <small>Critical issues, system down</small>
                </div>
                <div className="col-md-3">
                  <Badge color="danger" className="w-100 mb-2">
                    CRITICAL
                  </Badge>
                  <small>Emergency, immediate attention needed</small>
                </div>
              </div>
            </div>
          </motion.div>
        );

      case 3:
        return (
          <motion.div variants={fieldVariants} className="step-content">
            <h4 className="mb-4">
              <CheckCircle className="me-2" />
              Review your ticket
            </h4>

            <Card className="p-4 mb-4">
              <Row>
                <Col md={8}>
                  <h5>{formData.title}</h5>
                  <p className="text-muted">{formData.description}</p>
                </Col>
                <Col md={4} className="text-end">
                  <Badge color="warning" className="mb-2">
                    {ticketCategories.find(c => c.id.toString() === formData.category?.toString())?.name}
                  </Badge>
                  <br />
                  <Badge color="danger">{ticketPriorities.find(p => p.id.toString() === formData.priority?.toString())?.name}</Badge>
                </Col>
              </Row>

              <hr />

              <div className="row text-muted">
                <div className="col-md-4">
                  <User size={16} className="me-1" />
                  Submitted by: You
                </div>
                <div className="col-md-4">
                  <Calendar size={16} className="me-1" />
                  Date: {new Date().toLocaleDateString()}
                </div>
                <div className="col-md-4">
                  <Clock size={16} className="me-1" />
                  Status: Open
                </div>
              </div>
            </Card>

            <Alert color="info">
              <CheckCircle size={20} className="me-2" />
              <strong>What happens next?</strong>
              <ul className="mb-0 mt-2">
                <li>Your ticket will be assigned to our support team</li>
                <li>You&apos;ll receive email updates on progress</li>
                <li>We aim to respond within 24 hours</li>
              </ul>
            </Alert>
          </motion.div>
        );

      default:
        return null;
    }
  };

  return (
    <motion.div variants={formVariants} initial="hidden" animate="visible" className="ticket-submission-container">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <Button color="link" onClick={handleClose} className="d-flex align-items-center gap-2" data-cy="back-button">
          <ArrowLeft size={20} />
          Back to Tickets
        </Button>

        <h2 className="mb-0">
          <Ticket className="me-2" />
          Submit Support Ticket
        </h2>
      </div>

      {/* Progress Bar */}
      <Card className="mb-4">
        <div className="p-4">
          <div className="d-flex justify-content-between align-items-center mb-3">
            {progressSteps.map((step, index) => (
              <div key={step.id} className={`d-flex align-items-center ${step.id <= currentStep ? 'text-primary' : 'text-muted'}`}>
                <div
                  className={`rounded-circle d-flex align-items-center justify-content-center me-2 ${
                    step.id <= currentStep ? 'bg-primary text-white' : 'bg-light'
                  }`}
                  style={{ width: '40px', height: '40px' }}
                >
                  {step.icon}
                </div>
                <span className="d-none d-md-inline">{step.title}</span>
              </div>
            ))}
          </div>
          <Progress value={getProgressPercentage()} className="mb-0" color="primary" data-cy="submission-progress" />
        </div>
      </Card>

      {/* Form Content */}
      <Card>
        <div className="p-4">
          <AnimatePresence mode="wait">{renderStepContent()}</AnimatePresence>

          {/* Navigation Buttons */}
          <div className="d-flex justify-content-between mt-4">
            <Button color="outline-secondary" onClick={handlePrevious} disabled={currentStep === 1} data-cy="previous-step-button">
              Previous
            </Button>

            <div>
              {currentStep < 3 ? (
                <Button color="primary" onClick={handleNext} disabled={loading} data-cy="next-step-button">
                  Next
                </Button>
              ) : (
                <Button
                  color="success"
                  onClick={handleSubmit}
                  disabled={loading || updating}
                  className="d-flex align-items-center gap-2"
                  data-cy="submit-ticket-button"
                >
                  {updating ? (
                    <>
                      <div className="spinner-border spinner-border-sm" role="status" />
                      Submitting...
                    </>
                  ) : (
                    <>
                      <Save size={20} />
                      Submit Ticket
                    </>
                  )}
                </Button>
              )}
            </div>
          </div>
        </div>
      </Card>
    </motion.div>
  );
};

export default TicketSubmission;
