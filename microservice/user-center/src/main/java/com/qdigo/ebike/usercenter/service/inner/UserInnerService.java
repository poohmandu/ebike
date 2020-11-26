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

import com.google.common.collect.Lists;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.agent.AgentDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeLoc;
import com.qdigo.ebike.api.domain.dto.station.StationDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.agent.AgentService;
import com.qdigo.ebike.api.service.bike.BikeLocService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.api.service.station.StationService;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import com.qdigo.ebike.commonaop.annotations.ThreadCache;
import com.qdigo.ebike.usercenter.domain.entity.*;
import com.qdigo.ebike.usercenter.domain.vo.UserResponse;
import com.qdigo.ebike.usercenter.repository.UserAddressRepository;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.repository.UserWxOpenInfoRepository;
import com.qdigo.ebike.usercenter.repository.UserZfbOpenInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2019/12/26 3:37 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserInnerService {

    private final UserRepository userRepository;
    private final BikeLocService bikeLocService;
    private final UserAddressService userAddressService;
    private final UserAddressRepository userAddressRepository;
    private final AgentConfigService agentConfigService;
    private final OrderLongRentService orderLongRentService;
    private final WxliteService wxliteService;
    private final UserZfbOpenInfoRepository userZfbOpenInfoRepository;
    private final BikeService bikeService;
    private final AgentService agentService;
    private final StationService stationService;
    private final UserWxOpenInfoRepository userWxOpenInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;
    @Resource
    private UserStatusInnerService userStatusInnerService;

    @Transactional
    public User createUser(String mobileNo) {
        log.debug("开始创建新增user:{},userCredit,userCreditRecord", mobileNo);
        User u = new User();
        //第一次登录,注册
        final UserCredit userCredit;
        final UserCreditRecord userCreditRecord;
        u.setMobileNo(mobileNo)
                .setAccount(new UserAccount()
                        .setUser(u))
                .setUserCredit(userCredit = new UserCredit()
                        .setUser(u));

        u.getUserCredit().getCreditRecords().add(userCreditRecord = new UserCreditRecord()
                .setEventInfo(Status.CreditEvent.login.getVal())
                .setScoreChange(Status.CreditEvent.login.getScore())
                .setEventTime(new Date())
                .setUserCredit(userCredit));
        u.getUserCredit().setScore(userCredit.getScore() + userCreditRecord.getScoreChange());
        log.debug("创建新增user:{},userCredit,userCreditRecord", mobileNo);
        return userRepository.save(u);
    }

    private Long getAgentByLocation(String mobileNo, String city) {
        if (StringUtils.isEmpty(city)) {
            log.debug("{}用户定位地址尚未上传:{}", mobileNo, city);
            return null;
        }
        List<AgentDto> agentDtos = agentService.findByCity(city);
        if (agentDtos.size() == 0) {
            log.debug("{}用户所在城市无代理商:{}", city);
            return null;
        } else if (agentDtos.size() == 1) {
            log.debug("{}获得代理商关联通过:地理位置同城市", mobileNo);
            return agentDtos.get(0).getAgentId();
        } else {
            UserAddress userAddress = userAddressRepository.findById((mobileNo)).orElse(null);
            if (userAddress == null) {
                log.debug("{}用户定位尚未上传", mobileNo);
                return null;
            } else {
                //GPS坐标系
                double latitude = userAddress.getLatitude();
                double longitude = userAddress.getLongitude();
                StationDto nearestStation = stationService.getNearestStation(longitude, latitude, 20);
                if (nearestStation == null) {
                    log.debug("{}用户20公里附近无还车点", mobileNo);
                    return null;
                } else {
                    log.debug("{}获得代理商关联通过:地理位置最近的还车点{}", mobileNo, nearestStation.getStationName());
                    return nearestStation.getAgentId();
                }
            }
        }
    }

    // agent与user关联只基于地理位置
    //当一个城市有多个代理商,根据
    @ThreadCache(key = "mobileNo")
    public Long getAgentId(final String mobileNo) {
        if (StringUtils.isBlank(mobileNo)) {
            return null;
        }
        val user = userRepository.findOneByMobileNo(mobileNo).orElse(null);
        if (user == null) {
            log.debug("【{}】用户未被创建", mobileNo);
            return null;
        }

        BikeLoc bikeLoc = bikeLocService.findLastScanLoc(mobileNo);
        if (bikeLoc != null && bikeLoc.getImei() != null && !bikeLoc.getImei().isEmpty()) {
            BikeDto bikeDto = bikeService.findByImei(bikeLoc.getImei());
            if (bikeDto != null) {
                Long agentId = bikeDto.getAgentId();
                this.updateUserAgent(user, agentId);
                log.debug("{}获得代理商关联通过:最近扫码的车辆{}", mobileNo, bikeDto.getImeiId());
                return agentId;
            }
        }
        val userCity = userAddressService.getUserCity(mobileNo);

        //重新根据地理位置获取agentId
        Long agentId = this.getAgentByLocation(mobileNo, userCity);
        this.updateUserAgent(user, agentId);
        return agentId;
    }

    private void updateUserAgent(User user, Long agentId) {
        Long userAgentId = user.getAgentId();
        if (agentId == null) {
            return;
        }
        if (userAgentId != null && Objects.equals(userAgentId, agentId)) {
            return;
        }
        user.setAgentId(agentId);
        userRepository.save(user);
    }

    public UserResponse getUserResponse(User user) {
        val mobileNo = user.getMobileNo();

        String studentAuth = userStatusInnerService.getUserStudentAuthStatus(mobileNo);
        String userCity = userAddressService.getUserCity(mobileNo);
        Long agentId = this.getAgentId(mobileNo);
        boolean hasLongRent = orderLongRentService.hasLongRent(user.getUserId());
        val openInfo = this.getOpenInfo(user);

        AgentCfg config = agentConfigService.getAgentConfig(agentId);

        return UserResponse.build(user, studentAuth, userCity, agentId, hasLongRent, config, openInfo);
    }

    public List<UserResponse.OpenInfo> getOpenInfo(User user) {
        val deviceId = user.getDeviceId();
        if (wxliteService.isWxlite(deviceId)) {
            return Lists.newArrayList(UserResponse.OpenInfo.builder().openId(user.getWxliteOpenId())
                    .appId(ConfigConstants.wxlite_appId.getConstant()).build());
        } else if (Const.zfblite.equals(deviceId)) {
            return userZfbOpenInfoRepository.findByUserId(user.getUserId()).stream()
                    .map(info -> UserResponse.OpenInfo.builder()
                            .openId(info.getOpenId()).appId(info.getAppId())
                            .build()).collect(Collectors.toList());
        } else {
            return Lists.newArrayList();
        }
    }

    @ThreadCache(key = "userId")
    public String getCurWxOpenId(User user) {
        String appId = wxliteService.getAppId(user.getDeviceId());
        List<UserWxOpenInfo> infoList = userWxOpenInfoRepository.findByUserIdAndAppId(user.getUserId(), appId);
        String openId;
        if (infoList.size() == 1) {
            openId = infoList.get(0).getOpenId();
        } else {
            infoList.stream().map(UserWxOpenInfo::getOpenId).reduce((s0, s1) -> s0 + "】【" + s1).ifPresent(s ->
                    log.debug("【id:{}】用户存在多个账号为【{}】", user.getUserId(), s));
            openId = user.getWxliteOpenId();
            log.debug("【id:{},openId:{}】用户存在多个账号情况,infoList:{}", user.getUserId(), openId, infoList.size());
        }
        return openId;
    }


    @Transactional
    public void loginUser(User user, double lat, double lng, String ip, String countryCode, String deviceId, String imei) {
        val mobileNo = user.getMobileNo();
        //保存登录前扫码的IMEI
        if (StringUtils.isNotEmpty(imei)) {
            log.debug("{}登录时保存扫码到的车辆{}", mobileNo, imei);
            bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.scanImei, lat, lng);
        } else log.debug("{}登录时未扫码车辆", mobileNo);

        userAddressService.getUserAddress(lat, lng, mobileNo, ip);//独立事务
        Long agentId = this.getAgentId(mobileNo);
        log.debug("{}登录时绑定的代理商ID为:{}", mobileNo, agentId);
        AgentCfg config = agentConfigService.getAgentConfig(agentId);
        user.getAccount().setRequireDeposit(config.getRequireDeposit());

        // 在redis中更新accessToken 翻入缓冲中
        String accessToken = SecurityUtil.generateAccessToken();
        String key = Keys.getKey(Keys.AccessToken, mobileNo);
        redisTemplate.opsForValue().set(key, accessToken, Long.parseLong(ConfigConstants.validity_period.getConstant()), TimeUnit.DAYS);
        log.debug("redis里用户{}的accessToken为{}", mobileNo, redisTemplate.opsForValue().get(key));

        // 更新user信息
        user.setAccessToken(accessToken)
                .setCountryCode(countryCode)
                .setDeviceId(deviceId)
                .setActive(true);
        userRepository.save(user);
    }

}
