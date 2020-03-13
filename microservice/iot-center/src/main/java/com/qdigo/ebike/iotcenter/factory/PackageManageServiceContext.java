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

package com.qdigo.ebike.iotcenter.factory;

import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg.*;

/**
 * Description: 
 * date: 2020/2/22 10:40 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Component
public class PackageManageServiceContext {

    @Resource
    private Map<String, PackageManageStrateyg> strategys = new ConcurrentHashMap<>();

    public PackageManageStrateyg getParsePackageStrategy(char header0, char header1) {
        if ('P' == header0 && 'L' == header1) {
            return strategys.get(PLStratrgy);
        } else if ('P' == header0 && 'G' == header1) {
            return strategys.get(PGStratrgy);
        } else if ('P' == header0 && 'H' == header1) {
            return strategys.get(PHStratrgy);
        } else if ('P' == header0 && 'C' == header1) {
            return strategys.get(PCStratrgy);
        } else if ('P' == header0 && 'X' == header1) {
            return strategys.get(PCStratrgy);
        } else if ('M' == header0 && 'D' == header1) {
            return strategys.get(MDStratrgy);
        } else if ('M' == header0 && 'L' == header1) {
            return strategys.get(MLStratrgy);
        } else if ('M' == header0 && 'C' == header1) {
            return strategys.get(MCStratrgy);
        } else if ('M' == header0 && 'X' == header1) {
            return strategys.get(MCStratrgy);
        } else {
            String errorMsg = MessageFormat.format("header0={0},header1={1}", header0, header1);
            throw new IotServiceBizException(IotServiceExceptionEnum.NOT_SUPPORT_SOCKET_DATA.getCode(), IotServiceExceptionEnum.NOT_SUPPORT_SOCKET_DATA.getMsg() + errorMsg);
        }
    }

}
