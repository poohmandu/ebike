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

package com.qdigo.ebike.bike.service;

import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.mapper.BikeMapper;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class BikeDaoService {

    @Resource
    private BikeMapper bikeMapper;

    public List<Bike> findOnlineByLocation(double lat, double lng, double meter, List<Long> agentIds) {
        GeoUtil.Around around = GeoUtil.getAround(lng, lat, meter);
        return bikeMapper.findOnlineByLocation(around.minY, around.maxY, around.minX, around.maxX, agentIds);
    }


}
