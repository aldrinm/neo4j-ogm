/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 *  conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.neo4j.ogm.persistence.examples.companies;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.ogm.domain.companies.annotated.Company;
import org.neo4j.ogm.domain.companies.annotated.Device;
import org.neo4j.ogm.domain.companies.annotated.Person;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.testutil.MultiDriverTestClass;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Luanne Misquitta
 * @author Vince Bickers
 */
public class CompaniesIntegrationTest extends MultiDriverTestClass {

    private Session session;

    @BeforeClass
    public static void init() throws IOException {
        sessionFactory = new SessionFactory(driver, "org.neo4j.ogm.domain.companies.annotated");
    }

    @Before
    public void setUp() {
        session = sessionFactory.openSession();
    }

    @After
    public void teardown() {
        session.purgeDatabase();
    }

    @Test
    public void employeesShouldNotBeSetAsOwnersWhenLoadingCompanies() {
        Company company = new Company("GraphAware");
        Person michal = new Person("Michal");
        Person daniela = new Person("Daniela");
        Set<Person> employees = new HashSet<>();
        employees.add(michal);
        employees.add(daniela);
        company.setEmployees(employees);
        session.save(company);
        session.clear();

        company = session.load(Company.class, company.getId());
        assertThat(company).isNotNull();
        assertThat(company.getEmployees()).hasSize(2);
        assertThat(company.getOwners()).isNull();

        for (Person employee : company.getEmployees()) {
            assertThat(employee.getEmployer()).isNotNull();
            assertThat(employee.getOwns()).isNull();
        }
    }

    @Test
    public void employeesAndOwnersShouldBeLoaded() {
        Company company = new Company("GraphAware");
        Person michal = new Person("Michal");
        Person daniela = new Person("Daniela");
        michal.setOwns(Collections.singleton(company));
        daniela.setOwns(Collections.singleton(company));
        Set<Person> employees = new HashSet<>();
        employees.add(michal);
        employees.add(daniela);
        company.setEmployees(employees);
        company.setOwners(employees);
        session.save(company);
        session.clear();

        company = session.load(Company.class, company.getId());
        assertThat(company).isNotNull();
        assertThat(company.getEmployees()).hasSize(2);
        assertThat(company.getOwners()).hasSize(2);

        for (Person employee : company.getEmployees()) {
            assertThat(employee.getEmployer().getId()).isEqualTo(company.getId());
            assertThat(employee.getOwns()).hasSize(1);
            assertThat(employee.getOwns().iterator().next().getId()).isEqualTo(company.getId());
        }
    }

    /**
     * @see Issue 112
     */
    @Test
    public void shouldDeleteUndirectedRelationship() {
        Person person = new Person();
        Device device = new Device();
        person.addDevice(device);
        session.save(person);
        person.removeDevice(device);
        assertThat(person.getDevices()).isEmpty();
        session.save(person);

        session.clear();
        person = session.load(Person.class, person.getId());
        assertThat(person).isNotNull();
        assertThat(person.getDevices()).isNull();
    }
}
