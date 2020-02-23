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

package com.qdigo.ebike.api.domain.dto.iot.datagram;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class PGPackage extends DatagramDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String pgImei; // imei号
    private Double pgLongitude; // gps经度
    private Double pgLatitude; // gps纬度
    private Integer pgHight; // 海拔高度
    private Integer pgSpeed; // 实际速度
    private Integer pgStar; // 卫星数量
    private Integer pgElectric; // (0:无外界电源 1:有外接电源)
    private Integer pgDoorLock; // (0:电门锁关，1:电门锁开)
    private Integer pgLocked; // (0:没锁车 1:锁车)
    private Integer pgShaked; // (0:无震动，1:震动)
    private Integer pgWheelInput;// (0:不是轮车输入模式 1:是轮车输入模式)
    private Integer pgAutoLocked; // (0:不是自动锁车 1：自动锁车)
    private Integer pgTumble; // (0:没跌倒 1:跌倒)
    private Integer pgError; // (0:无故障 1:有故障

    private String pgClient;
    private String pgServer;
}
