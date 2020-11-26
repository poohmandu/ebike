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

package com.qdigo.ebike.third.service.insurance.bgb;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = {"product_code", "policy_user_name", "policy_user_certno", "policy_user_certtype", "policy_user_sex",
        "policy_user_birthday", "unlock_time", "policy_user_mobile", "biz_order_sn", "province", "city", "promote_id"})
public class BaigeRequestBody {
    private String product_code;
    private String policy_user_name;
    private String policy_user_certno;
    private String policy_user_certtype;
    private String policy_user_sex;
    private String policy_user_birthday;
    private String unlock_time;
    private String policy_user_mobile;
    private String biz_order_sn;
    private String province;
    private String city;
    private String promote_id;

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getPolicy_user_name() {
        return policy_user_name;
    }

    public void setPolicy_user_name(String policy_user_name) {
        this.policy_user_name = policy_user_name;
    }

    public String getPolicy_user_certno() {
        return policy_user_certno;
    }

    public void setPolicy_user_certno(String policy_user_certno) {
        this.policy_user_certno = policy_user_certno;
    }

    public String getPolicy_user_certtype() {
        return policy_user_certtype;
    }

    public void setPolicy_user_certtype(String policy_user_certtype) {
        this.policy_user_certtype = policy_user_certtype;
    }

    public String getUnlock_time() {
        return unlock_time;
    }

    public void setUnlock_time(String unlock_time) {
        this.unlock_time = unlock_time;
    }

    public String getBiz_order_sn() {
        return biz_order_sn;
    }

    public void setBiz_order_sn(String biz_order_sn) {
        this.biz_order_sn = biz_order_sn;
    }

    public String getPolicy_user_sex() {
        return policy_user_sex;
    }

    public void setPolicy_user_sex(String policy_user_sex) {
        this.policy_user_sex = policy_user_sex;
    }

    public String getPolicy_user_birthday() {
        return policy_user_birthday;
    }

    public void setPolicy_user_birthday(String policy_user_birthday) {
        this.policy_user_birthday = policy_user_birthday;
    }

    public String getPolicy_user_mobile() {
        return policy_user_mobile;
    }

    public void setPolicy_user_mobile(String policy_user_mobile) {
        this.policy_user_mobile = policy_user_mobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPromote_id() {
        return promote_id;
    }

    public void setPromote_id(String promote_id) {
        this.promote_id = promote_id;
    }
}
