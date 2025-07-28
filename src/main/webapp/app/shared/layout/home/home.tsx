import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, Row, Col, Badge, Button } from 'reactstrap';
import { motion } from 'framer-motion';
import { Ticket, Users, Clock, CheckCircle, AlertTriangle, TrendingUp, Plus, ArrowRight, Activity } from 'lucide-react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/ticket/ticket.reducer';

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
};

const statCardVariants = {
  hidden: { opacity: 0, scale: 0.8 },
  visible: {
    opacity: 1,
    scale: 1,
    transition: {
      duration: 0.6,
      ease: 'easeOut' as const,
    },
  },
  hover: {
    scale: 1.05,
    y: -8,
    transition: {
      duration: 0.3,
    },
  },
};

export const Home = () => {
  const dispatch = useAppDispatch();
  const ticketList = useAppSelector(state => state.ticket.entities);
  const loading = useAppSelector(state => state.ticket.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const getStatusCount = (status: string) => {
    return ticketList?.filter(t => t.status === status)?.length || 0;
  };

  const getPriorityCount = (priority: string) => {
    return ticketList?.filter(t => t.priority?.name === priority)?.length || 0;
  };

  const recentTickets = ticketList?.slice(0, 5) || [];

  const stats = [
    {
      title: 'Total Tickets',
      value: ticketList?.length || 0,
      icon: Ticket,
      color: 'primary',
      gradient: 'linear-gradient(135deg, var(--primary-color), var(--primary-dark))',
    },
    {
      title: 'Open Tickets',
      value: getStatusCount('OPEN'),
      icon: AlertTriangle,
      color: 'warning',
      gradient: 'linear-gradient(135deg, var(--warning-color), #d97706)',
    },
    {
      title: 'In Progress',
      value: getStatusCount('IN_PROGRESS'),
      icon: Clock,
      color: 'info',
      gradient: 'linear-gradient(135deg, var(--info-color), #2563eb)',
    },
    {
      title: 'Resolved',
      value: getStatusCount('RESOLVED'),
      icon: CheckCircle,
      color: 'success',
      gradient: 'linear-gradient(135deg, var(--success-color), #059669)',
    },
  ];

  return (
    <motion.div initial="hidden" animate="visible" variants={containerVariants} className="home-dashboard">
      {/* Welcome Header */}
      <motion.div variants={itemVariants} className="mb-4">
        <Card className="border-0 shadow-sm">
          <div className="p-4">
            <Row className="align-items-center">
              <Col md={8}>
                <div className="d-flex align-items-center">
                  <motion.div whileHover={{ rotate: 360 }} transition={{ duration: 0.6 }} className="me-3">
                    <Activity size={32} className="text-primary" />
                  </motion.div>
                  <div>
                    <h1 className="mb-0 fw-bold text-gradient">Welcome to Ticket Tracker</h1>
                    <p className="text-muted mb-0">Manage and track support requests efficiently</p>
                  </div>
                </div>
              </Col>
              <Col md={4} className="text-end">
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Link to="/ticket/new" className="btn btn-primary d-flex align-items-center gap-2">
                    <Plus size={16} />
                    Create Ticket
                  </Link>
                </motion.div>
              </Col>
            </Row>
          </div>
        </Card>
      </motion.div>

      {/* Statistics Cards */}
      <motion.div variants={itemVariants} className="mb-4">
        <Row>
          {stats.map((stat, index) => (
            <Col md={3} key={stat.title}>
              <motion.div variants={statCardVariants} whileHover="hover" className="mb-3">
                <Card className="border-0 shadow-sm h-100 stat-card">
                  <div className="p-4 text-center position-relative overflow-hidden">
                    <motion.div
                      className="stat-icon mb-3"
                      style={{ color: `var(--${stat.color}-color)` }}
                      whileHover={{ scale: 1.2, rotate: 5 }}
                    >
                      <stat.icon size={32} />
                    </motion.div>
                    <h2 className="mb-1 fw-bold" style={{ color: `var(--${stat.color}-color)` }}>
                      {stat.value}
                    </h2>
                    <p className="text-muted mb-0 small">{stat.title}</p>
                    <motion.div
                      className="stat-background"
                      style={{
                        background: stat.gradient,
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        opacity: 0.05,
                        zIndex: -1,
                      }}
                      whileHover={{ scale: 1.1 }}
                    />
                  </div>
                </Card>
              </motion.div>
            </Col>
          ))}
        </Row>
      </motion.div>

      {/* Priority Overview */}
      <motion.div variants={itemVariants} className="mb-4">
        <Row>
          <Col md={6}>
            <motion.div whileHover={{ y: -5 }}>
              <Card className="border-0 shadow-sm h-100">
                <div className="p-4">
                  <div className="d-flex align-items-center justify-content-between mb-3">
                    <h5 className="mb-0 fw-bold">Priority Distribution</h5>
                    <TrendingUp size={20} className="text-primary" />
                  </div>
                  <div className="space-y-3">
                    <div className="d-flex justify-content-between align-items-center">
                      <div className="d-flex align-items-center">
                        <div className="priority-dot bg-danger me-2"></div>
                        <span>High/Critical</span>
                      </div>
                      <Badge color="danger" className="px-3 py-2">
                        {getPriorityCount('HIGH') + getPriorityCount('CRITICAL')}
                      </Badge>
                    </div>
                    <div className="d-flex justify-content-between align-items-center">
                      <div className="d-flex align-items-center">
                        <div className="priority-dot bg-warning me-2"></div>
                        <span>Medium</span>
                      </div>
                      <Badge color="warning" className="px-3 py-2">
                        {getPriorityCount('MEDIUM')}
                      </Badge>
                    </div>
                    <div className="d-flex justify-content-between align-items-center">
                      <div className="d-flex align-items-center">
                        <div className="priority-dot bg-success me-2"></div>
                        <span>Low</span>
                      </div>
                      <Badge color="success" className="px-3 py-2">
                        {getPriorityCount('LOW')}
                      </Badge>
                    </div>
                  </div>
                </div>
              </Card>
            </motion.div>
          </Col>
          <Col md={6}>
            <motion.div whileHover={{ y: -5 }}>
              <Card className="border-0 shadow-sm h-100">
                <div className="p-4">
                  <div className="d-flex align-items-center justify-content-between mb-3">
                    <h5 className="mb-0 fw-bold">Quick Actions</h5>
                    <Activity size={20} className="text-primary" />
                  </div>
                  <div className="d-grid gap-3">
                    <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
                      <Link to="/ticket/new" className="btn btn-primary w-100 d-flex align-items-center justify-content-between">
                        <span>Create New Ticket</span>
                        <Plus size={16} />
                      </Link>
                    </motion.div>
                    <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
                      <Link to="/ticket" className="btn btn-outline-primary w-100 d-flex align-items-center justify-content-between">
                        <span>View All Tickets</span>
                        <ArrowRight size={16} />
                      </Link>
                    </motion.div>
                    <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
                      <Link
                        to="/ticket-category"
                        className="btn btn-outline-secondary w-100 d-flex align-items-center justify-content-between"
                      >
                        <span>Manage Categories</span>
                        <ArrowRight size={16} />
                      </Link>
                    </motion.div>
                  </div>
                </div>
              </Card>
            </motion.div>
          </Col>
        </Row>
      </motion.div>

      {/* Recent Tickets */}
      <motion.div variants={itemVariants}>
        <Card className="border-0 shadow-sm">
          <div className="p-4">
            <div className="d-flex align-items-center justify-content-between mb-4">
              <h5 className="mb-0 fw-bold">Recent Tickets</h5>
              <Link to="/ticket" className="btn btn-outline-primary btn-sm">
                View All
              </Link>
            </div>

            {loading ? (
              <div className="text-center py-4">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              </div>
            ) : recentTickets.length > 0 ? (
              <div className="table-responsive">
                <table className="table table-hover mb-0">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Title</th>
                      <th>Status</th>
                      <th>Priority</th>
                      <th>Created</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentTickets.map((ticket, index) => (
                      <motion.tr
                        key={ticket.id}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: index * 0.1 }}
                        whileHover={{ backgroundColor: 'var(--gray-50)' }}
                      >
                        <td>
                          <span className="fw-bold text-primary">#{ticket.id}</span>
                        </td>
                        <td>
                          <div>
                            <div className="fw-semibold">{ticket.title}</div>
                            <div className="text-muted small">{ticket.description?.substring(0, 50)}...</div>
                          </div>
                        </td>
                        <td>
                          <Badge
                            color={
                              ticket.status === 'OPEN'
                                ? 'warning'
                                : ticket.status === 'IN_PROGRESS'
                                  ? 'info'
                                  : ticket.status === 'RESOLVED'
                                    ? 'success'
                                    : 'secondary'
                            }
                            className="px-3 py-2"
                          >
                            {ticket.status}
                          </Badge>
                        </td>
                        <td>
                          <Badge
                            color={ticket.priority?.name === 'LOW' ? 'success' : ticket.priority?.name === 'MEDIUM' ? 'warning' : 'danger'}
                            className="px-3 py-2"
                          >
                            {ticket.priority?.name || 'N/A'}
                          </Badge>
                        </td>
                        <td className="text-muted small">{new Date(ticket.creationDate).toLocaleDateString()}</td>
                        <td>
                          <Link to={`/ticket/${ticket.id}`} className="btn btn-sm btn-outline-primary">
                            View
                          </Link>
                        </td>
                      </motion.tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="text-center py-5">
                <Ticket size={48} className="text-muted mb-3" />
                <h6 className="text-muted">No tickets found</h6>
                <p className="text-muted small">Create your first ticket to get started</p>
                <Link to="/ticket/new" className="btn btn-primary">
                  <Plus size={16} className="me-2" />
                  Create Ticket
                </Link>
              </div>
            )}
          </div>
        </Card>
      </motion.div>
    </motion.div>
  );
};
