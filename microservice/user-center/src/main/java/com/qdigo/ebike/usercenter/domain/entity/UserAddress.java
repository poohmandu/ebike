/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

import com.qdigo.ebike.api.domain.dto.third.map.Address;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by niezhao on 2017/7/22.
 */
@Entity
@Table
@ToString
public class UserAddress extends Address {

    public enum LocType {
        loc, ip
    }

    @Id
    private String mobileNo;

    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'loc'")
    @Enumerated(EnumType.STRING)
    private LocType locType;

    public String getMobileNo() {
        return mobileNo;
    }

    public UserAddress setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public LocType getLocType() {
        return locType;
    }

    public UserAddress setLocType(LocType locType) {
        this.locType = locType;
        return this;
    }
}
