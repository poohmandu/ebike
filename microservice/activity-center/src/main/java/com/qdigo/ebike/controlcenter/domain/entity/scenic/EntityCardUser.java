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
 * 创建时就代表已完成扫码,一个用户可以用多张卡、一张卡可被多个用户用、但是一个用户和一张卡的绑定是唯一的
 */
@Data
@Entity
@Table(name = "scenic_entity_card_to_user")
public class EntityCardUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long userId;

    private long entityCardId;//指向entityCard

    private long hotelId;

    private double userAmount; //返利多少用户

    private double hotelAmount; //返利多少酒店

    private Date payTime; //付费返利的时间

    private Date scanTime;//确定关系的时间

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {scan, paid}

}
