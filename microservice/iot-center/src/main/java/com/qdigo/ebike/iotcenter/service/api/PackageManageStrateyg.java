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

package com.qdigo.ebike.iotcenter.service.api;

import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;

/**
 * Description: 
 * date: 2020/2/22 10:45 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
public interface PackageManageStrateyg<T extends DatagramPacketBasicDto> {

    void sendMsg(T dataPackDto);

    void saveInfo(T dataPackDto);

    String PGStratrgy = "PGManageStratrgy";
    String PHStratrgy = "PHManageStratrgy";
    String PLStratrgy = "PLManageStratrgy";
    String PCStratrgy = "PCManageStratrgy";
    String MDStratrgy = "MDManageStratrgy";
    String MLStratrgy = "MLManageStratrgyv";
    String MCStratrgy = "MCManageStratrgy";
}
