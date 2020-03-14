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

package com.qdigo.ebike.api;


/**
 * Description:
 * date: 2019/12/11 3:19 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
public interface ApiRoute {
    String api = "/v1.0/api";

    interface UserCenter {
        String userCenter = api + "/user-center";

        interface User {
            String user = userCenter + "/user";

            String findByMobileNo = user + "/findByMobileNo";
            String findById = user + "/findById";
            String getOpenInfo = user + "/getOpenInfo";
        }

        interface UserStatus {
            String userStatus = userCenter + "/userStatus";

            String getUserWxscoreEnable = userStatus + "/getUserWxscoreEnable";
        }

        interface UserWx {
            String userWx = userCenter + "/userWx";

            String findByWxId = userWx + "/findByWxId";
            String saveUserWxOpenInfo = userWx + "/saveUserWxOpenInfo";
            String updateUserWxliteInfo = userWx + "/updateUserWxliteInfo";
            String findByMobileNo = userWx + "/findByMobileNo";
        }

        interface UserAccount {
            String userAccount = userCenter + "/userAccount";

            String findByMobileNo = userAccount + "/findByMobileNo";
            String findByUserId = userAccount + "/findByUserId";
            String update = userAccount + "/update";
        }
    }

    interface Third {
        String third = api + "/third";

        interface Sms {
            String sms = third + "/sms";

            String pincode = sms + "/pinCode";
            String insurance = sms + "/insurance";
            String joint = sms + "/joint";
            String batch = sms + "/batch";
            String report = sms + "/report";
        }

        interface Wxlite {
            String wxlite = third + "/wxlite";

            String getOpenId = wxlite + "/getOpenId";
            String getAccessToken = wxlite + "/getAccessToken";
        }

        interface Wxscore {
            String wxscore = third + "/wxscore";

            String decryptAes256ToString = wxscore + "/decryptAes256ToString";
            String userServiceState = wxscore + "/userServiceState";
            String queryByOrderNo = wxscore + "/queryByOrderNo";
            String startOrder = wxscore + "/startOrder";
            String buildUseRes = wxscore + "/buildUseRes";
            String completeOrder = wxscore + "/completeOrder";
            String syncOrder = wxscore + "/syncOrder";
            String wxpayScoreEnable = wxscore + "/wxpayScoreEnable";
            String wxscoreDetail = wxscore + "/wxscoreDetail";
        }

        interface Zfblite {
            String zfblite = third + "/zfblite";

            String getOpenId = zfblite + "/getOpenId";
        }

        interface Amap {
            String amap = third + "/map";

            String baseStationLocation = amap + "/baseStationLocation";
            String getAddress = amap + "/getAddress";
            String getIPAddress = amap + "/getIPAddress";
            String createFence = amap + "/createFence";
            String fenceStatus = amap + "/fenceStatus";
            String deleteFence = amap + "/deleteFence";
        }

        interface BdMap {
            String bdMap = third + "/bdMap";

            String addEntity = bdMap + "/addEntity";
            String updateEntity = bdMap + "/updateEntity";
            String addPoints = bdMap + "/addPoints";
            String addPoint = bdMap + "/addPoint";
            String getTrack = bdMap + "/getTrack";
        }

        interface DataPay {
            String dataPay = third + "/dataPay";

            String identifyFace = dataPay + "/identifyFace";
            String identifyIdCard = dataPay + "/identifyIdCard";
        }

        interface Hmb {
            String hmb = third + "/hmb";

            String insure = hmb + "/insure";
            String identifyIdCard = hmb + "/identifyIdCard";
        }

        interface Bgb {
            String bgb = third + "/bgb";

            String insure = bgb + "/insure";
            String identifyIdCard = bgb + "/identifyIdCard";
            String policyQuery = bgb + "/policyQuery";
        }

        interface Push {
            String push = third + "/push";

            String pushTimeNotation = push + "/pushTimeNotation";
            String pushNotation = push + "/pushNotation";
            String pushWarn = push + "/pushWarn";
        }

        interface DeviceSms{
            String deviceSms = third + "/deviceSms";

            interface Huahong{
                String huahong = deviceSms + "/huahong";
                String send = huahong + "/send";
                String receive = huahong + "/receive";
            }

            interface Dahan{
                String dahan = deviceSms + "/dahan";
                String httpSend = dahan + "/httpSend";
            }
            interface Youyun{
                String youyun = deviceSms + "/youyun";
                String httpSend = youyun + "/httpSend";
            }
        }
    }

    interface BikeCenter {
        String bikeCenter = api + "/bike-center";

        interface BikeLoc {
            String bikeLoc = bikeCenter + "/bikeLoc";

            String insertBikeLoc = bikeLoc + "/insertBikeLoc";
            String deleteCacheScanLoc = bikeLoc + "/deleteCacheScanLoc";
            String findLastScanLoc = bikeLoc + "/findLastScanLoc";
        }

        interface Bike {
            String bike = bikeCenter + "/bike";
            String findByImei = bike + "/findByImei";
            String findByDeviceId = bike + "/findByDeviceId";
            String findByImeiOrDeviceId = bike + "/findByImeiOrDeviceId";
            String findConfigByType = bike + "/findConfigByType";
        }

        interface BikeStatus {
            String bikeStatus = bikeCenter + "/bikeStatus";
            String findStatusByBikeIId = bikeStatus + "/findStatusByBikeIId";
            String findByImei = bikeStatus + "/findByImei";
            String update = bikeStatus + "/update";
        }

        interface BikeGpsStatus {
            String bikeGpsStatus = bikeCenter + "/bikeGpsStatus";

            String findByImei = bikeGpsStatus + "/findByImei";
            String updatePg = bikeGpsStatus + "/updatePg";
            String updatePh = bikeGpsStatus + "/updatePh";
            String updatePl = bikeGpsStatus + "/updatePl";
            String update = bikeGpsStatus + "/update";
        }

        interface BikeAddress {
            String bikeAddress = bikeCenter + "/bikeAddress";

            String updateBikeAddress = bikeAddress + "/updateBikeAddress";
        }

        interface SmsCard {
            String smsCard = bikeCenter + "/smsCard";

            String findByImsi = smsCard + "/findByImsi";
        }
    }

    interface ControlCenter {
        String controlCenter = api + "/control-center";

        interface Track {
            String track = controlCenter + "/track";

            String getTrackByPeriod = track + "/getTrackByPeriod";
            String getMoveTrackByTime = track + "/getMoveTrackByTime";
            String getLocationByTime = track + "/getLocationByTime";
        }

        interface RideTrack {
            String rideTrack = controlCenter + "/rideTrack";

            String getLastRideTrack = rideTrack + "/getLastRideTrack";
            String getOneWithCursor = rideTrack + "/getOneWithCursor";
            String insertRideTracks = rideTrack + "/insertRideTracks";
            String getRideTrack = rideTrack + "/getRideTrack";
            String getRideTrackAfter = rideTrack + "/getRideTrackAfter";
            String getRideTrackAfterAndCursor = rideTrack + "/getRideTrackAfterAndCursor";
            String saveCursorRideTrack = rideTrack + "/saveCursorRideTrack";
        }

    }

    interface StationCenter {
        String stationCenter = api + "/station-center";

        interface StationGeo {
            String stationGeo = stationCenter + "/stationGeo";

            String isAtStation = stationGeo + "/isAtStation";
            String isAtStationWithCompensate = stationGeo + "/isAtStationWithCompensate";
            String isAtArea = stationGeo + "/isAtArea";
        }

        interface Station {
            String station = stationCenter + "/station";

            String getNearestStation = station + "/getNearestStation";
            String findByStationId = station + "/findByStationId";
        }

        interface StationStatus {
            String stationStatus = stationCenter + "/stationStatus";

            String update = stationStatus + "/update";
        }
    }

    interface AgentCenter {
        String agentCenter = api + "/agent-center";

        interface Config {
            String config = agentCenter + "/config";

            String getAgentConfig = config + "/getAgentConfig";
            String allowAgents = config + "/allowAgents";
            String findByImei = config + "/findByImei";
        }

        interface Agent {
            String agent = agentCenter + "/agent";

            String findByCity = agent + "/findByCity";
        }

        interface LongRentConfig {
            String longRentConfig = agentCenter + "/longRentConfig";

            String findByAgentId = longRentConfig + "/findByAgentId";
        }

        interface TakeawayConfig {
            String takeawayConfig = agentCenter + "/takeawayConfig";

            String findByAgentId = takeawayConfig + "/findByAgentId";
        }

        interface Ops {
            String ops = agentCenter + "/ops";

            interface Warn {
                String warn = ops + "/warn";
                String pushWarn = warn + "/pushWarn";
            }
            interface UseRecord{
                String useRecord = ops + "/useRecord";
                String findByUsingBike = useRecord + "/findByUsingBike";
            }
        }

    }

    interface OrderCenter {
        String orderCenter = api + "/order-center";

        interface LongRent {
            String longRent = orderCenter + "/longRent";

            String findValidByUserId = longRent + "/findValidByUserId";
            String hasLongRent = longRent + "/hasLongRent";
            String findLastOne = longRent + "/findLastOne";
            String create = longRent + "/create";
        }

        interface ThirdRecord {
            String thirdRecord = orderCenter + "/thirdRecord";

            String insert = thirdRecord + "/insert";
        }

        interface Ride {
            String ride = orderCenter + "/ride";

            String findRidingByImei = ride + "/findRidingByImei";
            String findRidingByMobileNo = ride + "/findRidingByMobileNo";
            String findById = ride + "/findById";
            String findAnyByMobileNo = ride + "/findAnyByMobileNo";
            String findEndByMobileNo = ride + "/findEndByMobileNo";
            String findEndPageByMobileNo = ride + "/findEndPageByMobileNo";
        }

        interface JournalAccount {
            String journalAccount = orderCenter + "/journalAccount";

            String insert4Charge = journalAccount + "/insert4Charge";
            String insert4Ride = journalAccount + "/insert4Ride";
            String insert4LongRent = journalAccount + "/insert4LongRent";
        }
    }

}