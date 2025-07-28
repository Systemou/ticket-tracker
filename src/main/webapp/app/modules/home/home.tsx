import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Alert, Col, Row, Card, Button, Container } from 'reactstrap';
import { motion } from 'framer-motion';
import { Ticket, MessageCircle, Clock, CheckCircle, AlertTriangle, Users, Shield, ArrowRight, Plus } from 'lucide-react';

import { useAppSelector } from 'app/config/store';

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.2,
      delayChildren: 0.1,
    },
  },
};

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.6,
    },
  },
};

const featureCardVariants = {
  hidden: { opacity: 0, scale: 0.9 },
  visible: {
    opacity: 1,
    scale: 1,
    transition: {
      duration: 0.5,
    },
  },
  hover: {
    scale: 1.05,
    transition: {
      duration: 0.2,
    },
  },
};

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  const features = [
    {
      icon: <Ticket className="text-primary" size={32} />,
      title: 'Quick Ticket Submission',
      description: 'Submit support tickets easily with our streamlined form',
    },
    {
      icon: <MessageCircle className="text-info" size={32} />,
      title: 'Real-time Updates',
      description: 'Track your ticket status and receive instant notifications',
    },
    {
      icon: <Clock className="text-warning" size={32} />,
      title: 'Fast Response Time',
      description: 'Our support team responds within 24 hours',
    },
    {
      icon: <Shield className="text-success" size={32} />,
      title: 'Secure & Private',
      description: 'Your information is protected with enterprise-grade security',
    },
  ];

  return (
    <motion.div variants={containerVariants} initial="hidden" animate="visible" className="home-container">
      {/* Hero Section */}
      <motion.div variants={itemVariants} className="hero-section text-center py-5">
        <Container>
          <h1 className="display-3 fw-bold mb-4">Welcome to Support Ticket Tracker</h1>
          <p className="lead mb-5">
            Get the help you need quickly and efficiently. Our support team is here to assist you with any issues or questions.
          </p>

          {account?.login ? (
            <motion.div variants={itemVariants} className="d-flex justify-content-center gap-3 flex-wrap">
              <Button
                color="primary"
                size="lg"
                tag={Link}
                to="/ticket/new"
                className="d-flex align-items-center gap-2"
                data-cy="submit-ticket-button"
              >
                <Plus size={20} />
                Submit New Ticket
              </Button>
              <Button
                color="outline-primary"
                size="lg"
                tag={Link}
                to="/ticket"
                className="d-flex align-items-center gap-2"
                data-cy="view-tickets-button"
              >
                <Ticket size={20} />
                View My Tickets
              </Button>
            </motion.div>
          ) : (
            <motion.div variants={itemVariants} className="d-flex justify-content-center gap-3 flex-wrap">
              <Button color="primary" size="lg" tag={Link} to="/login" className="d-flex align-items-center gap-2" data-cy="login-button">
                Sign In
                <ArrowRight size={20} />
              </Button>
              <Button
                color="outline-primary"
                size="lg"
                tag={Link}
                to="/account/register"
                className="d-flex align-items-center gap-2"
                data-cy="register-button"
              >
                Create Account
              </Button>
            </motion.div>
          )}
        </Container>
      </motion.div>

      {/* Features Section */}
      <motion.div variants={itemVariants} className="features-section py-5 bg-light">
        <Container>
          <div className="text-center mb-5">
            <h2 className="h1 mb-3">Why Choose Our Support Platform?</h2>
            <p className="text-muted">We provide comprehensive support solutions designed for your needs</p>
          </div>

          <Row className="g-4">
            {features.map((feature, index) => (
              <Col key={index} lg={3} md={6}>
                <motion.div variants={featureCardVariants} whileHover="hover" className="h-100">
                  <Card className="h-100 border-0 shadow-sm p-4 text-center">
                    <div className="mb-3">{feature.icon}</div>
                    <h5 className="mb-3">{feature.title}</h5>
                    <p className="text-muted mb-0">{feature.description}</p>
                  </Card>
                </motion.div>
              </Col>
            ))}
          </Row>
        </Container>
      </motion.div>

      {/* Login Alert Section */}
      {!account?.login && (
        <motion.div variants={itemVariants} className="py-4">
          <Container>
            <Alert color="info" className="text-center">
              <div className="d-flex align-items-center justify-content-center gap-2 mb-2">
                <Users size={20} />
                <strong>Get Started Today</strong>
              </div>
              <p className="mb-3">
                Create an account to submit support tickets and track their progress. Our platform provides a seamless experience for
                getting the help you need.
              </p>
              <div className="d-flex justify-content-center gap-2 flex-wrap">
                <Button color="primary" tag={Link} to="/login" size="sm" data-cy="hero-login-button">
                  Sign In
                </Button>
                <Button color="outline-primary" tag={Link} to="/account/register" size="sm" data-cy="hero-register-button">
                  Create Account
                </Button>
              </div>
            </Alert>
          </Container>
        </motion.div>
      )}

      {/* Support Information */}
      <motion.div variants={itemVariants} className="py-5">
        <Container>
          <Row className="align-items-center">
            <Col lg={6}>
              <h3 className="mb-4">Need Immediate Assistance?</h3>
              <p className="text-muted mb-4">
                Our support team is available to help you resolve issues quickly. Submit a ticket and we&apos;ll get back to you as soon as
                possible.
              </p>
              <div className="d-flex flex-column gap-2">
                <div className="d-flex align-items-center gap-2">
                  <CheckCircle className="text-success" size={20} />
                  <span>24/7 Ticket Submission</span>
                </div>
                <div className="d-flex align-items-center gap-2">
                  <Clock className="text-warning" size={20} />
                  <span>Response within 24 hours</span>
                </div>
                <div className="d-flex align-items-center gap-2">
                  <AlertTriangle className="text-info" size={20} />
                  <span>Priority-based support</span>
                </div>
              </div>
            </Col>
            <Col lg={6} className="text-center">
              <div className="support-illustration">
                <Ticket size={120} className="text-primary opacity-25" />
              </div>
            </Col>
          </Row>
        </Container>
      </motion.div>
    </motion.div>
  );
};

export default Home;
