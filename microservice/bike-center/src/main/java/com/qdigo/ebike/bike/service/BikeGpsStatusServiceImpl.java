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
import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PGPackage;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PHPackage;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PLPackage;
import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.bike.domain.entity.BikeGpsStatus;
import com.qdigo.ebike.bike.repository.BikeGpsStatusRepository;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/12 5:32 PM
 * @author niezhao
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeGpsStatusServiceImpl implements BikeGpsStatusService {

    private final BikeGpsStatusRepository gpsStatusRepository;

    @Override
    public BikeGpsStatusDto findByImei(String imei) {
        BikeGpsStatus bikeGpsStatus = gpsStatusRepository.findByImei(imei).orElse(null);
        return ConvertUtil.to(bikeGpsStatus, BikeGpsStatusDto.class);
    }

    @Override
    @Transactional
    public void updatePg(PGPackage PG) {
        BikeGpsStatus one = gpsStatusRepository.findById(PG.getPgImei()).orElse(null);
        if (one == null) {
            one = new BikeGpsStatus();
        }
        gpsStatusRepository.save(one.setAutoLocked(PG.getPgAutoLocked())
                .setDoorLock(PG.getPgDoorLock())
                .setElectric(PG.getPgElectric())
                .setError(PG.getPgError())
                .setHight(PG.getPgHight())
                .setLocked(PG.getPgLocked())
                .setPgTime(FormatUtil.getCurTime())
                .setSpeed(PG.getPgSpeed())
                .setStar(PG.getPgStar())
                .setTumble(PG.getPgTumble())
                .setWheelInput(PG.getPgWheelInput())
                .setImei(PG.getPgImei())
                .setShaked(PG.getPgShaked()));
    }

    @Override
    @Transactional
    public void updatePh(PHPackage PH) {
        BikeGpsStatus one = gpsStatusRepository.findById(PH.getPhImei()).orElse(null);
        if (one == null) {
            one = new BikeGpsStatus();
        }
        gpsStatusRepository.save(one.setAutoLocked(PH.getPhAutoLocked())
                .setBatteryVoltage(PH.getPhBatteryVoltage())
                .setBrakeError(PH.getPhBrakeErroe())
                .setControlError(PH.getPhControlError())
                .setDoorLock(PH.getPhDoorLock())
                .setElectric(PH.getPhElectric())
                .setError(PH.getPhError())
                .setHandleBarError(PH.getPhHandleBarError())
                .setImei(PH.getPhImei())
                .setImsi(PH.getPhImsi())
                .setLocked(PH.getPhLocked())
                .setMachineError(PH.getPhMachineError())
                .setPhTime(FormatUtil.getCurTime())
                .setPowerVoltage(PH.getPhPowerVoltage())
                .setSensitivity(PH.getPhSentity())
                .setShaked(PH.getPhShaked())
                .setSoc(PH.getPhSoc())
                .setTumble(PH.getPhTumble())
                .setWheelInput(PH.getPhWheelInput()));
    }

    @Override
    @Transactional
    public void updatePl(PLPackage PL) {
        BikeGpsStatus one = gpsStatusRepository.findById(PL.getPlImei()).orElse(null);
        if (one == null) {
            one = new BikeGpsStatus();
        }
        gpsStatusRepository.save(one.setWheelInput(PL.getPlWheelInput())
                .setTumble(PL.getPlTumble())
                .setShaked(PL.getPlShaked())
                .setAutoLocked(PL.getPlAutoLocked())
                .setCellid(PL.getPlCellid())
                .setDoorLock(PL.getPlDoorLock())
                .setElectric(PL.getPlElectric())
                .setError(PL.getPlError())
                .setLac(PL.getPlLac())
                .setLocked(PL.getPlLocked())
                .setPlTime(FormatUtil.getCurTime())
                .setSingal(PL.getPlSingal())
                .setImei(PL.getPlImei()));
    }
}
