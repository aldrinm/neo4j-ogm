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

package org.neo4j.ogm.persistence.examples.cineasts.annotated;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;
import org.neo4j.ogm.domain.cineasts.annotated.Movie;
import org.neo4j.ogm.domain.cineasts.annotated.Rating;
import org.neo4j.ogm.metadata.MetaData;
import org.neo4j.ogm.session.Neo4jSession;
import org.neo4j.ogm.session.SessionFactory;

/**
 * @author Michal Bachman
 * @author Mark Angrish
 */
public class MovieTest {

    @Test
    public void testDeserialiseMovie() {

        MetaData metadata = new MetaData("org.neo4j.ogm.domain.cineasts.annotated");
        Neo4jSession session = new Neo4jSession(metadata, new MoviesRequest());

        Movie movie = session.load(Movie.class, UUID.fromString("38ebe777-bc85-4810-8217-096f29a361f1"), 1);

        assertEquals("Pulp Fiction", movie.getTitle());
        assertNotNull(movie.getRatings());
        assertEquals(1, movie.getRatings().size());

        Rating rating = movie.getRatings().iterator().next();

        assertEquals("Michal", rating.getUser().getName());
        assertEquals("Pulp Fiction", rating.getMovie().getTitle());
    }
}
