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

package com.qdigo.ebike.api.service.third.address;

import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.ApiRoute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 
 * date: 2020/1/18 12:11 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "bd-map")
public interface BdMapService {

    @PostMapping(ApiRoute.Third.BdMap.addEntity)
    JSONObject addEntity(@RequestParam("entityName") String entityName);

    @PostMapping(ApiRoute.Third.BdMap.updateEntity)
    JSONObject updateEntity(@RequestParam("entityName") String entityName, @RequestParam("entityDesc") String entityDesc);

    @PostMapping(ApiRoute.Third.BdMap.addPoints)
    JSONObject addPoints(@RequestBody List<Map> points);

    @PostMapping(ApiRoute.Third.BdMap.addPoint)
    JSONObject addPoint(@RequestParam("entityName") String entityName, @RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude);

    @PostMapping(ApiRoute.Third.BdMap.getTrack)
    JSONObject getTrack(@RequestParam("entityName") String entityName, @RequestParam("start") Date start, @RequestParam("end") Date end);
}
