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

package com.qdigo.ebike.api.domain.dto.third.wx.wxscore;

import lombok.Data;

@Data
public class EncryptResource {

    private String algorithm;  //加密算法类型:对开启结果数据进行加密的加密算法，目前只支持AEAD_AES_256_GCM

    private String ciphertext; //数据密文:Base64编码后的开启结果数据密文

    private String associated_data; //附加数据

    private String nonce; //加密使用的随机串
}
