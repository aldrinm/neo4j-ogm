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

package org.neo4j.ogm.domain.forum.activity;

import java.util.Date;

import org.neo4j.ogm.annotation.GraphId;

/**
 * @author Vince Bickers
 */
public abstract class Activity {

    private Date date;

    @GraphId  // not strictly necessary, can always default to field id, but required to explicitly use this getter
    private Long id;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getActivityId() {
        return id;
    }

    public void setActivityId(Long id) {
        this.id = id;
    }
}
