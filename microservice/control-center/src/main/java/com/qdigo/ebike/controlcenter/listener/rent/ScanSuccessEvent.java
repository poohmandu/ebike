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

import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.controlcenter.domain.dto.rent.StartDto;
import org.springframework.context.ApplicationEvent;

/**
 * Created by niezhao on 2018/1/26.
 */
public class ScanSuccessEvent extends ApplicationEvent {

    private RideDto rideDto;
    private StartDto startDto;

    public ScanSuccessEvent(Object source, StartDto startDto, RideDto rideDto) {
        super(source);
        this.rideDto = rideDto;
        this.startDto = startDto;
    }

    public RideDto getRideDto() {
        return rideDto;
    }

    public StartDto getStartDto() {
        return startDto;
    }
}
