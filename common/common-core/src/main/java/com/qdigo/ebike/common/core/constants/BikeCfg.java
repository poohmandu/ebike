/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
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
 * Created by niezhao on 2017/9/21.
 */
public final class BikeCfg {

    /*
    public static class A {
        public static final int max = 5200; //4800
        public static final int min = 4300; //4300
        public static final int enough = 5000; //70%~80%
        public static final int low = 4500;
        public static final double kilometer = 40; //50,预留10公里
    }

    //铅酸电池
    public static class B {
        public static final int enough = 5950; //50%
        public static final int max = 6500;
        public static final int min = 5400;
        public static final int low = 5600;
        public static final double kilometer = 50;
    }

    //开封自改车
    public static class C {
        public static final int max = 5100;
        public static final int min = 4250;
        public static final int enough = 4930;
        public static final int low = 4420;
        public static final double kilometer = 30;
    }

    //樱花脚踏车
    public static class D {
        public static final int enough = 3625;
        public static final int max = 3850; //实测3950
        public static final int min = 3100; //实测3050
        public static final int low = 3250;
        public static final double kilometer = 40;
    }

    //开封E形车
    public static class E {
        public static final int enough = 6600;
        public static final int max = 6900; //实测3950
        public static final int min = 5800; //实测3050
        public static final int low = 5900;
        public static final double kilometer = 38;
    }*/


    public enum OperationType {
        //景区
        scenic,
        //校园
        school,
        //城市
        city,
        //社区
        community,
        //外卖
        takeaway
    }

    public enum LocationType {
        gps, scan, end //end :还车时pg包断了,谨慎
    }

    public enum OpsType {
        on, off, lock, unlock, end
    }

}
