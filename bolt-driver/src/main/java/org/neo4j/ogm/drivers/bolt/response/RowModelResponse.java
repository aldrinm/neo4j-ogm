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
package org.neo4j.ogm.drivers.bolt.response;

import java.util.Arrays;
import java.util.Map;

import org.neo4j.driver.v1.StatementResult;

import org.neo4j.ogm.model.RowModel;
import org.neo4j.ogm.result.adapter.ResultAdapter;
import org.neo4j.ogm.transaction.TransactionManager;

/**
 * @author Luanne Misquitta
 */
public class RowModelResponse extends BoltResponse<RowModel> {

    private final ResultAdapter<Map<String, Object>, RowModel> adapter = new BoltRowModelAdapter();

    public RowModelResponse(StatementResult result, TransactionManager transactionManager) {
        super(result, transactionManager);
        ((BoltRowModelAdapter) adapter).setColumns(Arrays.asList(columns()));
    }

    @Override
    public RowModel fetchNext() {
        if (result.hasNext()) {
            return adapter.adapt(result.next().asMap());
        }
        return null;
    }
}
