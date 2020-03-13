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

package com.qdigo.ebike.controlcenter.service.inner.cammand;

import java.util.function.Consumer;

/**
 * Created by niezhao on 2017/3/30.
 */
//rebuild 融入spring管理
public class DeviceCtrl {

    private static int i_seq = 0;//可能还用不到

    //点火
    static boolean Ignition(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(3).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //熄火
    static boolean Flameout(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq++).cmd(3).param("0").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //4获取版本号


    //修改GPS发包频率
    static boolean SetPTIME(String imei, int pTime, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(7).param(String.valueOf(pTime) + ",0").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //关闭电门锁后休眠时间
    static boolean SetHTIME(String imei, int hTime, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(7).param("0," + String.valueOf(hTime)).build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //硬重启
    static boolean Reboot(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(8).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //修改设备内置IMEI，防止飞号
    static boolean UpdateImei(String imei, String newImei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(11).param(newImei).build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    static boolean SetSensitivity(String imei, int grade, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(12).param(String.valueOf((char) grade)).build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //20 蜂鸣器响10秒

    static boolean Lock(String imei, Consumer<byte[]> consumer, int count) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(24).param("1").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer, count);
        //return Connection0.getInstance(imei).Send(cmd, consumer);
    }

    static boolean Unlock(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(24).param("0").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
        //return Connection0.getInstance(imei).Send(cmd, consumer);
    }

    //已废弃,改为打开蓝牙
    static boolean AutoLockOn(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(27).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);

        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    static boolean AutoLockOff(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(27).param("0").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    // 自动锁车 改为 蓝牙打开
    static boolean OpenBle(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(27).param("1").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //关闭蓝牙
    static boolean CloseBle(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(27).param("0").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //请求立即上报心跳包reqHearBeat
    static boolean ReqHearbeat(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(30).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //寻车开始
    static boolean SeekStart(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(41).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //寻车结束
    static boolean SeekEnd(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(41).param("0").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //上电
    static boolean Fire(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(42).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //断电
    static boolean Shutdown(String imei, Consumer<byte[]> consumer, int count) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(42).param("0").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer, count);
    }

    //遥控器学习
    static boolean Remotlearn(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(43).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //高档位
    static boolean HighGear(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(44).param("0").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //低档位
    static boolean LowGear(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(44).param("1").build();
        byte[] cmd = builder.toCmd();
        //consumer.accept(cmd);
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //45 11  开坐垫锁
    static boolean SeatCushion(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("11").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //打开服务区报警
    static boolean OpenAreaWarn(String imei, boolean hasResponse, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("12").build();
        byte[] cmd = builder.toCmd();
        if (hasResponse) {
            return Connection.getInstance(imei).SendCmd(cmd, consumer);
        } else {
            Connection.getInstance(imei).sendNoResponse(cmd, consumer);
            return true;
        }
    }

    //关闭服务区报警
    static boolean CloseAreaWarn(String imei, boolean hasResponse, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("13").build();
        byte[] cmd = builder.toCmd();
        if (hasResponse) {
            return Connection.getInstance(imei).SendCmd(cmd, consumer);
        } else {
            Connection.getInstance(imei).sendNoResponse(cmd, consumer);
            return true;
        }
    }

    enum Voice {
        balance("14"), endSuccess("15"), scanSuccess("16"), battery30("17"), battery20("18"), battery10("19"), atStation("20"), notAtStation("21");
        public String param;

        Voice(String param) {
            this.param = param;
        }
    }

    public static boolean playVoice(String imei, Voice voice, boolean hasResponse, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param(voice.param).build();
        byte[] cmd = builder.toCmd();
        if (hasResponse) {
            return Connection.getInstance(imei).SendCmd(cmd, consumer);
        } else {
            Connection.getInstance(imei).sendNoResponse(cmd, consumer);
            return true;
        }
    }

    //45 31 立即上报蓝牙MAC地址
    public static boolean GetMac(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("31").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    static boolean RemoteEnable(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("32").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    static boolean RemoteDisenable(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("33").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    static boolean ForceEnable(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("34").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }


    static boolean ForceDisenable(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(45).param("35").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //GPS打开
    static boolean GPSOpen(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(47).param("1").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //GPS关闭
    static boolean GPSClose(String imei, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(47).param("0").build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //自定义发送
    static boolean Send(String imei, int command, String param, Consumer<byte[]> consumer) {
        CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(command).param(param).build();
        byte[] cmd = builder.toCmd();
        return Connection.getInstance(imei).SendCmd(cmd, consumer);
    }

    //充电桩控制
    static class ChargerCtrl {

        //打开充电口
        static boolean openPort(String imei, String port, Consumer<byte[]> consumer) {
            String param = port + ",1";
            CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(1).param(param).build();
            byte[] cmd = builder.toChargerCmd();
            //consumer.accept(cmd);
            return Connection.getChargerInstance(imei).SendCmd(cmd, consumer);
        }

        //关闭充电口
        static boolean closePort(String imei, String port, Consumer<byte[]> consumer) {
            String param = port + ",0";
            CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(1).param(param).build();
            byte[] cmd = builder.toChargerCmd();
            //consumer.accept(cmd);
            return Connection.getChargerInstance(imei).SendCmd(cmd, consumer);
        }

        //打开风扇
        static boolean openBlower(String imei, String blowerNo, Consumer<byte[]> consumer) {
            String param = blowerNo + ",1";
            CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(2).param(param).build();
            byte[] cmd = builder.toChargerCmd();
            //consumer.accept(cmd);
            return Connection.getChargerInstance(imei).SendCmd(cmd, consumer);
        }

        //关闭风扇
        static boolean closeBlower(String imei, String blowerNo, Consumer<byte[]> consumer) {
            String param = blowerNo + ",0";
            CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(2).param(param).build();
            byte[] cmd = builder.toChargerCmd();
            //consumer.accept(cmd);
            return Connection.getChargerInstance(imei).SendCmd(cmd, consumer);
        }


        static boolean startCharge(String imei, int port, int hours, Consumer<byte[]> consumer) {
            String param = port + "," + hours;
            CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(3).param(param).build();
            byte[] cmd = builder.toChargerCmd();
            return Connection.getChargerInstance(imei).SendCmd(cmd, consumer);
        }

        static boolean showQRCode(String imei, String content, Consumer<byte[]> consumer) {
            String param = content.length() + "," + content;
            CmdBuilder builder = new CmdBuilder.Builder().imei(imei).seq(i_seq).cmd(4).param(param).build();
            byte[] cmd = builder.toChargerCmd();
            return Connection.getChargerInstance(imei).SendCmd(cmd, consumer);
        }

    }

}
