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

package com.qdigo.ebike.api.service.bike;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.common.core.constants.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 包括业务状态和设备状态
 * date: 2020/2/23 5:59 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "bike-center", contextId = "bike-status")
public interface BikeStatusService {

    @PostMapping(ApiRoute.BikeCenter.BikeStatus.findStatusByBikeIId)
    BikeStatusDto findStatusByBikeIId(@RequestParam("bikeId") Long bikeId);

    @PostMapping(ApiRoute.BikeCenter.BikeStatus.findByImei)
    BikeStatusDto findByImei(@RequestParam("imei") String imei);

    @PostMapping(ApiRoute.BikeCenter.BikeStatus.update)
    void update(@RequestBody BikeStatusDto bikeStatusDto);


    default void setActualStatus(BikeStatusDto bikeStatus, String actualStatus) {
        if (Status.BikeActualStatus.ok.getVal().equals(actualStatus)) {
            bikeStatus.setActualStatus(actualStatus);
        } else {
            if (Status.BikeActualStatus.ok.getVal().equals(bikeStatus.getActualStatus())) {
                bikeStatus.setActualStatus(actualStatus);
            } else {
                if (!StringUtils.contains(bikeStatus.getActualStatus(), actualStatus)) {
                    bikeStatus.setActualStatus(bikeStatus.getActualStatus() + "," + actualStatus);
                }
            }
        }
    }

    default void removeActualStatus(BikeStatusDto bikeStatus, String actualStatus) {
        if (!StringUtils.contains(bikeStatus.getActualStatus(), ",")) {
            if (StringUtils.contains(bikeStatus.getActualStatus(), actualStatus)) {
                bikeStatus.setActualStatus(Status.BikeActualStatus.ok.getVal());
            } else {
                bikeStatus.setActualStatus(bikeStatus.getActualStatus());
            }
        } else {
            if (StringUtils.contains(bikeStatus.getActualStatus(), "," + actualStatus)) {
                bikeStatus.setActualStatus(StringUtils.remove(bikeStatus.getActualStatus(), "," + actualStatus));
            } else {
                bikeStatus.setActualStatus(StringUtils.remove(bikeStatus.getActualStatus(), actualStatus + ","));
            }
        }
    }

}
