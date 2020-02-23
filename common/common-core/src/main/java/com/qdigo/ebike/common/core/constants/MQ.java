/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.common.core.constants;

/**
 * Created by niezhao on 2017/4/20.
 */
public class MQ {

    public static class Topic {

        public static class Exchange {
            public static final String pg = "pg";
            public static final String ph = "ph";
            public static final String pl = "pl";
            public static final String pc = "pc";
            public static final String pg_exchange = "pg.exchange";
        }

        public static class Key {
            public static final String up_pg = "up.pg";
            public static final String up_pg_bak = "up.pg.bak";
            public static final String up_pg_biz = "up.pg.biz";

            public static final String up_ph = "up.ph";
            public static final String up_pl = "up.pl";
            public static final String up_pc = "up.pc";
            public static final String up_pc_special = "up.pc.special";
        }

    }

    public static class Direct {
        public static final String task_order_push = "task.order.push";
        public static final String task_order_deposit_push = "task.order.deposit.push";
        public static final String task_charger_bikeForPush = "task.charger.bikeForPush";
        public static final String task_ride_warn_normal = "task.ride.warn.normal";
        public static final String device_connect = "device.connect";
    }

}
