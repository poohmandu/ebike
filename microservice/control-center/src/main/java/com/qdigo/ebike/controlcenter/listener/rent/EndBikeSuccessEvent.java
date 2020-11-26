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

package com.qdigo.ebike.controlcenter.listener.rent;

import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import org.springframework.context.ApplicationEvent;

/**
 * Created by niezhao on 2018/1/10.
 */
public class EndBikeSuccessEvent extends ApplicationEvent {

    private EndDTO endDTO;

    public EndBikeSuccessEvent(Object source, EndDTO endDTO) {
        super(source);
        this.endDTO = endDTO;
    }

    public EndDTO getEndDTO() {
        return endDTO;
    }
}
