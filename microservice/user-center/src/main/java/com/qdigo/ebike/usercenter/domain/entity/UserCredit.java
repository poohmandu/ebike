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

import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by niezhao on 2017/2/23.
 */
@Entity
@Table(name = "user_credit")
public class UserCredit extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1342568706423553707L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userCreditId;

    @Column(length = 5, nullable = false)
    private int score = 0;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "userCredit")
    @OrderBy(value = "eventTime desc")
    private List<UserCreditRecord> creditRecords = new ArrayList<>();

    public long getUserCreditId() {
        return userCreditId;
    }

    public UserCredit setUserCreditId(long userCreditId) {
        this.userCreditId = userCreditId;
        return this;
    }

    public int getScore() {
        return score;
    }

    public UserCredit setScore(int score) {
        this.score = score;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserCredit setUser(User user) {
        this.user = user;
        return this;
    }

    public List<UserCreditRecord> getCreditRecords() {
        return creditRecords;
    }

    public UserCredit setCreditRecords(List<UserCreditRecord> creditRecords) {
        this.creditRecords = creditRecords;
        return this;
    }

}
