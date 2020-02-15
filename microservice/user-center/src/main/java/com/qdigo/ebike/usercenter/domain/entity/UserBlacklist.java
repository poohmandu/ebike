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

import javax.persistence.*;
import java.util.Date;

/**
 * Created by niezhao on 2018/1/2.
 */
@Entity
@Table(name = "user_blacklist")
public class UserBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private long userId;

    @Column(length = 100, nullable = false)
    private String cause;

    private Date startTime;

    private Date endTime;

    public Date getStartTime() {
        return startTime;
    }

    public UserBlacklist setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public UserBlacklist setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public long getId() {
        return id;
    }

    public UserBlacklist setId(long id) {
        this.id = id;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public UserBlacklist setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getCause() {
        return cause;
    }

    public UserBlacklist setCause(String cause) {
        this.cause = cause;
        return this;
    }

}
