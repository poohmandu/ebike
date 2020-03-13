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

package com.qdigo.ebike.controlcenter.domain.dto;

/**
 * description: 
 *
 * date: 2020/3/6 11:06 AM
 * @author niezhao
 */
public class EBikeIdentifyForm {

    private String imeiIdOrDeviceId;

    public String getImeiIdOrDeviceId() {
        return imeiIdOrDeviceId;
    }

    public void setImeiIdOrDeviceId(String imeiIdOrDeviceId) {
        this.imeiIdOrDeviceId = imeiIdOrDeviceId;
    }

}
