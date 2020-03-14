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

package com.qdigo.ebike.controlcenter.service.inner.command.sms;

import com.qdigo.ebike.api.domain.dto.bike.SimDto;
import com.qdigo.ebike.api.domain.dto.control.Location;

import java.util.Optional;
import java.util.concurrent.Future;

public interface IDevSMSService {

    boolean smsOpen(String imei, String mobileNo, SimDto sim);

    boolean smsClose(String imei, String mobileNo, SimDto sim);

    boolean smsSetImei(String imei, String newImei, String mobileNo, SimDto sim);

    boolean smsSetHost(String imei, String domain, int port, String mobileNo, SimDto sim);

    Optional<Location> smsLoc(String imei, String mobileNo, SimDto sim);

    Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply);

    Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply, boolean fast);

    String huahong = "huahongInnerService";
    String dahan = "dahanInnerService";
    String youyun = "youyunInnerService";

}
