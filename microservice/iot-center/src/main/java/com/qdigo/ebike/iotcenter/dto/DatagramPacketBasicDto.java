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

import java.io.Serializable;


public class DatagramPacketBasicDto implements Serializable {

    private static final long serialVersionUID = 7869686439958718471L;
    private char header0;
    private char header1;
    // IMEI 号
    private int imei;

    private String server = String.format("%s:%d", SocketServer.NET_IP, SocketServer.PORT);
    private String client;

    public String getServer() {
        return server;
    }

    public DatagramPacketBasicDto setServer(String server) {
        this.server = server;
        return this;
    }

    public String getClient() {
        return client;
    }

    public DatagramPacketBasicDto setClient(String client) {
        this.client = client;
        return this;
    }

    public char getHeader0() {
        return header0;
    }

    public void setHeader0(char header0) {
        this.header0 = header0;
    }

    public char getHeader1() {
        return header1;
    }

    public void setHeader1(char header1) {
        this.header1 = header1;
    }

    public int getImei() {
        return imei;
    }

    public void setImei(int imei) {
        this.imei = imei;
    }

}
