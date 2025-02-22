// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import org.junit.jupiter.api.Assertions;

suite("docs/data-operate/update/update-of-unique-model.md") {
    try {
        multi_sql """
            DROP TABLE IF EXISTS order_tbl;
            CREATE TABLE IF NOT EXISTS order_tbl
            (
                `order_id` BIGINT NOT NULL,
                `order_amount` DECIMAL(27, 9) NOT NULL,
                `order_status` VARCHAR(65533)
            )
            UNIQUE KEY(`order_id`)
            DISTRIBUTED BY HASH(`order_id`) BUCKETS 1
            PROPERTIES (
            "replication_allocation" = "tag.location.default: 1"
            );
            INSERT INTO order_tbl(order_id, order_amount, order_status) VALUES
                                  (1       , 100         , 'Pending'   );
        """
        cmd """curl --location-trusted -u ${context.config.jdbcUser}:${context.config.jdbcPassword} -H "partial_columns:true" -H "column_separator:," -H "columns:order_id,order_status" -T ${context.file.parent}/update.csv http://${context.config.feHttpAddress}/api/${curDbName}/order_tbl/_stream_load"""
        multi_sql """
            set enable_unique_key_partial_update=true;
            INSERT INTO order_tbl (order_id, order_status) values (1,'To be shipped');
        """
        qt_sql "SELECT * FROM order_tbl"
    } catch (Throwable t) {
        Assertions.fail("examples in docs/data-operate/update/update-of-unique-model.md failed to exec, please fix it", t)
    }
}
