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

package com.qdigo.ebike.iotcenter.exception;




public class IotServiceBizException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4540588052427941409L;
	private String code;
    private String msg;

    public IotServiceBizException() {
    }


    public IotServiceBizException(IotServiceExceptionEnum bizEnum) {
        this.code = bizEnum.getCode();
        this.msg = bizEnum.getMsg();
    }

    public IotServiceBizException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
