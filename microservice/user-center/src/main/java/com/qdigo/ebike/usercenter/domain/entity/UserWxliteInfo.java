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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by niezhao on 2017/10/12.
 */
@Data
@Entity
@Table(name = "user_wxlite_info")
public class UserWxliteInfo {

    @Id
    private String mobileNo;

    @Column(nullable = false, length = 50, name = "sdk_version")
    private String sdkVersion = "";
    @Column(nullable = false, length = 50)
    private String version = "";
    @Column(nullable = false, length = 50)
    private String platform = "";
    @Column(nullable = false, length = 50)
    private String system = "";
    @Column(nullable = false, length = 50)
    private String brand = "";
    @Column(nullable = false, length = 50)
    private String model = "";
    @Column(nullable = false, length = 50)
    private String pixelRatio = "";
    @Column(nullable = false, length = 50)
    private String language = "";
    @Column(nullable = false, length = 50)
    private String fontSizeSetting = "";
    @Column(nullable = false, length = 20)
    private String wxliteVersion;
    @Column(nullable = false, length = 50)
    private String appId;

}
