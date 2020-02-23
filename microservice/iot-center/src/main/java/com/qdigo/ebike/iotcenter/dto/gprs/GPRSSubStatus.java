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

package com.qdigo.ebike.iotcenter.dto.gprs;

import lombok.Data;

import java.io.Serializable;

@Data
public class GPRSSubStatus implements Serializable {
    private static final long serialVersionUID = -5371736380827965009L;
    //外接电源（串口通讯） bit0
    private byte communicationStatus;
    //电门锁开关  bit1
    private byte switchStatus;
    //是否锁车 bit2
    private byte LockStatus;
    // 是否有震动 bit3
    private byte shockStatus;
    //是否轮车输入 bit4
    private byte inputStatus;
    // 是否自动 bit5
    private byte autoLockStatus;
    // 是否跌倒  bit6
    private byte fallStatus;
    // 是否故障 bit7
    private byte troubleStatus;
}