/*
 * Copyright 2019 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.api.service.third.address;

import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.third.map.Address;
import com.qdigo.ebike.api.domain.dto.third.map.Point;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Description: 
 * date: 2019/12/27 3:17 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "map")
public interface AmapService {

    @PostMapping(ApiRoute.Third.Amap.baseStationLocation)
    Map<String, String> baseStationLocation(@RequestParam("imei") String imei, @RequestParam("lac") String lac,
                                            @RequestParam("cellid") String cellid, @RequestParam("imsi") String imsi,
                                            @RequestParam("singal") String singal);

    @PostMapping(ApiRoute.Third.Amap.getAddress)
    Address getAddress(@RequestParam("lat") double lat, @RequestParam("lng") double lng, @RequestParam("amapLoc") boolean amapLoc);

    @PostMapping(ApiRoute.Third.Amap.getIPAddress)
    JSONObject getIPAddress(@RequestParam("ip") String ip);

    @PostMapping(ApiRoute.Third.Amap.createFence)
    JSONObject createFence(@RequestParam("name") String name, @RequestBody List<Point> points, @RequestParam("desc") String desc);

    @PostMapping(ApiRoute.Third.Amap.fenceStatus)
    JSONObject fenceStatus(@RequestParam("imei") String imei, @RequestParam("lat") double lat, @RequestParam("lng") double lng);

    @PostMapping(ApiRoute.Third.Amap.deleteFence)
    boolean deleteFence(@RequestParam("gid") String gid);
}
