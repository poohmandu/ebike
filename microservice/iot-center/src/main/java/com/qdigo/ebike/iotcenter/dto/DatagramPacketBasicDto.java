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

package com.qdigo.ebike.iotcenter.dto;

import com.qdigo.ebike.iotcenter.netty.SocketServer;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class DatagramPacketBasicDto implements Serializable {
    private static final long serialVersionUID = 7869686439958718471L;

    private char header0;
    private char header1;
    // IMEI 号
    private int imei;

    private String server = SocketServer.NET_IP;
    private String client;

}
