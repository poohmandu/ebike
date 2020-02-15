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

package com.qdigo.ebike.controlcenter.domain.entity.scenic;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 一张卡可以被多个用户充值
 */
@Data
@Entity
@Table(name = "scenic_entity_card")
public class EntityCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long entityCardId;

    @Column(nullable = false, unique = true, length = 25)
    private String entityCardNo; //充值卡上的卡号

    private double amount; //充值面额

    private double hotelAmount; //返利酒店

    private double userAmount; //返利用户

    @Column(name = "qr_code", nullable = false)
    private String QRCode; //二维码内容

    private Long hotelId; //激活实体卡:绑定的时候决定

    private boolean valid; //是否有效

    private Date activeTime; //激活时间

    @Column(nullable = false)
    private Date createdTime;//出厂时间

    @Column(nullable = false)
    private Date endTime; //失效时间

}
