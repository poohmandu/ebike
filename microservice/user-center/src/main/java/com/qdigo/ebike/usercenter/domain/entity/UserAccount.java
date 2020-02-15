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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Description: 
 * date: 2019/12/18 6:28 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */

@Entity
@Table(name = "user_account", indexes = {@Index(columnList = "user_id", unique = true)})
@JsonIgnoreProperties(value = {"user"})
public class UserAccount extends AbstractAuditingEntity implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userAccountId;

    @Column(scale = 2, nullable = false)
    private double deposit = 0.0; //元

    @Column(length = 20)
    private String refundStatus = Status.RefundStatus.not.getVal(); //退款状态: 受理中'pending' ,退款成功'success',退款失败'fail'

    //name为数据库中列名，referencedColumnName为数据库中被指向表的参考列
    @OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false,
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(scale = 2, nullable = false)
    private double requireDeposit = 299.0;//元

    @Column(length = 10, nullable = false)
    private String zmScore = "";

    @Column(length = 20, nullable = false)
    private String wxscore = "";

    @Column(scale = 2, nullable = false)
    private double balance = 0; //为负时，押金无法退还

    @Column(nullable = false, columnDefinition = "double default 0")
    private double giftBalance = 0;

    @Version
    private int version;

    public long getUserAccountId() {
        return userAccountId;
    }

    public UserAccount setUserAccountId(long userAccountId) {
        this.userAccountId = userAccountId;
        return this;
    }

    public double getDeposit() {
        return deposit;
    }

    public UserAccount setDeposit(double deposit) {
        this.deposit = deposit;
        return this;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public UserAccount setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserAccount setUser(User user) {
        this.user = user;
        return this;
    }

    public double getRequireDeposit() {
        return requireDeposit;
    }

    public UserAccount setRequireDeposit(double requireDeposit) {
        this.requireDeposit = requireDeposit;
        return this;
    }

    public String getZmScore() {
        return zmScore;
    }

    public UserAccount setZmScore(String zmScore) {
        this.zmScore = zmScore;
        return this;
    }

    public String getWxscore() {
        return wxscore;
    }

    public UserAccount setWxscore(String wxscore) {
        this.wxscore = wxscore;
        return this;
    }

    public double getBalance() {
        return balance;
    }

    public UserAccount setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public double getGiftBalance() {
        return giftBalance;
    }

    public UserAccount setGiftBalance(double giftBalance) {
        this.giftBalance = giftBalance;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public UserAccount setVersion(int version) {
        this.version = version;
        return this;
    }
}
