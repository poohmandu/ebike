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

package com.qdigo.ebike.third.service.wxlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.CompleteOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.StartOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 微信支付分相关
 * 参考:https://pay.weixin.qq.com/wiki/doc/apiv3/payscore.php?chapter=12_2&index=1
 */
@Slf4j
@RemoteService
public class WxScoreServiceImpl implements WxscoreService {

    private static final String mch_id = "1395754202";
    private static final String mchSerialNo = "62B48DF5ED6465969B575793AC796A22759ACBAC";
    private static final String test_service_id = "2003001000000627025651101003456";
    private static final String service_id = "00003001000000627221751184375541";

    private static final String sign_type = "HMAC-SHA256";
    private static final String key = "fjr831222niezhao4511369qdigohaha";

    private static final String service_introduction = "电滴出行电动车租赁服务";
    private static final String service_location = "电滴出行电动车停车租借点";
    private static final int risk_amount = 10000; //分
    private static final int retry_risk_amount = 2000; //分

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private String nonceStr() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3] + idd[4];
    }

    private String out_no() {
        String curTime = FormatUtil.yyyyMMddHHmmss.format(new Date());//精确到秒就行了
        String key = Keys.wxlitePayScore.getKey(FormatUtil.getCurDate());
        Long num = redisTemplate.opsForValue().increment(key, 1);
        if (num <= 2) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }

        DecimalFormat df = new DecimalFormat("00000000");
        // 20151210093658804 12345678
        String outRequestNo = curTime + df.format(num);
        log.info("今日的生成的第{}个微信支付分out_no:{}", num, outRequestNo);
        return outRequestNo;
    }

    private String out_request_no() {
        return "REQ" + out_no();
    }

    public String out_order_no() {
        return "ORD" + out_no();
    }

    private String timestamp() {
        long l = System.currentTimeMillis() / 1000;
        return String.valueOf(l);
    }

    private String sign(final Map<String, String> data) {
        Set<String> keySet = data.keySet();
        String[] strings = new String[keySet.size()];
        String[] keyArray = keySet.toArray(strings);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        return SecurityUtil.getHMACSHA256(sb.toString(), key);
    }

    private CloseableHttpClient getClient() {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                new ByteArrayInputStream(ConfigConstants.wxlite_privateKey.getConstant().getBytes()));
        X509Certificate wechatpayCertificate = PemUtil.loadCertificate(
                new ByteArrayInputStream(ConfigConstants.wxlitePlatformCertificate.getConstant().getBytes()));
        ArrayList<X509Certificate> listCertificates = new ArrayList<>();
        listCertificates.add(wechatpayCertificate);
        return WechatPayHttpClientBuilder.create()
                .withMerchant(mch_id, mchSerialNo, merchantPrivateKey)
                .withWechatpay(listCertificates)
                .build();
    }

    public String decryptAes256ToString(String associated_data, String ciphertext, String nonce) throws GeneralSecurityException, IOException {
        //异常java.security.InvalidKeyException:illegal Key Size: jdk9以下需要手动下载补丁
        Security.setProperty("crypto.policy", "unlimited");
        AesUtil aesUtil = new AesUtil(key.getBytes());
        //获得解密字符串
        return aesUtil.decryptToString(associated_data.getBytes(), nonce.getBytes(), ciphertext);
    }


    /**
     * 获取微信支付平台证书
     * 现有证书2024年过期
     *
     * @return
     */
    public String certificates() {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                new ByteArrayInputStream(ConfigConstants.wxlite_privateKey.getConstant().getBytes()));
        HttpUriRequest request = RequestBuilder.get("https://api.mch.weixin.qq.com/v3/certificates")
                .addHeader("Accept", "application/json").build();
        try (CloseableHttpClient client = WechatPayHttpClientBuilder.create()
                .withMerchant(mch_id, mchSerialNo, merchantPrivateKey)
                .withValidator(response -> true)
                .build();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject res = JSON.parseObject(jsonStr);
            JSONObject data0 = res.getJSONArray("data").getJSONObject(0);
            JSONObject encryptCertificate = data0.getJSONObject("encrypt_certificate");
            String algorithm = encryptCertificate.getString("algorithm");
            String nonce = encryptCertificate.getString("nonce");
            String associatedData = encryptCertificate.getString("associated_data");
            String ciphertext = encryptCertificate.getString("ciphertext");
            String decryptToString = decryptAes256ToString(associatedData, ciphertext, nonce);
            log.debug("微信支付平台证书:{}", decryptToString);
            return decryptToString;
        } catch (GeneralSecurityException | IOException e) {
            log.error("微信支付分接口请求异常", e);
            return null;
        }
    }

    /**
     * 查询用户是否可使用服务
     *
     * @param appId
     * @param openId
     * @return
     */
    //@CatAnnotation
    public ResponseDTO<Boolean> userServiceState(String appId, String openId) {
        HttpUriRequest request = RequestBuilder.get("https://api.mch.weixin.qq.com/v3/payscore/user-service-state")
                .addHeader("Accept", "application/json")
                .addParameter("service_id", service_id)
                .addParameter("appid", appId)
                .addParameter("openid", openId).build();
        try (CloseableHttpClient client = this.getClient();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("微信支付分签约查询接口返回:{}", jsonStr);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("微信支付分接口返回异常:{}", jsonStr);
                return new ResponseDTO<>(400, "接口请求异常");
            }
            String useServiceState = JSON.parseObject(jsonStr).getString("use_service_state");
            if ("AVAILABLE".equals(useServiceState)) {
                return new ResponseDTO<>(200, "用户已经开启过微信支付分服务", true);
            } else {
                return new ResponseDTO<>(200, "未开启微信支付分服务", false);
            }
        } catch (IOException e) {
            log.error("微信支付分服务是否可用状态接口请求异常", e);
            return new ResponseDTO<>(400, "接口请求异常");
        }
    }

    //@CatAnnotation
    public ResponseDTO<WxscoreOrder> queryByOrderNo(String outOrderNo, String appId) {

        HttpUriRequest request = RequestBuilder.get("https://api.mch.weixin.qq.com/v3/payscore/payafter-orders")
                .addHeader("Accept", "application/json")
                .addParameter("service_id", service_id)
                .addParameter("out_order_no", outOrderNo)
                .addParameter("appid", appId)
                .build();

        try (CloseableHttpClient client = this.getClient();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("查询微信支付分订单详情返回:{}", jsonStr);
            JSONObject json = JSON.parseObject(jsonStr);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("查询微信支付分订单详情接口返回异常:{}", jsonStr);
                return new ResponseDTO<>(400, json.getString("message"));
            }
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            String finish_ticket = jsonObject.getString("finish_ticket");
            log.debug("完结订单时所需凭证:{}", finish_ticket);
            WxscoreOrder wxscoreOrder = JSON.parseObject(jsonStr, WxscoreOrder.class);
            return new ResponseDTO<>(200, "查询到微信支付分订单详情", wxscoreOrder);
        } catch (IOException e) {
            log.error("查询微信支付分订单接口请求异常", e);
            return new ResponseDTO<>(400, "接口请求异常");
        }
    }

    /**
     * 创建免确认订单;
     * 1.购买了骑行卡租车时不创建免密支付
     *
     * @param param
     * @return out_order_no
     */
    public ResponseDTO<String> startOrder(StartOrderParam param) {
        Map<String, Object> fee = new ImmutableMap.Builder<String, Object>()
                .put("fee_name", rent_fee_name)
                //.put("fee_count", 1) //不填默认为1;填了query时返回为1非null,完结订单不能传此参数
                .put("fee_desc", param.getFeeDesc())
                .build();

        String attach;
        try {
            attach = URLEncoder.encode(JSON.toJSONString(new ImmutableMap.Builder<String, Object>()
                    .put("userId", param.getUserId())
                    .put("rideRecordId", param.getRideRecordId())
                    .put("agentId", param.getAgentId())
                    .build()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("微信支付分接口请求异常", e);
            return new ResponseDTO<>(400, "接口请求异常");
        }

        String outOrderNo = out_order_no();
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("appid", param.getAppId());
        jsonData.put("out_order_no", outOrderNo);
        jsonData.put("service_id", service_id);
        jsonData.put("service_start_time", "OnAccept"); //用户确认时间,我们为免确认
        jsonData.put("service_start_location", service_location);
        jsonData.put("service_introduction", service_introduction);
        jsonData.put("fees", Lists.newArrayList(fee));
        jsonData.put("risk_amount", risk_amount);
        jsonData.put("need_user_confirm", false);
        jsonData.put("openid", param.getOpenId());
        jsonData.put("attach", attach);

        HttpUriRequest request = RequestBuilder.post("https://api.mch.weixin.qq.com/v3/payscore/payafter-orders")
                .addHeader("Accept", "application/json")
                .setEntity(new StringEntity(JSON.toJSONString(jsonData), ContentType.APPLICATION_JSON))
                .build();
        try (CloseableHttpClient client = this.getClient();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("微信支付分创建订单接口返回:{}", jsonStr);
            JSONObject json = JSON.parseObject(jsonStr);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("微信支付分创建订单接口第一次返回异常:{}", jsonStr);
                //再次创建订单
                if ("INVALID_REQUEST".equals(json.get("code"))) {
                    outOrderNo = out_order_no();
                    jsonData.put("out_order_no", outOrderNo);
                    jsonData.put("risk_amount", retry_risk_amount);
                    request = RequestBuilder.post("https://api.mch.weixin.qq.com/v3/payscore/payafter-orders")
                            .addHeader("Accept", "application/json")
                            .setEntity(new StringEntity(JSON.toJSONString(jsonData), ContentType.APPLICATION_JSON))
                            .build();

                    try (CloseableHttpResponse reResponse = client.execute(request)) {
                        HttpEntity entity0 = reResponse.getEntity();
                        String jsonStr0 = EntityUtils.toString(entity0);
                        log.debug("微信支付分第二次创建订单接口返回:{}", jsonStr0);
                        JSONObject json0 = JSON.parseObject(jsonStr0);
                        if (reResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                            log.error("微信支付分创建订单接口第二次返回异常:{}", jsonStr0);
                            return new ResponseDTO<>(400, json0.getString("message"));
                        }
                        return new ResponseDTO<>(200, "成功获取微信支付分创建订单", outOrderNo);
                    }
                }
                return new ResponseDTO<>(400, json.getString("message"));
            }
            return new ResponseDTO<>(200, "成功获取微信支付分创建订单", outOrderNo);
        } catch (IOException e) {
            log.error("微信支付分接口请求异常", e);
            return new ResponseDTO<>(400, "接口请求异常");
        }
    }

    public WxscoreService.WxscoreUseRes buildUseRes(JSONObject json) {
        String orderId = json.getString("order_id");
        String outOrderNo = json.getString("out_order_no");
        String aPackage = json.getString("package");
        String timestamp = timestamp();
        String nonceStr = nonceStr();
        Map<String, String> data = new ImmutableMap.Builder<String, String>()
                .put("mch_id", mch_id)
                .put("package", aPackage)
                .put("timestamp", timestamp)
                .put("nonce_str", nonceStr)
                .put("sign_type", sign_type)
                .build();
        String sign = sign(data);

        return new WxscoreService.WxscoreUseRes().setMch_id(mch_id).setNonce_str(nonceStr)
                .setSign(sign).setPackageStr(aPackage).setSign_type(sign_type).setTimestamp(timestamp);
    }

    //@CatAnnotation
    public ResponseDTO<Void> completeOrder(CompleteOrderParam param) {
        //费用说明必须为空
        int feeTotal = 0;
        for (WxscoreOrder.Fee fee : param.getFees()) {
            fee.setFee_desc(null);
            fee.setFee_count(null);
            if (fee.getFee_amount() != null) feeTotal += fee.getFee_amount();
        }
        //后优惠金额不能为空
        int discountTotal = 0;
        for (WxscoreOrder.Discount discount : param.getDiscounts()) {
            if (discount.getDiscount_amount() == null) {
                discount.setDiscount_amount(0);
            }
            discountTotal += discount.getDiscount_amount();
        }

        log.debug("费用总金额:{},优惠总金额:{}", feeTotal, discountTotal);

        long nowTime = System.currentTimeMillis();
        if (nowTime < param.getRealStartTime() + 1000) {
            nowTime = param.getRealStartTime() + 1000;//加1秒
        }
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("appid", param.getAppId());
        jsonData.put("service_id", service_id);
        jsonData.put("finish_type", 2);
        jsonData.put("finish_ticket", param.getFinishTicket());
        jsonData.put("fees", param.getFees());
        jsonData.put("discounts", param.getDiscounts());
        jsonData.put("real_service_end_time", FormatUtil.yyyyMMddHHmmss.format(new Date(nowTime)));
        jsonData.put("total_amount", feeTotal - discountTotal); //一定要有
        //实际开始时间就是用户扫码时间,不需要特殊计算
        if (param.getRealStartTime() > 0) {
            jsonData.put("real_service_start_time", FormatUtil.yyyyMMddHHmmss.format(new Date(param.getRealStartTime())));
        }

        HttpUriRequest request = RequestBuilder.post("https://api.mch.weixin.qq.com/v3/payscore/payafter-orders/" + param.getOutOrderNo() + "/complete")
                .addHeader("Accept", "application/json")
                .setEntity(new StringEntity(JSON.toJSONString(jsonData), ContentType.APPLICATION_JSON))
                .build();
        try (CloseableHttpClient client = this.getClient();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("微信支付分完结订单接口返回:{}", jsonStr);
            JSONObject json = JSON.parseObject(jsonStr);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("微信支付分完结订单接口返回异常:{}", jsonStr);
                return new ResponseDTO<>(400, json.getString("message"));
            }
            return new ResponseDTO<>(200, "成功获取微信支付分创建订单");
        } catch (IOException e) {
            log.error("微信支付分完结订单接口请求异常", e);
            return new ResponseDTO<>(400, "接口请求异常");
        }
    }

    public ResponseDTO<Void> syncOrder(String outOrderNo, String appId) {
        //服务开始时间＜商户完结订单时间＜用户实际付款成功时间＜商户使用收款成功信息同步能力的时间
        String paid_time = FormatUtil.yyyyMMddHHmmss.format(new Date(System.currentTimeMillis() - 2000));
        Map<String, String> detail = ImmutableMap.of("paid_time", paid_time);

        Map<String, Object> jsonData = new ImmutableMap.Builder<String, Object>()
                .put("appid", appId)
                .put("service_id", service_id)
                .put("type", "Order_Paid")
                .put("detail", detail)
                .build();

        HttpUriRequest request = RequestBuilder.post("https://api.mch.weixin.qq.com/v3/payscore/payafter-orders/" + outOrderNo + "/sync")
                .addHeader("Accept", "application/json")
                .setEntity(new StringEntity(JSON.toJSONString(jsonData), ContentType.APPLICATION_JSON))
                .build();

        try (CloseableHttpClient client = this.getClient();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("微信支付分订单同步接口返回:{}", jsonStr);
            JSONObject json = JSON.parseObject(jsonStr);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("微信支付分完结订单接口返回异常:{}", response);
                return new ResponseDTO<>(400, json.getString("message"));
            }
            return new ResponseDTO<>(200, "成功支付分订单同步");
        } catch (IOException e) {
            log.error("微信支付分完结订单接口请求异常", e);
            return new ResponseDTO<>(400, "接口请求异常");
        }
    }

    public WxscoreService.WxscoreEnableRes wxpayScoreEnable() {
        String outRequestNo = out_request_no();
        String timestamp = timestamp();
        String nonceStr = nonceStr();

        Map<String, String> data = new ImmutableMap.Builder<String, String>()
                .put("mch_id", mch_id)
                .put("service_id", service_id)
                .put("out_request_no", outRequestNo)
                .put("timestamp", timestamp)
                .put("nonce_str", nonceStr)
                .put("sign_type", sign_type)
                .build();
        String sign = sign(data);
        return new WxscoreService.WxscoreEnableRes().setMch_id(mch_id).setService_id(service_id).setOut_request_no(outRequestNo)
                .setTimestamp(timestamp).setNonce_str(nonceStr).setSign_type(sign_type).setSign(sign);
    }

    public WxscoreService.WxscoreDetailRes wxscoreDetail(String out_order_no) {
        String timestamp = timestamp();
        String nonceStr = nonceStr();
        Map<String, String> data = new ImmutableMap.Builder<String, String>()
                .put("mch_id", mch_id)
                .put("service_id", service_id)
                .put("out_order_no", out_order_no)
                .put("timestamp", timestamp)
                .put("nonce_str", nonceStr)
                .put("sign_type", sign_type)
                .build();
        String sign = sign(data);
        return new WxscoreService.WxscoreDetailRes().setMch_id(mch_id).setService_id(service_id).setOut_order_no(out_order_no)
                .setTimestamp(timestamp).setNonce_str(nonceStr).setSign_type(sign_type).setSign(sign);
    }

}
