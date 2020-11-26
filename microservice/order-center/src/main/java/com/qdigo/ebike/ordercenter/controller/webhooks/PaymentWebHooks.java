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

package com.qdigo.ebike.ordercenter.controller.webhooks;

import com.google.gson.Gson;
import com.pingplusplus.model.*;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.ordercenter.service.inner.webhooks.chargesucceed.ChargeSucceed;
import com.qdigo.ebike.ordercenter.service.inner.webhooks.RefundSucceed;
import com.qdigo.ebike.ordercenter.service.inner.webhooks.TransferCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * 接收ping++回调消息
 */
@Slf4j
@Controller
@RequestMapping("/v1.0/payment")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PaymentWebHooks {

    private final ChargeSucceed chargeSucceed;
    private final RefundSucceed refundSucceed;
    private final TransferCallback transferCallback;


    @PostMapping(value = "/webHooks")
    public ResponseEntity<R<?>> receiveWebHooks(HttpServletRequest request) {
        try {
            log.debug("开始调用回调信息");
            request.setCharacterEncoding("UTF8");
            // 获得 http body 内容
            BufferedReader reader = request.getReader();
            StringBuilder buffer = new StringBuilder();
            String string;
            while ((string = reader.readLine()) != null) {
                buffer.append(string);
            }
            reader.close();
            String body = buffer.toString();
            String sign = request.getHeader("x-pingplusplus-signature");
            if (!verifySignature(body, sign, getPubKey())) {
                log.debug("验签失败");
                return ResponseEntity.badRequest().build();
            }
            log.debug("验签成功,为pingxx返回的信息:{}", sign);
            // 解析异步通知数据
            Event event = Webhooks.eventParse(body);
            MDC.put("mobileNo", event.getId());
            log.debug("ping++回调返回的event是:{}", event);
            if ("charge.succeeded".equals(event.getType())) {
                Gson gson = Charge.GSON; //这里用了ping++ 的 gson 解析
                Charge charge = gson.fromJson(event.getData().getObject().toString(), Charge.class);
                if (!chargeSucceed.chargeSucceed(charge)) {
                    return ResponseEntity.badRequest().build();
                }
            } else if ("refund.succeeded".equals(event.getType())) {
                Gson gson = Refund.GSON;
                Refund refund = gson.fromJson(event.getData().getObject().toString(), Refund.class);
                if (!refundSucceed.refundSucceed(refund)) {
                    return ResponseEntity.badRequest().build();
                }
            } else if ("transfer.succeeded".equals(event.getType())) {
                Gson gson = Transfer.GSON;
                Transfer transfer = gson.fromJson(event.getData().getObject().toString(), Transfer.class);
                transferCallback.transferCallback(transfer, true);
            } else if ("transfer.failed".equals(event.getType())) {
                Gson gson = Transfer.GSON;
                Transfer transfer = gson.fromJson(event.getData().getObject().toString(), Transfer.class);
                transferCallback.transferCallback(transfer, false);
            } else if ("summary.daily.available".equals(event.getType())) {
                log.debug("收到ping++日汇总");
            } else {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("pingxx回调函数反生了异常:", e);
            throw new RuntimeException(e);
        }
    }

    //验签
    private static boolean verifySignature(String dataString, String signatureString, PublicKey publicKey) throws Exception {

        byte[] signatureBytes = Base64.decodeBase64(signatureString);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataString.getBytes("UTF-8"));
        return signature.verify(signatureBytes);
    }

    //获得公钥
    private static PublicKey getPubKey() throws Exception {
        String pubKeyString = ConfigConstants.publicKey.getConstant();
        pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        byte[] keyBytes = Base64.decodeBase64(pubKeyString);

        // generate public key
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }


}
