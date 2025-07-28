package com.mycompany.myapp.cucumber;

import org.springframework.test.context.web.WebAppConfiguration;

import com.mycompany.myapp.IntegrationTest;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
