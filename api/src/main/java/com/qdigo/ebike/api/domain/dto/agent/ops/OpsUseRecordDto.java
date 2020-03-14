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

package com.qdigo.ebike.api.domain.dto.agent.ops;

import lombok.Data;

import java.util.Date;

/**
 * description: 
 *
 * date: 2020/3/14 5:09 PM
 * @author niezhao
 */
@Data
public class OpsUseRecordDto {
    private long id;
    private String opsUser;
    private String imei;
    private String useStatus;
    private Date startTime;
    private Date endTime;

}
