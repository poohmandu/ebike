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

import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.third.service.wxlite.WxlitePush;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/1/20.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/wxlite")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WxliteRest {

    private final WxlitePush wxlitePush;

    @PostMapping(value = "/pushFormId", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> pushFormId(String mobileNo, String formId) {
        wxlitePush.saveFormId(mobileNo, formId);
        return R.ok(200, "保存表单id");
    }

}

