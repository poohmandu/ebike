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

package com.qdigo.ebike.controlcenter.service.inner.command;


import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.ByteArrayToNumber;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.commonconfig.configuration.ThreadPool;
import com.qdigo.ebike.commonconfig.configuration.properties.QdigoOnOffProperties;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PHPackage;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PXPackage;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PHMongoService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by niezhao on 2017/6/2.
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DeviceService {

    private final PXService pxService;
    private final QdigoOnOffProperties onOffProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final PHMongoService phMongoService;

    @Inject
    private DevSMSService devSMSService;

    private void insertPX(final byte[] bytes, final String mobileNo) {
        String imei = ConfigConstants.imei.getConstant() + ByteArrayToNumber.byteArrayToInt(bytes, 2);
        byte seq = bytes[6];
        byte cmd = bytes[7];
        //去掉$符
        byte[] params = new byte[bytes.length - 9];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 9);
        String param = ByteArrayToNumber.bytesToString(params);

        PXPackage px = PXPackage.builder().pxImei(imei).pxCmd(cmd).pxParam(param).pxSequence(seq)
                .timestamp(System.currentTimeMillis()).mobileNo(mobileNo).build();

        log.debug("user:{},bike:{}保存PX包,bytes:{},PX:{}", mobileNo, imei, Arrays.toString(bytes), px);
        pxService.insertPX(px);
    }

    private boolean canI() {
        if (onOffProperties.isCommandClientSend()) {
            return true;
        }
        try {
            log.info("配置生效,模拟发送");
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            log.info("配置生效,模拟发送失败");
        }
        return false;
    }


    public boolean ignition(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Ignition(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //熄火
    public boolean flameOut(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Flameout(imei, bytes -> insertPX(bytes, mobileNo));
    }


    public boolean setPTime(String imei, int pTime, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.SetPTIME(imei, pTime, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean setHTime(String imei, int hTime, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.SetHTIME(imei, hTime, bytes -> insertPX(bytes, mobileNo));
    }

    //硬重启
    public boolean reboot(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Reboot(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //修改imei号码
    public boolean updateImei(String imei, String newImei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.UpdateImei(imei, newImei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean setSensitivity(String imei, int grade, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.SetSensitivity(imei, grade, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean lock(String imei, String mobileNo) {
        if (!canI()) return true;
        return lock(imei, mobileNo, 2);
    }

    public boolean lock(String imei, String mobileNo, int count) {
        if (!canI()) return true;
        return DeviceCtrl.Lock(imei, bytes -> insertPX(bytes, mobileNo), count);
    }


    public boolean unLock(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Unlock(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean autoLockOn(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.AutoLockOn(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean autoLockOff(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.AutoLockOff(imei, bytes -> insertPX(bytes, mobileNo));
    }

    // 自动锁车 改为 蓝牙打开
    //@CatAnnotation
    public boolean openBle(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.OpenBle(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean closeBle(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.CloseBle(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //请求立即上报心跳包
    public boolean reqHearBeat(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.ReqHearbeat(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //寻车开始
    public boolean seekStart(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.SeekStart(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //寻车结束
    public boolean seekEnd(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.SeekEnd(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //上电
    public boolean fire(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Fire(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //断电
    public boolean shutdown(String imei, String mobileNo) {
        if (!canI()) return true;
        return shutdown(imei, mobileNo, 2);
    }

    public boolean shutdown(String imei, String mobileNo, int count) {
        if (!canI()) return true;
        return DeviceCtrl.Shutdown(imei, bytes -> insertPX(bytes, mobileNo), count);
    }

    //遥控器学习
    public boolean remoteLearn(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Remotlearn(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //高档位
    //@CatAnnotation
    public boolean highGear(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.HighGear(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //低档位
    //@CatAnnotation
    public boolean lowGear(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.LowGear(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //开坐垫
    public boolean seatCushion(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.SeatCushion(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //打开服务区报警
    public void openAreaWarn(String imei, String mobileNo, Consumer<Boolean> callback) {
        if (!canI()) return;
        CompletableFuture.supplyAsync(() -> {
            return DeviceCtrl.OpenAreaWarn(imei, true, bytes -> insertPX(bytes, mobileNo));
        }, ThreadPool.cachedThreadPool()).thenAccept(callback); //我们可能会想当然的认为它会阻塞当前线程,实际上不会能根据调用者线程是否空闲来使用当前线程还是用执行任务的线程池
    }

    public void openAreaWarn(String imei, String mobileNo) {
        if (!canI()) return;
        DeviceCtrl.OpenAreaWarn(imei, false, bytes -> insertPX(bytes, mobileNo));
    }

    //关闭服务区报警
    public void closeAreaWarn(String imei, String mobileNo, Consumer<Boolean> callback) {
        if (!canI()) return;
        CompletableFuture.supplyAsync(() -> {
            return DeviceCtrl.CloseAreaWarn(imei, true, bytes -> insertPX(bytes, mobileNo));
        }, ThreadPool.cachedThreadPool()).thenAccept(callback);
    }

    public void closeAreaWarn(String imei, String mobileNo) {
        if (!canI()) return;
        DeviceCtrl.CloseAreaWarn(imei, false, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean closeAreaWarnResult(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.CloseAreaWarn(imei, true, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean getMac(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.GetMac(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean remoteEnable(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.RemoteEnable(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean remoteDisenable(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.RemoteDisenable(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean forceEnable(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.ForceEnable(imei, bytes -> insertPX(bytes, mobileNo));
    }

    public boolean forceDisenable(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.ForceDisenable(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //打开GPS
    public boolean gpsOpen(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.GPSOpen(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //关闭GPS
    public boolean gpsClose(String imei, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.GPSClose(imei, bytes -> insertPX(bytes, mobileNo));
    }

    //短信上电
    public boolean smsOpen(String imei, String mobileNo) {
        if (!canI()) return true;
        boolean smsOpen = devSMSService.smsOpen(imei, mobileNo);
        return smsOpen && smsResult(imei, mobileNo, "#TURN;1");
    }

    //短信断电
    public boolean smsClose(String imei, String mobileNo) {
        if (!canI()) return true;
        val smsClose = devSMSService.smsClose(imei, mobileNo);
        return smsClose && smsResult(imei, mobileNo, "#TURN;0");
    }

    public Optional<Location> smsLocation(String imei, String mobileNo) {
        return devSMSService.smsLoc(imei, mobileNo);
    }

    //最长堵塞 15 秒左右
    private boolean smsResult(String imei, String mobileNo, String reply) {
        try {
            return devSMSService.receiveSMSAsync(imei, mobileNo, reply).get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("ExecutionException | InterruptedException 异常:" + e.getMessage());
            return false;
        }
    }


    //自定义发送
    public boolean send(String imei, int cmd, String param, String mobileNo) {
        if (!canI()) return true;
        return DeviceCtrl.Send(imei, cmd, param, bytes -> insertPX(bytes, mobileNo));
    }

    //@CatAnnotation
    public boolean gpsEnd(String imei, String mobileNo) {
        if (!canI()) return true;
        try {
            if (this.lock(imei, mobileNo)) {
                log.debug("user:{},bike:{}第一次只通过GPS上锁成功", mobileNo, imei);
                return true;
            } else {
                log.debug("user:{},bike:{}第一次只通过GPS上锁失败，尝试再上锁一次", mobileNo, imei);
                TimeUnit.SECONDS.sleep(1);
                return this.lock(imei, mobileNo);
            }
        } catch (Exception e) {
            log.error("还车时发生异常:", e);
            return false;
        }
    }

    public boolean gpsEnd0(String imei, String mobileNo) {
        if (!canI()) return true;
        try {
            val watch = new StopWatch();
            watch.start();
            boolean shutdown = this.shutdown(imei, mobileNo);//熄火
            TimeUnit.SECONDS.sleep(1);
            boolean lock = this.lock(imei, mobileNo);//上锁
            if (!shutdown) {
                log.debug("user:{},bike:{}第一次断电失败，尝试再断电一次", mobileNo, imei);
                shutdown = this.shutdown(imei, mobileNo);
            } else {
                log.debug("user:{},bike:{}第一次断电成功", mobileNo, imei);
            }
            if (!lock) {
                log.debug("user:{},bike:{}第一次上锁失败，尝试再上锁一次", mobileNo, imei);
                lock = this.lock(imei, mobileNo);
            } else {
                log.debug("user:{},bike:{}第一次上锁成功", mobileNo, imei);
            }
            val ok = shutdown && lock;
            watch.stop();
            log.debug("user:{},bike:{},socket操作用时{}毫秒,操作结果:{}", mobileNo, imei, watch.getTotalTimeMillis(), ok);
            return ok;
        } catch (InterruptedException e) {
            throw new RuntimeException("线程中断异常");
        }
    }

    @Async
    public ListenableFuture<Boolean> gpsSmsEndAsync(String imei, String mobileNo) {
        if (!canI()) return new AsyncResult<>(true);
        boolean close = this.gpsSmsEnd(imei, mobileNo);
        return new AsyncResult<>(close);
    }

    @Async
    public ListenableFuture<Boolean> gpsSmsEndAsync0(String imei, String mobileNo) {
        if (!canI()) return new AsyncResult<>(true);
        boolean close = this.gpsSmsEnd0(imei, mobileNo);
        return new AsyncResult<>(close);
    }

    public boolean gpsSmsEnd(String imei, String mobileNo) {
        if (!canI()) return true;
        try {
            Future<Boolean> future;
            if (this.lock(imei, mobileNo)) {
                log.debug("user:{},bike:{},第一次锁车成功", mobileNo, imei);
                return true;
            } else {
                log.debug("user:{},bike:{},第一次锁车失败,准备发送短信还车", mobileNo, imei);
                devSMSService.smsClose(imei, mobileNo);
                future = devSMSService.receiveSMSAsync(imei, mobileNo, "#TURN;0");
                TimeUnit.SECONDS.sleep(1);
                if (this.lock(imei, mobileNo)) {
                    return true;
                } else {
                    boolean smsOk = future.get();
                    log.debug("user:{},bike:{}短信还车结果:{}", mobileNo, imei, smsOk);
                    return smsOk;
                }
            }
        } catch (Exception e) {
            log.error("还车过程中发生错误:", e);
            return false;
        }
    }

    //@CatAnnotation
    public boolean gpsSmsEnd0(String imei, String mobileNo) {
        if (!canI()) return true;
        try {
            long start = System.currentTimeMillis();
            boolean ok;
            boolean useSms = false;
            Future<Boolean> future;
            boolean shutdown = this.shutdown(imei, mobileNo, 1);//熄火
            TimeUnit.SECONDS.sleep(1);
            boolean lock = this.lock(imei, mobileNo, 1);//上锁
            if (!shutdown || !lock) {
                log.debug("user:{},bike:{},第一次操作失败,发送短信还车", mobileNo, imei);
                devSMSService.smsClose(imei, mobileNo);
                future = devSMSService.receiveSMSAsync(imei, mobileNo, "#TURN;0");
            } else {
                future = new AsyncResult<>(false);
            }
            if (!shutdown) {
                log.debug("user:{},bike:{}第一次断电失败，尝试再断电一次", mobileNo, imei);
                shutdown = this.shutdown(imei, mobileNo, 1);
            } else {
                log.debug("user:{},bike:{}第一次断电成功", mobileNo, imei);
            }
            if (!lock) {
                log.debug("user:{},bike:{}第一次上锁失败，尝试再上锁一次", mobileNo, imei);
                lock = this.lock(imei, mobileNo, 1);
            } else {
                log.debug("user:{},bike:{}第一次上锁成功", mobileNo, imei);
            }
            if (shutdown && lock) {
                log.debug("user:{},bike:{}第二次断电并上锁成功", mobileNo, imei);
                ok = true;
            } else {
                ok = false;
                //boolean smsResult = future.get();
                useSms = true;
                log.debug("user:{},bike:{}正常情况下短信还车结果:{}", mobileNo, imei, false);
            }
            long end = System.currentTimeMillis();
            log.debug("user:{},bike:{},是否加上短信用时:{},还车一共用时时间:{}", mobileNo, imei, useSms, end - start);
            return ok;
        } catch (Exception e) {
            log.error("还车过程中发生错误:", e);
            return false;
        }
    }

    //@CatAnnotation
    public boolean gpsSmsEndFast(String imei, String mobileNo) {
        if (!canI()) return true;
        try {
            log.debug("user:{},bike:{}当PG包断的时候,直接先发送短信还车", mobileNo, imei);
            devSMSService.smsClose(imei, mobileNo);
            Future<Boolean> future = devSMSService.receiveSMSAsync(imei, mobileNo, "#TURN;0", true);
            if (this.lock(imei, mobileNo)) {
                log.debug("user:{},bike:{} 当PG包断的时候,第一次锁车成功", mobileNo, imei);
                return true;
            }
            boolean smsOk = future.get();
            log.debug("user:{},bike:{}当GPS断开情况下短信还车结果:{}", mobileNo, imei, smsOk);
            return smsOk;
        } catch (Exception e) {
            log.error("还车过程中发生错误:", e);
            return false;
        }
    }


    @Async
    @Token(key = "imei", expireSeconds = 60)
    public void rebootGPSAsync(String imei) {
        if (!canI()) return;
        log.debug("{}车辆重启GPS", imei);
        try {
            boolean gpsClose = false;
            for (int i = 0; i < 2; i++) {
                gpsClose = this.gpsClose(imei, "system");
                if (gpsClose) {
                    break;
                }
            }
            if (gpsClose) {
                TimeUnit.SECONDS.sleep(10);
                for (int i = 0; i < 2; i++) {
                    if (this.gpsOpen(imei, "system")) {
                        val key = Keys.flagReboot.getKey(imei);
                        redisTemplate.delete(key);
                        TimeUnit.SECONDS.sleep(40);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("bikeService的executorService被中断2:" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    //@CatAnnotation
    public PHPackage getHearBeat(String imei, String mobileNo) {
        if (!canI()) return null;
        try {
            val hearBeat = this.reqHearBeat(imei, mobileNo);
            if (!hearBeat) {
                log.debug("用户:{},车辆:{},发送请求心跳包指令失败", mobileNo, imei);
            }
            long timeout = 5000;
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < timeout) {
                PHPackage ph = phMongoService.findLatestPH(imei, start);
                if (ph != null) {
                    log.debug("用户:{},车辆:{},成功获取心跳包:{}", mobileNo, imei, ph);
                    return ph;
                }
                TimeUnit.MILLISECONDS.sleep(500);
            }
            log.debug("用户:{},车辆:{},获取心跳包等待超时", mobileNo, imei);
        } catch (Exception e) {
            log.debug("用户:{},车辆:{},发送请求心跳包指令异常", mobileNo, imei);
            log.error("获取心跳异常:" + e.getMessage());
        }
        return null;
    }

    @Async
    public ListenableFuture<PHPackage> getHearBeatAsync(String imei, String mobileNo) {
        if (!canI()) return new AsyncResult<>(null);
        try {
            return new AsyncResult<>(this.getHearBeat(imei, mobileNo));
        } catch (Exception e) {
            log.error("获取心跳异常:" + e.getMessage());
            return new AsyncResult<>(null);
        }
    }

}

