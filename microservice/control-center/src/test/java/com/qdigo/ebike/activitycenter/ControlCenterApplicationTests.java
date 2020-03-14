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

package com.qdigo.ebike.activitycenter;

import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
class ControlCenterApplicationTests {

    @Resource
    private MongoTemplate mongoTemplate;
    private String[] imeiArr = {
            "86072001000009", "86072001000011", "86072001000028", "86072001000037", "86072001000043",
            "86072001000089", "860720010000114", "860720010000108", "860720010000101", "860720010000014",
            "860720010000006", "860720010000081", "860720010000050", "860720010000113", "860720010014807",
            "860720010000315", "860720010000956", "860720010001076", "860720010001036", "860720010001188",
            "860720010001125", "860720010002340", "860720010002025", "860720010001262", "860720010001419",
            "860720010001832", "860720010001593", "860720010008013", "860720010008702", "860720010004008",
            "860720010000079", "860720010000063", "860720010001818", "860720010000843", "860720010000112"};
    private String[] dateArr = {"PG20200225"};
    public final static DateFormat df = new SimpleDateFormat("HH:mm:ss");

    //@Test
    void contextLoads() {
        log.info("开始mongodb测试");
        Query query = new Query(Criteria.where("pgImei").in(imeiArr)
                .and("seconds").gte(120));
        List<String> strings = new ArrayList<>();
        Stream.of(dateArr).forEach(ds -> {
            List<PGPackage> pgPackages = mongoTemplate.find(query, PGPackage.class, ds);
            StringBuilder sb = new StringBuilder("29个设备超过2分钟断线的情况:");
            pgPackages.stream().collect(Collectors.groupingBy(o -> o.getPgImei()))
                    .forEach((s, pgArr) -> {
                        sb.append(s).append("断线").append(pgArr.size()).append("次。");
                        pgArr.forEach(pgPackage -> {
                            sb.append("时间").append(df.format(new Date(pgPackage.getTimestamp())))
                                    .append("时长").append(pgPackage.getSeconds()).append("秒\n");
                        });
                    });
            sb.append("\n");
            strings.add(sb.toString());
        });
        log.info("输出结果:", strings);
    }

}
