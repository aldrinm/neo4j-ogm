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

package org.neo4j.ogm.persistence.examples.hierarchy.dualRelationships;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.ogm.domain.hierarchy.dualRelation.DataView;
import org.neo4j.ogm.domain.hierarchy.dualRelation.DataViewOwned;
import org.neo4j.ogm.domain.hierarchy.dualRelation.Thing;
import org.neo4j.ogm.domain.hierarchy.dualRelation.ThingOwned;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.Utils;
import org.neo4j.ogm.testutil.MultiDriverTestClass;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test passes because DataView and DataViewOwned have their setSharedWith annotated with @Relationship.
 * If the setter were not annotated, these tests would fail.
 * //TODO revisit this after we take a decision on mapping strategies
 *
 * @author Vince Bickers
 * @author Luanne Misquitta
 * @see Issue 144
 */
public class DualRelationshipTest extends MultiDriverTestClass {

    private static SessionFactory sessionFactory;

    private Session session;

    @BeforeClass
    public static void oneTimeSetUp() {
        sessionFactory = new SessionFactory(driver, "org.neo4j.ogm.domain.hierarchy.dualRelation");
    }

    @Before
    public void init() throws IOException {
        session = sessionFactory.openSession();
    }

    @After
    public void cleanup() {
        session.purgeDatabase();
    }

    @Test
    public void shouldRehydrateProperlyUsingLoad() {

        Thing thing1 = new Thing();
        Thing thing2 = new Thing();
        DataView dataview = new DataView();
        dataview.setName("dataview");

        thing1.setName("owner");
        thing2.setName("shared");

        dataview.setOwner(thing1);
        dataview.getSharedWith().add(thing2);

        session.save(dataview);

        session.clear();

        DataView found = session.load(DataView.class, dataview.getId());

        assertThat(found.getOwner().getName()).isEqualTo("owner");
        assertThat(found.getSharedWith().get(0).getName()).isEqualTo("shared");
        assertThat(found.getSharedWith()).hasSize(1);
    }

    @Test
    public void shouldRehydrateProperlyWithQuery() {

        Thing thing1 = new Thing();
        Thing thing2 = new Thing();
        DataView dataview = new DataView();
        dataview.setName("dataview");

        thing1.setName("owner");
        thing2.setName("shared");

        dataview.setOwner(thing1);
        dataview.getSharedWith().add(thing2);

        session.save(dataview);

        session.clear();

        String query = "MATCH (n:DataView) WITH n MATCH p=(n)-[*0..1]-(m) RETURN n,nodes(p),rels(p)";

        DataView found = session.queryForObject(DataView.class, query, Utils.map());

        assertThat(found.getSharedWith()).hasSize(1);
        assertThat(found.getOwner().getName()).isEqualTo("owner");
        assertThat(found.getSharedWith().get(0).getName()).isEqualTo("shared");
    }

    @Test
    public void shouldRehydrateEntitiesWithAbstractParentProperlyUsingLoad() {

        ThingOwned thing1 = new ThingOwned();
        ThingOwned thing2 = new ThingOwned();
        DataViewOwned dataview = new DataViewOwned();
        dataview.setName("dataview");

        thing1.setName("owner");
        thing2.setName("shared");

        dataview.setOwner(thing1);
        dataview.getSharedWith().add(thing2);

        session.save(dataview);

        session.clear();

        DataViewOwned found = session.load(DataViewOwned.class, dataview.getId());

        assertThat(found.getOwner().getName()).isEqualTo("owner");
        assertThat(found.getSharedWith().get(0).getName()).isEqualTo("shared");
        assertThat(found.getSharedWith()).hasSize(1);
    }

    @Test
    public void shouldRehydrateEntitiesWithAbstractParentProperlyWithQuery() {

        ThingOwned thing1 = new ThingOwned();
        ThingOwned thing2 = new ThingOwned();
        DataViewOwned dataview = new DataViewOwned();
        dataview.setName("dataview");

        thing1.setName("owner");
        thing2.setName("shared");

        dataview.setOwner(thing1);
        dataview.getSharedWith().add(thing2);

        session.save(dataview);

        session.clear();

        String query = "MATCH (n:DataViewOwned) WITH n MATCH p=(n)-[*0..1]-(m) RETURN n,nodes(p),rels(p)";

        DataViewOwned found = session.queryForObject(DataViewOwned.class, query, Utils.map());

        assertThat(found).isNotNull();
        assertThat(found.getSharedWith()).hasSize(1);
        assertThat(found.getOwner().getName()).isEqualTo("owner");
        assertThat(found.getSharedWith().get(0).getName()).isEqualTo("shared");
    }
}
