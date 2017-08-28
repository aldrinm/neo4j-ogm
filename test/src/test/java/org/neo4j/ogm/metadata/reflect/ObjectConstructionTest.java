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

package org.neo4j.ogm.metadata.reflect;

import java.lang.reflect.Constructor;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Vince Bickers
 */
public class ObjectConstructionTest {

    @Test
    public void shouldCreateReflectionInstance() {

        String fqn = "org.neo4j.ogm.metadata.reflect.ObjectConstructionTest$A";
        try {
            Class<?> loadedClass = Class.forName(fqn);
            Constructor<?> defaultConstructor = loadedClass.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            assertThat(defaultConstructor.newInstance()).isNotNull();
        } catch (SecurityException | IllegalArgumentException | ReflectiveOperationException e) {
            fail("Unable to instantiate class: " + fqn + e.getLocalizedMessage());
        }
    }

    public static class A extends E {

    }

    public static class E {

        Long id;
    }
}
