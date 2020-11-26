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

package com.qdigo.ebike.api.domain.dto.third.insurance;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * Description: 
 * date: 2020/1/9 11:53 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Data
public class InsuranceRecordDto {
    //private long id;
    private long rideRecordId;
    //姓名
    private String name;
    //身份证号
    private String idNo;
    //手机号
    private String mobileNo;
    //商户订单号，海绵保所传
    private String orderSn;
    //保单号，海绵保所传
    private String policyNo = "";
    //错误码，海绵保所传
    private String errorCode = "";
    //错误信息，海绵保所传
    private String errorMsg = "";
    private String productCode;
    private Date startTime;
    private Date endTime;
    private String insureType;
}
