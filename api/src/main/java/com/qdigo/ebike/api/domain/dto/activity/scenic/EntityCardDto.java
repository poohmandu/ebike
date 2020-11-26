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

package com.qdigo.ebike.api.domain.dto.activity.scenic;

import lombok.Data;

import java.util.Date;

/**
 * description: 
 *
 * date: 2020/4/4 11:13 PM
 * @author niezhao
 */
@Data
public class EntityCardDto {
    private long entityCardId;
    private String entityCardNo; //充值卡上的卡号
    private double amount; //充值面额
    private double hotelAmount; //返利酒店
    private double userAmount; //返利用户
    private String QRCode; //二维码内容
    private Long hotelId; //激活实体卡:绑定的时候决定
    private boolean valid; //是否有效
    private Date activeTime; //激活时间
    private Date createdTime;//出厂时间
    private Date endTime; //失效时间
}
