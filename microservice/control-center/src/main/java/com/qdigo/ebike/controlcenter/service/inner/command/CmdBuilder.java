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

import com.qdigo.ebike.common.core.constants.ConfigConstants;

/**
 * Created by niezhao on 2017/3/30.
 */
public class CmdBuilder {

    private static long CMD_IMEI = 0;
    private static int CMD_SEQ = 0;
    private static int CMD_CMD = 0;
    private static String CMD_PARAM = "0";

    public static class Builder {

        private long imei;
        private int seq;
        private int cmd;
        private String param;

        public Builder imei(String val) {
            if (val.startsWith(ConfigConstants.imei.getConstant())) {
                //15位
                val = val.substring(7);
            } else {
                throw new RuntimeException("传入的imei号格式不对:" + val);
            }
            imei = Long.parseLong(val);
            return this;
        }

        public Builder seq(int val) {
            seq = val;
            return this;
        }

        public Builder cmd(int val) {
            cmd = val;
            return this;
        }

        public Builder param(String val) {
            param = val;
            return this;
        }

        public CmdBuilder build() {
            return new CmdBuilder(this);
        }
    }

    private CmdBuilder(Builder b) {
        CmdBuilder.CMD_IMEI = b.imei;
        CmdBuilder.CMD_SEQ = b.seq;
        CmdBuilder.CMD_CMD = b.cmd;
        CmdBuilder.CMD_PARAM = b.param;
    }

    byte[] toCmd() {
        char[] param = CmdBuilder.CMD_PARAM.toCharArray();
        byte[] res = new byte[8 + param.length + 1];
        res[0] = (byte) 'P';
        res[1] = (byte) 'X';
        res[2] = (byte) (CmdBuilder.CMD_IMEI >> 24 & 0xFF);
        res[3] = (byte) ((CmdBuilder.CMD_IMEI >> 16) & 0xFF);
        res[4] = (byte) ((CmdBuilder.CMD_IMEI >> 8) & 0xFF);
        res[5] = (byte) ((CmdBuilder.CMD_IMEI) & 0xFF);
        int seq = (int) (Math.random() * 127);
        res[6] = (byte) (seq == '$' ? (int) (Math.random() * 127) : seq);
        res[7] = (byte) CmdBuilder.CMD_CMD;

        for (int i = 0; i < param.length; i++) {
            res[8 + i] = (byte) param[i];
        }
        //最后加上 $ 结束符
        res[8 + param.length] = '$';//十进制36是$
        return res;
    }

    byte[] toChargerCmd() {
        char[] param = CmdBuilder.CMD_PARAM.toCharArray();
        byte[] res = new byte[8 + param.length + 1];
        res[0] = (byte) 'M';
        res[1] = (byte) 'X';
        res[2] = (byte) (CmdBuilder.CMD_IMEI >> 24 & 0xFF);
        res[3] = (byte) ((CmdBuilder.CMD_IMEI >> 16) & 0xFF);
        res[4] = (byte) ((CmdBuilder.CMD_IMEI >> 8) & 0xFF);
        res[5] = (byte) ((CmdBuilder.CMD_IMEI) & 0xFF);
        int seq = (int) (Math.random() * 127);
        res[6] = (byte) (seq == '$' ? (int) (Math.random() * 127) : seq);
        res[7] = (byte) CmdBuilder.CMD_CMD;

        for (int i = 0; i < param.length; i++) {
            res[8 + i] = (byte) param[i];
        }
        //最后加上 $ 结束符
        res[8 + param.length] = '$';//十进制36是$
        return res;
    }

}
