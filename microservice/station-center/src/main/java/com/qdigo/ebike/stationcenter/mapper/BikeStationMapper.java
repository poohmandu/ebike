/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.stationcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: 
 * date: 2020/1/2 3:24 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
public interface BikeStationMapper extends BaseMapper<BikeStation> {


    List<BikeStation> findByLocationOrdered(@Param("minLat") double minLat, @Param("maxLat") double maxLat,
                                            @Param("minLng") double minLng, @Param("maxLng") double maxLng,
                                            @Param("lat") double lat, @Param("lng") double lng,
                                            @Param("agentIds") List<Long> agentIds,
                                            @Param("lim") int lim);

    List<BikeStation> findByLocation(@Param("minLat") double minLat, @Param("maxLat") double maxLat,
                                     @Param("minLng") double minLng, @Param("maxLng") double maxLng,
                                     @Param("agentIds") List<Long> agentIds);

    //mybatis-plus 分页查询方式二
    List<BikeStation> findOrderByLocation(@Param("lat") double lat, @Param("lng") double lng, @Param("agentIds") List<Long> agentIds);

}
