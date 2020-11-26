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

package com.qdigo.ebike.usercenter.service.inner;

import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.domain.dto.third.map.Address;
import com.qdigo.ebike.api.service.third.address.AmapService;
import com.qdigo.ebike.usercenter.domain.entity.UserAddress;
import com.qdigo.ebike.usercenter.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Description:
 * date: 2020/1/1 1:15 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserAddressService {

    private final AmapService amapService;
    private final UserAddressRepository userAddressRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<UserAddress> getUserAddress(double lat, double lng, String mobileNo, String remoteIp) {
        boolean locValid = lat > 3 && lat < 55 & lng > 60 && lng < 150; // lat:3~55,lng:60~150
        if (!locValid) {
            // 1、城市为空  2、城市变化
            JSONObject json = amapService.getIPAddress(remoteIp);
            String reason;
            if (json != null) {
                try {
                    String city = json.getString("city");
                    String userCity = this.getUserCity(mobileNo);
                    if (StringUtils.isEmpty(city) || "[]".equals(city)) {
                        //局域网网段内IP或者非法IP或国外IP
                        reason = "IP无法获取城市,City=" + city;
                    } else if (StringUtils.isEmpty(userCity) || !userCity.equals(city)) {
                        String rectangle = json.getString("rectangle");
                        String[] split = StringUtils.split(rectangle, ';');
                        String[] sp1 = StringUtils.split(split[0], ',');
                        String[] sp2 = StringUtils.split(split[1], ',');
                        double lng1 = Double.parseDouble(sp1[0]);
                        double lat1 = Double.parseDouble(sp1[1]);
                        double lng2 = Double.parseDouble(sp2[0]);
                        double lat2 = Double.parseDouble(sp2[1]);
                        lng = (lng1 + lng2) / 2;
                        lat = (lat1 + lat2) / 2;
                        return this.getUserAddress(lat, lng, mobileNo, UserAddress.LocType.ip);
                    } else {
                        reason = "原城市:" + userCity + ",IP城市:" + city;
                    }
                } catch (Exception e) {
                    log.error("获取用户userAddress失败:", e);
                    reason = "获取用户userAddress失败:" + e.getMessage();
                }
            } else {
                reason = "获取的json为空";
            }
            log.debug(mobileNo + "通过IP获取用户address失败原因:" + reason);
            return Optional.empty();
        } else {
            return this.getUserAddress(lat, lng, mobileNo, UserAddress.LocType.loc);
        }
    }

    private Optional<UserAddress> getUserAddress(double lat, double lng, String mobileNo, UserAddress.LocType locType) {
        final Address address = amapService.getAddress(lat, lng, true);
        if (address == null) {
            return Optional.empty();
        } else {
            //已转为gps坐标
            log.debug("{}获取用户地址为{},通过{}方式", mobileNo, address, locType);
            final UserAddress us = new UserAddress();
            us.setAdCode(address.getAdCode());
            us.setAddress(address.getAddress());
            us.setCity(address.getCity());
            us.setCityCode(address.getCityCode());
            us.setDistrict(address.getDistrict());
            us.setLatitude(address.getLatitude());
            us.setLongitude(address.getLongitude());
            us.setProvince(address.getProvince());
            us.setMobileNo(mobileNo);
            us.setLocType(locType);
            userAddressRepository.save(us);
            return Optional.of(us);
        }
    }

    public String getUserCity(String mobileNo) {
        val userAddress = userAddressRepository.findById(mobileNo).orElse(null);
        return this.getUserCity(userAddress);
    }

    private String getUserCity(UserAddress userAddress) {
        if (userAddress == null) {
            return "";
        } else {
            return getCity(userAddress);
        }
    }

    private String getCity(Address address) {
        val city = address.getCity();
        if (city != null && !city.isEmpty() && !city.equals("[]")) {
            return city;
        } else {
            val province = address.getProvince();
            if (province != null && !province.isEmpty() && !province.equals("[]")) {
                return province;
            } else {
                return "";
            }
        }
    }

}


