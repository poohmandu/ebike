/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.usercenter.domain.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by niezhao on 2018/2/7.
 *
 * ID绑定关系
 */
@Data
@Entity
@Table(name = "user_wx_open_info")
public class UserWxOpenInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "user_id")
    private long userId;

    @Column(nullable = false, length = 50)
    private String appId;

    @Column(nullable = false, length = 50)
    private String openId;

    @Column(length = 50)
    private String unionId;

    @Column(length = 10)
    private String version;

}
