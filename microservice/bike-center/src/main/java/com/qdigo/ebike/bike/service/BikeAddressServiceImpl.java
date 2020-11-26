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

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.third.map.Address;
import com.qdigo.ebike.api.service.bike.BikeAddressService;
import com.qdigo.ebike.api.service.third.address.AmapService;
import com.qdigo.ebike.bike.domain.entity.BikeAddress;
import com.qdigo.ebike.bike.repository.BikeAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/12 9:55 AM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeAddressServiceImpl implements BikeAddressService {

    private final AmapService amapService;
    private final BikeAddressRepository bikeAddressRepository;

    @Override
    @Transactional
    public BikeAddressDto updateBikeAddress(double lat, double lng, String imei) {
        final Address ad = amapService.getAddress(lat, lng, false);
        if (ad == null) {
            return null;
        } else {
            final BikeAddress bk = new BikeAddress();
            bk.setAdCode(ad.getAdCode());
            bk.setImei(imei);
            bk.setAddress(ad.getAddress());
            bk.setCity(ad.getCity());
            bk.setCityCode(ad.getCityCode());
            bk.setDistrict(ad.getDistrict());
            bk.setLatitude(ad.getLatitude());
            bk.setLongitude(ad.getLongitude());
            bk.setProvince(ad.getProvince());
            bikeAddressRepository.save(bk);
            return new BikeAddressDto().setAdCode(bk.getAdCode()).setAddress(bk.getAddress()).setCity(bk.getCity())
                    .setCityCode(bk.getCityCode()).setDistrict(bk.getDistrict()).setImei(bk.getImei())
                    .setLatitude(bk.getLatitude()).setLongitude(bk.getLongitude()).setProvince(bk.getProvince());
        }
    }
}
