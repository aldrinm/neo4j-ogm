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

package org.neo4j.ogm.persistence.examples.friendships;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.ogm.domain.friendships.Friendship;
import org.neo4j.ogm.domain.friendships.Person;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.testutil.MultiDriverTestClass;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vince Bickers
 * @author Luanne Misquitta
 */
public class FriendshipsRelationshipEntityTest extends MultiDriverTestClass {

    private Session session;

    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        sessionFactory = new SessionFactory(driver, "org.neo4j.ogm.domain.friendships");
    }

    @Before
    public void init() throws IOException {
        session = sessionFactory.openSession();
        session.purgeDatabase();
    }

    @Test
    public void shouldSaveFromStartObjectSetsAllObjectIds() {

        Person mike = new Person("Mike");
        Person dave = new Person("Dave");

        // could use addFriend(...) but hey
        dave.getFriends().add(new Friendship(dave, mike, 5));

        session.save(dave);

        assertThat(dave.getId()).isNotNull();
        assertThat(mike.getId()).isNotNull();
        assertThat(dave.getFriends().get(0).getId()).isNotNull();
    }

    @Test
    public void shouldSaveAndReloadAllSetsAllObjectIdsAndReferencesCorrectly() {

        Person mike = new Person("Mike");
        Person dave = new Person("Dave");
        dave.getFriends().add(new Friendship(dave, mike, 5));

        session.save(dave);

        Collection<Person> personList = session.loadAll(Person.class);

        int expected = 2;
        assertThat(personList.size()).isEqualTo(expected);
        for (Person person : personList) {
            if (person.getName().equals("Dave")) {
                expected--;
                assertThat(person.getFriends().get(0).getFriend().getName()).isEqualTo("Mike");
            } else if (person.getName().equals("Mike")) {
                expected--;
                assertThat(person.getFriends().get(0).getPerson().getName()).isEqualTo("Dave");
            }
        }
        assertThat(expected).isEqualTo(0);
    }

    @Test
    public void shouldSaveFromRelationshipEntitySetsAllObjectIds() {

        Person mike = new Person("Mike");
        Person dave = new Person("Dave");

        Friendship friendship = new Friendship(dave, mike, 5);
        dave.getFriends().add(friendship);

        session.save(friendship);

        assertThat(dave.getId()).isNotNull();
        assertThat(mike.getId()).isNotNull();
        assertThat(dave.getFriends().get(0).getId()).isNotNull();
    }

    @Test
    public void shouldLoadStartObjectHydratesProperly() {

        Person mike = new Person("Mike");
        Person dave = new Person("Dave");
        Friendship friendship = new Friendship(dave, mike, 5);
        dave.getFriends().add(friendship);

        session.save(dave);

        Person daveCopy = session.load(Person.class, dave.getId());
        Friendship friendshipCopy = daveCopy.getFriends().get(0);
        Person mikeCopy = friendshipCopy.getFriend();

        assertThat(daveCopy.getId()).isNotNull();
        assertThat(mikeCopy.getId()).isNotNull();
        assertThat(friendshipCopy.getId()).isNotNull();

        assertThat(daveCopy.getName()).isEqualTo("Dave");
        assertThat(mikeCopy.getName()).isEqualTo("Mike");
        assertThat(friendshipCopy.getStrength()).isEqualTo(5);
    }

    @Test
    public void shouldLoadRelationshipEntityObjectHydratesProperly() {

        Person mike = new Person("Mike");
        Person dave = new Person("Dave");
        Friendship friendship = new Friendship(dave, mike, 5);
        dave.getFriends().add(friendship);

        session.save(dave);

        Friendship friendshipCopy = session.load(Friendship.class, friendship.getId());
        Person daveCopy = friendshipCopy.getPerson();
        Person mikeCopy = friendshipCopy.getFriend();

        assertThat(daveCopy.getId()).isNotNull();
        assertThat(mikeCopy.getId()).isNotNull();
        assertThat(friendshipCopy.getId()).isNotNull();

        assertThat(daveCopy.getName()).isEqualTo("Dave");
        assertThat(mikeCopy.getName()).isEqualTo("Mike");
        assertThat(friendshipCopy.getStrength()).isEqualTo(5);
    }

    /**
     * @see DATAGRAPH-644
     */
    @Test
    public void shouldRetrieveRelationshipEntitySetPropertyCorrectly() {

        Person mike = new Person("Mike");
        Person dave = new Person("Dave");

        Set<String> hobbies = new HashSet<>();
        hobbies.add("Swimming");
        hobbies.add("Cooking");
        dave.getFriends().add(new Friendship(dave, mike, 5, hobbies));

        session.save(dave);

        assertThat(dave.getId()).isNotNull();
        assertThat(mike.getId()).isNotNull();
        assertThat(dave.getFriends().get(0).getId()).isNotNull();

        session.clear();

        mike = session.load(Person.class, mike.getId());
        assertThat(mike.getFriends().get(0).getSharedHobbies()).hasSize(2);

    }
}
