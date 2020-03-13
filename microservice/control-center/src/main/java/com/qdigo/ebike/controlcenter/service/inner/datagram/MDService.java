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

package com.qdigo.ebike.controlcenter.service.inner.datagram;

import org.springframework.stereotype.Service;

/**
 * Created by niezhao on 2016/11/30.
 */
@Service
//rebuild 同理充电桩都先省略了
public class MDService {
    //
    //private final Logger logger = LoggerFactory.getLogger(MDService.class);
    //
    //@Inject
    //private BikeRepository bikeRepository;
    //@Inject
    //private ChargerRepository chargerRepository;
    //@Inject
    //private ChargerPortRepository portRepository;
    //@Inject
    //private RedisTemplate<String, String> redisTemplate;
    //@Inject
    //private RabbitTemplate rabbitTemplate;
    //@Inject
    //private RideRecordDao rideRecordDao;
    //
    ////@Async
    //@Transactional
    //public void updateTable(MDPackage md) {
    //
    //    String imei = md.getMdImei();
    //
    //    //对充电桩和充电口的相关信息进行更新
    //    Charger charger = chargerRepository.findByChargerImei(imei)
    //        .orElseThrow(() -> new NullPointerException("没找到指定充电桩"));
    //
    //    int portNo = md.getMdPortNumber();
    //    List<ChargerPort> chargerPorts = charger.getChargerPortList();
    //    ChargerPort chargerPort = chargerPorts.parallelStream()
    //        .filter(port -> port.getChargerPortNo() == portNo)
    //        .filter(Objects::nonNull)
    //        .findAny()
    //        .orElseGet(() -> {
    //            ChargerPort p = new ChargerPort();
    //            p.setChargerPortNo(portNo);
    //            p.setCharger(charger);
    //            return p;
    //        });
    //
    //    //充电状态:0不在充电 1:恒流充电
    //    if (md.getMdState() == 0) {
    //        chargerPort.setStatus("可使用");
    //        //0=没有故障，1=过温保护；2=过高压保护；3=欠压保护；4=风扇故障；5=热失控；6=与电动车通讯错误；
    //    } else if (md.getMdChargeError() == 0) {
    //        chargerPort.setStatus("充电中");
    //    } else {
    //        chargerPort.setStatus("已故障");
    //    }
    //    portRepository.save(chargerPort);
    //    logger.debug("对{}号充电口,进行状态更新", chargerPort.getChargerPortNo());
    //
    //    //对充电桩信息更新
    //    int usedPort = (int) chargerPorts.parallelStream()
    //        .filter(port -> "充电中".equals(port.getStatus().trim()))
    //        .count();
    //    charger.setUsedPortNumber(usedPort);
    //    int unused = chargerPorts.size() - usedPort;
    //    charger.setPortNumber(unused);
    //    chargerRepository.save(charger);
    //    logger.debug("充电桩:{}个口可用,{}个口不可用", unused, usedPort);
    //
    //    //充电桩发来的车辆imei号,检查
    //    String bikeImei = ConfigConstants.imei.getConstant() + md.getMdPortBikeNumber();
    //    Bike bike = bikeRepository.findByImeiId(bikeImei).orElse(null);
    //
    //    String key = Keys.up_MD_chk.getKey(FormatUtil.getCurDate());
    //
    //    //正在充电的电动车存进redis的hashkey
    //    Map<String, String> map = new HashMap<>();
    //    HashOperations<String, String, String> hash = redisTemplate.opsForHash();
    //    String currentTime = FormatUtil.getCurTime();
    //    if (!redisTemplate.hasKey(key)) {
    //        hash.put(key, "create_date", currentTime);
    //        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    //    } else {
    //        String hash_key = "bike:" + bikeImei;
    //        hash.put(key, hash_key, currentTime);
    //        logger.debug("redis存储的hash_key为{}", hash_key);
    //    }
    //    RideOrder rideOrder = rideRecordDao.findByRidingBike(bike);
    //    if (rideOrder != null && rideOrder.getRideStatus() == Status.RideStatus.running.getVal()) {
    //        logger.debug("{}发送插充电桩自动还车消息{}", imei, bikeImei);
    //        rabbitTemplate.convertAndSend(MQ.Direct.task_charger_bikeForPush, bikeImei);
    //    }
    //}
    //
    //
    ////得到所有10s内正在充电的电动车列表
    //public List<Bike> getBikesForCharging() {
    //    List<Bike> bikes = new ArrayList<>();
    //    Date date = new Date();
    //    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //    String key = "up_MD_check_" + new SimpleDateFormat("yyyyMMdd").format(date);
    //    HashOperations<String, String, String> hash = redisTemplate.opsForHash();
    //    Map<String, String> map = hash.entries(key);
    //    //logger.debug("开始遍历redis里charge的bike的imei号");
    //    map.forEach((k, v) -> {
    //        if (k.startsWith("bike:")) {
    //            Date preDate;
    //            try {
    //                preDate = dateFormat.parse(v);
    //            } catch (ParseException e) {
    //                logger.debug("时间解析错误:" + e.getMessage());
    //                return;
    //            }
    //            int seconds = (int) Duration.between(preDate.toInstant(), date.toInstant()).getSeconds();
    //            if (seconds < 10 && k.length() == 20) {
    //                String imei = k.substring(k.indexOf(":") + 1);
    //                logger.debug("正在充电的车辆的imei号为" + imei);
    //                bikeRepository.findTopByImeiId(imei)
    //                    .ifPresent(bikes::add);
    //            }
    //        }
    //    });
    //    logger.debug("一共有{}辆车在充电", bikes.size());
    //    return bikes;
    //}

}
