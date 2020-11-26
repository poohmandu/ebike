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

package com.qdigo.ebike.controlcenter.service.inner.rent;

import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PCPackage;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PXService;
import com.qdigo.ebike.controlcenter.service.inner.rent.end.RentEndService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/6/28.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ButtonEndService {

    private final RentEndService rentEndService;
    private final PXService pxService;

    private final ApplicationContext context;

    @Transactional
    public String executeButtonEnd(PCPackage pc) throws QdigoBizException {
        return "3";
        //val imei = pc.getPcImei();
        //ResponseDTO<EndDTO> dto = rentEndService.buttonEndValidate(imei);
        //RideDto rideRecord = dto.getData().getRideDto();
        //val statusCode = dto.getStatusCode();
        //log.debug("imei:{},70命令执行后,statusCode:{},message:{}", imei, dto.getStatusCode(), dto.getMessage());
        //String mobileNo;
        //String param;
        //if (!dto.isSuccess()) {
        //    if (statusCode == 400) {
        //        mobileNo = "system";
        //    } else if (statusCode == 401) {
        //        val user = rideRecord.getUser();
        //        mobileNo = user.getMobileNo();
        //        pushService.pushNotation(user, "您不在指定还车点，还车失败\n请到还车点还车", Const.PushType.buttonEndFail, "");
        //    } else if (statusCode == 403) {//402 =>403
        //        val user = rideRecord.getUser();
        //        mobileNo = user.getMobileNo();
        //        pushService.pushNotation(user, "还车失败,可再试一次", Const.PushType.buttonEndFail, "");
        //    } else {
        //        throw new RuntimeException("未知的statusCode" + statusCode);
        //    }
        //    param = "3";
        //} else {
        //    val bikeStatus = rideRecord.getBike().getBikeStatus();
        //    val user = rideRecord.getUser();
        //    mobileNo = user.getMobileNo();
        //    EndResponse response = rentEndService.endRideRecord(dto.getData(), true);
        //    context.publishEvent(new EndBikeSuccessEvent(this, rideRecord, response));
        //
        //    //if (statusCode == 201) {
        //    //    rideRecordService.freeRide(rideRecord, bikeStatus.getLongitude(), bikeStatus.getLatitude(), true, Status.FreeActivity.xxSecondsDrive);
        //    //    bikeService.confirmBikeStatusAsync(rideRecord, ride -> pushService.pushNotation(user, "成功还车," + Const.freeSeconds + "秒内还车不收取任何费用", Const.PushType.buttonEndSuccess, ""));
        //    //    userRecordService.insertUserRecord(user, "免费骑行," + Const.freeSeconds + "秒内还车不收取任何费用");
        //    //    log.debug("用户{}完成还车,为免费订单", mobileNo);
        //    //} else if (statusCode == 200) {
        //    //    rideRecordService.finishRide(rideRecord, bikeStatus.getLongitude(), bikeStatus.getLatitude(), true);
        //    //    bikeService.confirmBikeStatusAsync(rideRecord, ride -> pushService.pushNotation(user, "成功还车,本次骑行共花费" + ride.getConsume() + "元。", Const.PushType.buttonEndSuccess, ""));
        //    //    userRecordService.insertUserRecord(user, "成功还车,共花费" + rideRecord.getConsume() + "元");
        //    //    creditService.updateCreditInfo(user, Status.CreditEvent.normalDrive.getVal(), Status.CreditEvent.normalDrive.getScore());
        //    //    log.debug("用户{}完成还车", mobileNo);
        //    //} else {
        //    //    throw new RuntimeException("未知的statusCode" + statusCode);
        //    //}
        //    param = "2";
        //}
        //val px = PXPackage.builder().mobileNo(mobileNo).pxCmd(pc.getPcCmd()).pxImei(imei).pxParam(param)
        //        .pxSequence(pc.getPcSequence()).timestamp(System.currentTimeMillis()).build();
        //log.debug("user:{},bike:{}保存PX包,特殊的命令号cmd:{},PX:{}", mobileNo, imei, pc.getPcCmd(), px);
        //pxService.insertPX(px);
        //return param;

    }

}
