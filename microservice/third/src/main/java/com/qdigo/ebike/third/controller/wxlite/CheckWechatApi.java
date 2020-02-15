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

package com.qdigo.ebike.third.controller.wxlite;

import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by niezhao on 2017/2/13.
 */
@Slf4j
@Controller
@RequestMapping("/v1.0/wxlite")
public class CheckWechatApi {

    private static final String TOKEN = "niezhao666";

    @RequestMapping(value = "/checkSignature")
    public void checkSignature(String signature, String echostr, String timestamp, String nonce,
                               @RequestBody Map<String, Object> body, HttpServletResponse res) throws IOException {
        String[] str = {TOKEN, timestamp, nonce};
        Arrays.sort(str);// 字典序排序
        String bigStr = str[0] + str[1] + str[2];
        // SHA1加密
        String digest = SecurityUtil.getSHA1(bigStr);
        // 确认请求来至微信
        if (digest.equals(signature)) {
            res.getWriter().print(echostr);
        }

        Object openId = body.get("FromUserName");
        HttpPost request = new HttpPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + TOKEN);
        //创建http客户端
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("touser", (String) openId));
            params.add(new BasicNameValuePair("msgtype", "text"));
            Map<String, String> map = new HashMap<>();
            map.put("content", "后台已收到你的消息");
            params.add(new BasicNameValuePair("text", JSONObject.fromObject(map).toString()));
            //添加参数
            request.setEntity(new UrlEncodedFormEntity(params));

            //得到响应体
            HttpEntity entity = response.getEntity();
            //json 解析
            String jsonStr = EntityUtils.toString(entity);

        } catch (IOException e) {
            log.error("HttpClient发送过程中出现异常", e);
        }

    }

}
