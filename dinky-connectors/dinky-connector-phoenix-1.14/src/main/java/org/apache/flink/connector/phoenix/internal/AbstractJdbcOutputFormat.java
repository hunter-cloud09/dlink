/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.flink.connector.phoenix.internal;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.phoenix.internal.connection.JdbcConnectionProvider;
import org.apache.flink.util.Preconditions;

import java.io.Flushable;
import java.io.IOException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base jdbc outputFormat. */
public abstract class AbstractJdbcOutputFormat<T> extends RichOutputFormat<T> implements Flushable {

    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_FLUSH_MAX_SIZE = 5000;
    public static final long DEFAULT_FLUSH_INTERVAL_MILLS = 0L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJdbcOutputFormat.class);
    protected final JdbcConnectionProvider connectionProvider;

    public AbstractJdbcOutputFormat(JdbcConnectionProvider connectionProvider) {
        this.connectionProvider = Preconditions.checkNotNull(connectionProvider);
    }

    @Override
    public void configure(Configuration parameters) {}

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        try {
            connectionProvider.getOrEstablishConnection();
        } catch (Exception e) {
            throw new IOException("unable to open JDBC writer", e);
        }
    }

    @Override
    public void close() {
        connectionProvider.closeConnection();
    }

    @Override
    public void flush() throws IOException {}

    // @VisibleForTesting
    public Connection getConnection() {
        return connectionProvider.getConnection();
    }
}
