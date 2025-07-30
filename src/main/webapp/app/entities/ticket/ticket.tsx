import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table, Badge, Card, Row, Col, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faPlus, faSync, faSearch, faFilter } from '@fortawesome/free-solid-svg-icons';
import { motion, AnimatePresence } from 'framer-motion';
import { Ticket as TicketIcon, Plus, RefreshCw, Search, Filter, Trash2 } from 'lucide-react';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, deleteEntity } from './ticket.reducer';
import './ticket.scss';

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1,
      delayChildren: 0.2,
    },
  },
};

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.5,
      ease: 'easeOut' as const,
    },
  },
  exit: {
    opacity: 0,
    y: -20,
    transition: {
      duration: 0.3,
    },
  },
};

const getStatusColor = (status: string) => {
  switch (status?.toUpperCase()) {
    case 'OPEN':
      return 'warning';
    case 'IN_PROGRESS':
      return 'info';
    case 'RESOLVED':
      return 'success';
    case 'CLOSED':
      return 'secondary';
    default:
      return 'primary';
  }
};

const getPriorityColor = (priority: string) => {
  switch (priority?.toUpperCase()) {
    case 'LOW':
      return 'success';
    case 'MEDIUM':
      return 'warning';
    case 'HIGH':
      return 'danger';
    case 'CRITICAL':
      return 'danger';
    default:
      return 'primary';
  }
};

export const Ticket = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [ticketToDelete, setTicketToDelete] = useState(null);

  const ticketList = useAppSelector(state => state.ticket.entities);
  const loading = useAppSelector(state => state.ticket.loading);
  const totalItems = useAppSelector(state => state.ticket.totalItems);
  const updateSuccess = useAppSelector(state => state.ticket.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const handleDeleteClick = ticket => {
    setTicketToDelete(ticket);
    setDeleteModalOpen(true);
  };

  const handleDeleteConfirm = () => {
    if (ticketToDelete) {
      dispatch(deleteEntity(ticketToDelete.id));
    }
  };

  const handleDeleteCancel = () => {
    setDeleteModalOpen(false);
    setTicketToDelete(null);
  };

  useEffect(() => {
    if (updateSuccess) {
      setDeleteModalOpen(false);
      setTicketToDelete(null);
    }
  }, [updateSuccess]);

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <motion.div initial="hidden" animate="visible" variants={containerVariants} className="ticket-container">
      {/* Header Section */}
      <motion.div variants={itemVariants} className="mb-4">
        <Card className="border-0 shadow-sm">
          <div className="p-4">
            <Row className="align-items-center">
              <Col md={6}>
                <div className="d-flex align-items-center">
                  <motion.div whileHover={{ rotate: 360 }} transition={{ duration: 0.6 }} className="me-3">
                    <TicketIcon size={32} className="text-primary" />
                  </motion.div>
                  <div>
                    <h2 data-cy="TicketHeading" className="mb-0 fw-bold text-gradient">
                      Support Tickets
                    </h2>
                    <p className="text-muted mb-0">Manage and track support requests</p>
                  </div>
                </div>
              </Col>
              <Col md={6} className="text-end">
                <div className="d-flex gap-2 justify-content-end">
                  <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                    <Button color="secondary" onClick={handleSyncList} disabled={loading} className="d-flex align-items-center gap-2">
                      <motion.div animate={{ rotate: loading ? 360 : 0 }} transition={{ duration: 1, repeat: loading ? Infinity : 0 }}>
                        <RefreshCw size={16} />
                      </motion.div>
                      Refresh
                    </Button>
                  </motion.div>
                  <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                    <Link to="/ticket/new" className="btn btn-primary d-flex align-items-center gap-2" data-cy="entityCreateButton">
                      <Plus size={16} />
                      Create Ticket
                    </Link>
                  </motion.div>
                </div>
              </Col>
            </Row>
          </div>
        </Card>
      </motion.div>

      {/* Stats Cards */}
      <motion.div variants={itemVariants} className="mb-4">
        <Row>
          <Col md={3}>
            <motion.div whileHover={{ y: -5 }} className="mb-3">
              <Card className="border-0 shadow-sm h-100">
                <div className="p-3 text-center">
                  <div className="text-primary mb-2">
                    <TicketIcon size={24} />
                  </div>
                  <h4 className="mb-1 fw-bold">{totalItems || 0}</h4>
                  <p className="text-muted mb-0 small">Total Tickets</p>
                </div>
              </Card>
            </motion.div>
          </Col>
          <Col md={3}>
            <motion.div whileHover={{ y: -5 }} className="mb-3">
              <Card className="border-0 shadow-sm h-100">
                <div className="p-3 text-center">
                  <div className="text-warning mb-2">
                    <TicketIcon size={24} />
                  </div>
                  <h4 className="mb-1 fw-bold">{ticketList?.filter(t => t.status === 'OPEN')?.length || 0}</h4>
                  <p className="text-muted mb-0 small">Open</p>
                </div>
              </Card>
            </motion.div>
          </Col>
          <Col md={3}>
            <motion.div whileHover={{ y: -5 }} className="mb-3">
              <Card className="border-0 shadow-sm h-100">
                <div className="p-3 text-center">
                  <div className="text-info mb-2">
                    <TicketIcon size={24} />
                  </div>
                  <h4 className="mb-1 fw-bold">{ticketList?.filter(t => t.status === 'IN_PROGRESS')?.length || 0}</h4>
                  <p className="text-muted mb-0 small">In Progress</p>
                </div>
              </Card>
            </motion.div>
          </Col>
          <Col md={3}>
            <motion.div whileHover={{ y: -5 }} className="mb-3">
              <Card className="border-0 shadow-sm h-100">
                <div className="p-3 text-center">
                  <div className="text-success mb-2">
                    <TicketIcon size={24} />
                  </div>
                  <h4 className="mb-1 fw-bold">{ticketList?.filter(t => t.status === 'RESOLVED')?.length || 0}</h4>
                  <p className="text-muted mb-0 small">Resolved</p>
                </div>
              </Card>
            </motion.div>
          </Col>
        </Row>
      </motion.div>

      {/* Table Section */}
      <motion.div variants={itemVariants}>
        <Card className="border-0 shadow-sm">
          <div className="p-0">
            <div className="table-responsive">
              <Table className="table-hover mb-0" data-cy="entityTable">
                <thead>
                  <tr>
                    <th className="border-0">
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        className="btn btn-link text-decoration-none p-0 fw-bold"
                        onClick={sort('id')}
                      >
                        ID
                        <FontAwesomeIcon icon={getSortIconByFieldName('id')} className="ms-1" />
                      </motion.button>
                    </th>
                    <th className="border-0">
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        className="btn btn-link text-decoration-none p-0 fw-bold"
                        onClick={sort('title')}
                      >
                        Title
                        <FontAwesomeIcon icon={getSortIconByFieldName('title')} className="ms-1" />
                      </motion.button>
                    </th>
                    <th className="border-0">
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        className="btn btn-link text-decoration-none p-0 fw-bold"
                        onClick={sort('creationDate')}
                      >
                        Created
                        <FontAwesomeIcon icon={getSortIconByFieldName('creationDate')} className="ms-1" />
                      </motion.button>
                    </th>
                    <th className="border-0 fw-bold">Category</th>
                    <th className="border-0 fw-bold">Priority</th>
                    <th className="border-0 fw-bold">Status</th>
                    <th className="border-0 fw-bold">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <AnimatePresence>
                    {ticketList && ticketList.length > 0 ? (
                      ticketList.map((ticket, index) => (
                        <motion.tr
                          key={ticket.id}
                          variants={itemVariants}
                          initial="hidden"
                          animate="visible"
                          exit="exit"
                          custom={index}
                          whileHover={{
                            scale: 1.01,
                            backgroundColor: 'var(--gray-50)',
                          }}
                          className="border-bottom"
                        >
                          <td className="align-middle">
                            <span className="fw-bold text-primary">#{ticket.id}</span>
                          </td>
                          <td className="align-middle">
                            <div>
                              <div className="fw-semibold">{ticket.title}</div>
                              <div className="text-muted small">{ticket.description?.substring(0, 60)}...</div>
                            </div>
                          </td>
                          <td className="align-middle">
                            <TextFormat type="date" value={ticket.creationDate} format={APP_DATE_FORMAT} />
                          </td>
                          <td className="align-middle">
                            <Badge color="primary" className="px-3 py-2">
                              {ticket.category?.name || 'N/A'}
                            </Badge>
                          </td>
                          <td className="align-middle">
                            <Badge color={getPriorityColor(ticket.priority?.name)} className="px-3 py-2">
                              {ticket.priority?.name || 'N/A'}
                            </Badge>
                          </td>
                          <td className="align-middle">
                            <Badge color={getStatusColor(ticket.status)} className="px-3 py-2">
                              {ticket.status || 'N/A'}
                            </Badge>
                          </td>
                          <td className="align-middle">
                            <div className="d-flex gap-2">
                              <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}>
                                <Link to={`/ticket/${ticket.id}`} className="btn btn-sm btn-outline-primary" data-cy="entityDetailsButton">
                                  View
                                </Link>
                              </motion.div>
                              <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}>
                                <Link
                                  to={`/ticket/${ticket.id}/edit`}
                                  className="btn btn-sm btn-outline-secondary"
                                  data-cy="entityEditButton"
                                >
                                  Edit
                                </Link>
                              </motion.div>
                              <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}>
                                <Button
                                  color="danger"
                                  size="sm"
                                  outline
                                  onClick={() => handleDeleteClick(ticket)}
                                  className="d-flex align-items-center gap-1"
                                  data-cy="entityDeleteButton"
                                >
                                  <Trash2 size={14} />
                                  Delete
                                </Button>
                              </motion.div>
                            </div>
                          </td>
                        </motion.tr>
                      ))
                    ) : (
                      <motion.tr initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="text-center">
                        <td colSpan={7} className="py-5">
                          <div className="text-muted">
                            <TicketIcon size={48} className="mb-3 opacity-50" />
                            <h5>No tickets found</h5>
                            <p>Create your first support ticket to get started</p>
                            <Link to="/ticket/new" className="btn btn-primary">
                              <Plus size={16} className="me-2" />
                              Create Ticket
                            </Link>
                          </div>
                        </td>
                      </motion.tr>
                    )}
                  </AnimatePresence>
                </tbody>
              </Table>
            </div>
          </div>
        </Card>
      </motion.div>

      {/* Pagination */}
      <motion.div variants={itemVariants} className="mt-4">
        <Row className="justify-content-between align-items-center">
          <Col md={6}>
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </Col>
          <Col md={6} className="text-end">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </Col>
        </Row>
      </motion.div>

      {/* Delete Confirmation Modal */}
      <Modal isOpen={deleteModalOpen} toggle={handleDeleteCancel} centered>
        <ModalHeader toggle={handleDeleteCancel} className="border-0">
          <div className="d-flex align-items-center gap-2">
            <Trash2 size={20} className="text-danger" />
            <span>Confirm Delete</span>
          </div>
        </ModalHeader>
        <ModalBody className="py-4">
          <div className="text-center">
            <div className="mb-3">
              <Trash2 size={48} className="text-danger opacity-75" />
            </div>
            <h5 className="mb-3">Are you sure you want to delete this ticket?</h5>
            {ticketToDelete && (
              <div className="bg-light p-3 rounded">
                <p className="mb-1">
                  <strong>Title:</strong> {ticketToDelete.title}
                </p>
                <p className="mb-1">
                  <strong>ID:</strong> #{ticketToDelete.id}
                </p>
                <p className="mb-0">
                  <strong>Status:</strong> {ticketToDelete.status}
                </p>
              </div>
            )}
            <p className="text-muted mt-3 mb-0">This action cannot be undone. The ticket will be permanently deleted.</p>
          </div>
        </ModalBody>
        <ModalFooter className="border-0">
          <Button color="secondary" onClick={handleDeleteCancel} className="px-4">
            Cancel
          </Button>
          <Button color="danger" onClick={handleDeleteConfirm} className="px-4" data-cy="confirm-delete-button">
            <Trash2 size={16} className="me-2" />
            Delete Ticket
          </Button>
        </ModalFooter>
      </Modal>
    </motion.div>
  );
};

export default Ticket;
