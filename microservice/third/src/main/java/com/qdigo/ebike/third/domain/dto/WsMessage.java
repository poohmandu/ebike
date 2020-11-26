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

package com.qdigo.ebike.third.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.Data;
import lombok.ToString;

/**
 * Created by niezhao on 2017/7/3.
 */
@ToString
public class WsMessage {

    public enum RequestType {
        heartBeat, biz
    }

    public enum BizType {
        bikeInfo, ridingTrack
    }

    public enum MessageType {
        push, response, heartBeat
    }


    //(1) -- in
    @Data
    public static class Request {
        private String mobileNo;
        private RequestType requestType;
        private BizType bizType;
        private JSONObject data;
        private long timestamp;
    }

    //(1) -- out
    @Data
    public static class Message {
        private MessageType messageType;
        private String mobileNo;
        private BizType responseType;
        private Object data;
        private long timestamp;
    }

    //(2) -- out
    @Data
    public static class ResponseMessage {
        private int statusCode;
        private String message;
        private Object data;

        public Message toMessage(String mobileNo, BizType responseType) {
            Message message = new Message();
            message.setMessageType(MessageType.response);
            message.setMobileNo(mobileNo);
            message.setResponseType(responseType);
            message.setData(this);
            message.setTimestamp(System.currentTimeMillis());
            return message;
        }
    }

    //(2) -- out
    @Data
    public static class PushMessage {
        private String title;
        private Const.PushType pushType;
        private Object data;

        public Message toMessage(String mobileNo) {
            Message message = new Message();
            message.setMessageType(MessageType.push);
            message.setMobileNo(mobileNo);
            message.setTimestamp(System.currentTimeMillis());
            message.setData(this);
            return message;
        }
    }


}
