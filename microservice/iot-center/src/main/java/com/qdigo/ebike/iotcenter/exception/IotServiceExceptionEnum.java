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

public enum IotServiceExceptionEnum {
	SYSTEM("9999","系统异常"),
	NOT_SUPPORT_SOCKET_DATA("socket_1001","不支持该格式数据包"),
	CMD_DOWN_ERROR("socket_1002","下行命令发送失败"),
	PH_HEART_ERROR("socket_1003","心跳响应包发送失败"),
	CMD_DOWN_RESP_ERROR("socket_1004","下行命令响应包发送失败"),
	
	SEND_UP_PC_HTTP_ERROR("PC_1005","发送上行PC包http请求异常"),
	SAVE_UP_PC_REDIS_ERROR("PC_1006","保存上行pc包到缓存异常"),
	SAVE_DOWN_PC_REDIS_ERROR("PC_1007","保存下行pc包到缓存异常"),
	
	SEND_UP_PG_HTTP_ERROR("PG_1008","发送上行PG包http请求异常"),
	SAVE_UP_PG_REDIS_ERROR("PG_1009","保存上行PG包到缓存异常"),
	
	SEND_UP_PH_HTTP_ERROR("PH_1010","发送上行PH包http请求异常"),
	SAVE_UP_PH_REDIS_ERROR("PH_1011","保存上行PH包到缓存异常"),
	
	SEND_UP_PL_HTTP_ERROR("PL_1012","发送上行PL包http请求异常"),
	SAVE_UP_PL_REDIS_ERROR("PL_1013","保存上行PL包到缓存异常"),
	
	SEND_UP_MD_HTTP_ERROR("MD_1014","发送上行MD包http请求异常"),
	SAVE_UP_MD_REDIS_ERROR("MD_1015","保存上行MD包到缓存异常"),
	
	SEND_UP_ML_HTTP_ERROR("MD_1016","发送上行ML包http请求异常"),
	SAVE_UP_ML_REDIS_ERROR("MD_1017","保存上行ML包到缓存异常"),
	
	SEND_UP_MC_HTTP_ERROR("MC_1018","发送上行MC包http请求异常"),
	SAVE_UP_MC_REDIS_ERROR("MC_1019","保存上行MC包到缓存异常"),
	SAVE_DOWN_MC_REDIS_ERROR("MC_1020","保存下行MC包到缓存异常"),
	;
	private String code;
	private String msg;
	private IotServiceExceptionEnum(String code,String msg){
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
