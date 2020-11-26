/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.common.core.constants;

/**
 * Description:
 * date: 2019/12/11 5:20 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
public enum ConfigConstants {

    imei("8607200", "上行的车辆的imei号为8位,需补前7位"),

    validity_period("30", "登录的有效期 (天)"),

    //ping++
    subject("电滴出行", "订单里的支付标题"),
    publicKey("-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy6EnKCOh7mqmeZsTOxFL\n" +
            "r41MNGDrzYAafMnNWsHaP8jfsaRwheXSXh3PL1pYewtj1VktBMBMwKHyHwLOfppy\n" +
            "BJwttSUNQ7ndhl0LefBVrZFJ/auWNrc4GOxNzXb2VT0h0aiHRdVKItKanlyhE/1Q\n" +
            "K5bjES8zITV5MVb3pa0bNgcU4UaW+Csfr+jKJ063Hp/LueITnK2fxncsDS4BnuZJ\n" +
            "MloOVr9ejU2Tn+CU5I5NdcM+L6wQ58vBnEwLvmnZjoW++bYyjyfzl5I415xaVFhr\n" +
            "Pnwen82lD6kFPghw0zbQdO7e61wE1tgW6l4kcOCS0t1zRWiMctlf3QxJljFGa+Zd\n" +
            "2QIDAQAB\n" +
            "-----END PUBLIC KEY-----\n", "回调时验签的公钥"),
    apiKey("sk_live_jLS44STePeD0v58u5K1uv54K", "Pingpp 管理平台对应的 API Key"),
    pingppTestKey("sk_test_TqbDaPPufXvDu940485KOOu1", "用于测试"),
    appId("app_OqDG8KyTWD8O9eH4", "Pingpp 管理平台对应的应用 ID"),

    //push
    masterSecret("7df2bfc9eb46a5cbd9f9388d", "极光推送密钥"),
    appKey("d971ef8deba40174bbfbe1c7", "极光推送 appkey"),
    tailg_jpush_masterSecret("1ef3efa5cb0b2d1a381a4176", "台铃云端-极光推送密钥"),
    tailg_jpush_appkey("db9d6fd5e947fd71ddb0b092", "台铃云端 "),
    ops_jpush_secret("c73bb555c44046b2d69c58ed", "管理员app极光推送"),
    ops_jpush_appkey("21af5006a657df8a539c17f9", "管理员app appkey"),

    //map
    iotKey("464de80d17a44e7af5c438e8bf72f3fe", "高德智能硬件的key"),
    amap_server("2f40ffc069792c4dc6373857ef741b62", "高德-电滴出行-服务端-key"),
    amap_wxliteServer("dd08770697ee35dbd5ca72a85202acb9", "高德-微信小程序使用server而不是wxilte"),
    amap_fence("e7b15dc23a01807aff6fc84d493d65e4", "供电滴出行电子围栏的使用"),
    //BDMap
    ak("GRV0vXq9WDhXWQiwCGzpEvIBVeEclDkG", "百度地图的AK"),
    yingyanServiceId("157308", "鹰眼服务id"),

    //wxlite
    wxlite_appId("wx8b40d25493fc47e0", "微信小程序appid"),
    wxlite_appSecret("827d18f28eaac51868f68c46c72f0faa", "微信小程序app密钥"),
    wxlite_privateKey("-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDG6oDOqbCT5sWF\n" +
            "Cb9RDLBRqa6XIBvnbl+JjnXfzfaQh0WrVXgp84wBnyIOSqeTaS009VZRw/6VIqOF\n" +
            "uW8TlCFSZw5t5hBqqyQG+p494gMVwBvYHA7smPJ9CIa71Ksp35CdS6OOUd8HcLfG\n" +
            "UrJL7yrju4VWoy9v0srJNfPLqKO9exXoIw/zXIFQMth3DzBcmnVtikAiaynMQIeP\n" +
            "dYp/bsjM3XG+KiCpt4jZNzOqCeuiZnwEgEZb3z+J/pF20gvvYVhM/PE+yfmQyzrw\n" +
            "8VaNCPIIZrda5biqSKbT++QarQ1bv5pDW0MVJk6Ursx6nRkhI9S76reAZ643bdLg\n" +
            "0MzlgJUDAgMBAAECggEABWyBhKEV4oKJYj5IzDH3YNKqow5KFH5q9no+9pJMJda6\n" +
            "bJiRBTvR1n17VT5t4VQd2nLSHrqkZ3ahVNUglT0Vx4Rt3UtHqVDvU41j4TYXdXFe\n" +
            "kP763TKycfxYiCidXi/tZoyYchoDZdWJ/Utl9zIXTkxfr2QVuAvcYa4p7qSzikgT\n" +
            "lMK+v6F27CbxGQzn53LjRY79AVyqErzEPX77DIj2p/+mWLxO73uyslCX2jvCC8Or\n" +
            "QoHUcCAx3/lPbbmTWjqs54oK5BJNckaVQ8FGQsBqcol44DuKDz8kP0smESjmPaZx\n" +
            "yBW6uBvWplSx41xfbC16LTXGZJNIZsbNhSUHcf2ccQKBgQDwz/h4DNq/IAvTPw7T\n" +
            "REn5hM9Wtn8Waw4DNodKUMpAq7LYZkekAtezDkNAcbENXsh5VJOdkn/daO/Z60LG\n" +
            "bgA/VmpRgfa+uoAh1NOPc/B4XPEieBy00LS6PFz6xANRWkeSgCqj1YM2CGJuHoJY\n" +
            "T/iKrZH4ONn19SHioj8t+JIiiQKBgQDTdhidAPqWjLc+hcoTaP9w0HuC8Vd0vJ4/\n" +
            "1T74MYGMuB2DXqrOn+hC8+IE3ZP+R2H7f8b9md0pqWlXRYGmqVgNnXl+Dk+AqeEn\n" +
            "YxgwPqzcYWgzFXBMFQAn1pjX0ScG2bBEtsvwyPDGlLN7btVyqcmuPMTR2kuJ1dJq\n" +
            "BS/6dryIKwKBgBhpvRLOms6NfSVpWyNn5Of64tozniazq7P/ry+FiTuNLJBrrg+e\n" +
            "iieI5qB95iko1sIWJPDwjS3xGX/KLfbX6AaDyDuDJXSoi5ziaqA8bMCHC5sm+iIK\n" +
            "lofa3sI9ZOi2clMT0z7QwoCT0QbA6Q3y7YuBlS7I9K/OriS/tg//BzGhAoGAb99a\n" +
            "xjVuXZe0mm9NqVczTQv/TBdCnY/saPj4h3Ypg7kkZAwBca9HwaQlP026rhw1ykif\n" +
            "jiuIxjHKeVFcdj/BRfK6A+auSPOXGq9ibKO78BvYdK4y6w1xN6Jg2kzcyNMCXlgX\n" +
            "LRwhxe2Fxxer9S2+dV/RAPTsYc+tsklsSKXouGECgYEAsLDkr5mPH3E0ACGqNBvx\n" +
            "IEqsS3dKlyqhmrJddOkV5UgKk2AHNIrk5asBeRY50mqzrk8WK0VtY2czvSAiTc7l\n" +
            "RsORAWEY6Ztjb2/oZcLtlDbkooNMAyZraiS7vLJmMD/CBqzX6IiOM68Gh5JCabbN\n" +
            "I/ag0qlhXzie0FsQKNNdUOc=\n" +
            "-----END PRIVATE KEY-----", "微信支付私钥"),
    wxlitePlatformCertificate("-----BEGIN CERTIFICATE-----\n" +
            "MIID3DCCAsSgAwIBAgIUa7Uqu9PmUWgfLkJ2aV9nC04fI9QwDQYJKoZIhvcNAQEL\n" +
            "BQAwXjELMAkGA1UEBhMCQ04xEzARBgNVBAoTClRlbnBheS5jb20xHTAbBgNVBAsT\n" +
            "FFRlbnBheS5jb20gQ0EgQ2VudGVyMRswGQYDVQQDExJUZW5wYXkuY29tIFJvb3Qg\n" +
            "Q0EwHhcNMTkwNjI4MDgyMzI2WhcNMjQwNjI2MDgyMzI2WjBuMRgwFgYDVQQDDA9U\n" +
            "ZW5wYXkuY29tIHNpZ24xEzARBgNVBAoMClRlbnBheS5jb20xHTAbBgNVBAsMFFRl\n" +
            "bnBheS5jb20gQ0EgQ2VudGVyMQswCQYDVQQGDAJDTjERMA8GA1UEBwwIU2hlblpo\n" +
            "ZW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDTfKQcEOLGSDftK5/y\n" +
            "aq3PFHPh5TMmPXORWd657QcSOOepMg2C6MKYPitkf6n4or/eJH0WhtfnMjNl6lF2\n" +
            "oMudsbYDpOgqOPjuYn9kDM+L7SXix9pTUhohh4MJP5Zrw7vsw4ax1lRgARbpILyX\n" +
            "dNHl3EnkqYgLdZwiDCG8l3SyLtImOO4wldPkTiwPmeiP5dm/SmUMcfbeAFUNwU3R\n" +
            "uEV5EYm2+136+xNyPDQDF5sf9rsCdzbV74GALSkc/+XwxUtVCp+6G3clDeo9c4xG\n" +
            "v9uyYcWgqZDgf9jOZVabHmpThpJYD5FK5+Lyka0DUHihem8bX9JYtf0XlFZUvfra\n" +
            "HV4XAgMBAAGjgYEwfzAJBgNVHRMEAjAAMAsGA1UdDwQEAwIE8DBlBgNVHR8EXjBc\n" +
            "MFqgWKBWhlRodHRwOi8vZXZjYS5pdHJ1cy5jb20uY24vcHVibGljL2l0cnVzY3Js\n" +
            "P0NBPTFCRDQyMjBFNTBEQkMwNEIwNkFEMzk3NTQ5ODQ2QzAxQzNFOEVCRDIwDQYJ\n" +
            "KoZIhvcNAQELBQADggEBAJ7M+LP48vSlJWKf6kU7c1Yh2gTro5Mpj0BcoW0f+YlT\n" +
            "WhQuo8hxAWACGlH5KYETvFMn88kbgeuLuDsMZz7zMiy9pCqW0tr8oAkyBTYJFopY\n" +
            "W2hM0t7cQfwjnxw2/YlgeBHwzUqizoSQX5lf4gd095VWOV6VP5LObO2acHk2bXjw\n" +
            "2gxSdRmBGrwQsfgPDcI5D6sPNryKYc5+flk9xhm8yAQ4mWfTF0CevER0Wz/2qNuW\n" +
            "rpE8kZ0nLfanAKtFQD3hC20KQqEp7kO8ecD/c8imB6NjchEjjDNLfTY6EL0eILfh\n" +
            "wZPL9CC2k2LI8degmuBqi1JlBV71OzoKDHerMfo1bqI=\n" +
            "-----END CERTIFICATE-----",
            "平台证书:必须通过接口获取,每5年需要更换;此证书失效时间为:2024-06-26T16:23:26+08:00"),

    //alipay Credit
    alipayAppId("2017022305838054", "芝麻信用用到的阿里的app id"),
    alipayAppPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDBkfdfMkieNdZKl2lQuE0qTj48FmpNbpA/3uNHDYVyGyYsX/x1DW5Vs6n6ZyVuBy4TAKHzeS964eMLthVktnY1ck895tzZ115ADNQqRKGIwQXY7RGvYX6pkzab3BW7fDxh5TzaF20Cr2IjdjQT2umFHAaY4Q/GY4xYTawCtqpGWuc4FeO5+33nZyqnSGw3pAI27XpjS4KepvN5667R4DOqZipfTY/m6pKHDCvsP3c3lk3BUK5MeV7aFxJp+bdJ/MudM44/ZyVn8x/njEEhWMspEmg76l/9g0yG8QlxbV5s833k6/bYZNdElGg/KiPycg1GPKSrTTm/7KvIdhRDR48nAgMBAAECggEAVDvXwPZZwXc+HFGNQ9IfykQoMu+yLKXrYc/1aUsKAiCsWO0gKDPDCspXMp9GG35GX4CL+S6IKdo4ejUvVBdOk2197DsVajqdShQLE6WwAZm1mLN5Wa98dgCY5/Q7BM5IXHVsKJ+/EUcO1s2uVeGScxlCCa8OtU9GmHqcWsXwjRJqAGCdukll3Cmwu8haGywkAKs0ZRgMBvu7sAdRenSat3psbI6el3zZbyxjEcW+G25U9V4kmKlOKsXwbNVWYxY4kyMX5Ko78a+AMWl1RLYt2NDomLGo6olcHv5pltZI8IJj5DyeOG898sMD6E0IqBsdghcKLgCxhjoeJziiPgPymQKBgQDhUPDeqc87NGF6kM2H42yAPIqO16RscPvt5fDHZuI5n9kWijs1u6FqRKTjjRBH11LkVLQRTMxLBavxq9ToP1D1S2txm/jy2BvpleYSApZyjs3oyabSB2UZLJ30T7U8pskUObKPgDXumADx/3swh6BFv4/K0opBMZf84Fui9Aor2wKBgQDb7kjKQlmph1sQYhaNzNBCPzBvk+g1uLBWvIkmSoqZv/bdtp03hyWjWDV6o3zsIbhmD4+NhmnvW47snlC6iR4J9TgjUa467b1TO6IMfMZTnubQ0Ant2KebRTBrTbkaeBXaYXpadCrwFrI6LOVro00sZt3cLiNR0+SHHGSlnltRpQKBgEw0CNL10X2vkIxYbvrXgvm4ABChhB2c9MeX7iAyfq6Ijm5eH/pjVAtR3gaLzgbOH13T+/ah57Iz39xjhiVqTKI5eMQ6wS64wm/035QVxfZGsANcOAxurdWezmkzkBNMQVOS0/5OmW7xf9hY1LwsEpukVyh8nn/AGyxOYQ+yw8HjAoGBANmQn/F2MbzDahKK9kTQmDTwzbmTV+PNEKS9Xe/DTdlLTauHO3/y8gjk+gKYOLxfn0tXmWsnzSQ8LudktffSLSUssJw/8VvaU7BDDcvMo2WVjECFjUz9RtETYRLFkJfes7+VLrTMtq1LxoRGZa5VncKPAGsQOWB5fVkrfWVZgn0BAoGBAJfTZYIeK34rLM+6Xga7nS+ppAvY6na5zu8hvdDYurSlnBsQGGnp/+ox+2WVYAQYYE29F4vXZD/2vGU1CiBHgn/sx2FcW+P7V71/96u+i2SA4Rkujnw5eYuID9XcgZjihCx1ksEbAaZ6/xy92BeyZmqUM0OB7UZNwbg8LLTCVxi6",
            "应用私钥"),
    alipayPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
            "支付宝公钥"),

    //支付宝小程序
    alipayliteAppId("2019050664363993", "支付宝小程序将来需要用到"),
    alipaylitePrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8YVUgyIQ+mi1S\n" +
            "QCjyg5tfJ78nTtCjDBWIJUFJpsvcbCpB36IRRDQVlL7r5urUtxkk8ev/z9BsZpVE\n" +
            "62EBJi7aWhljEV5MCGh8ZWeqx5tSk3cRwffm5IW4SLJkOOqYJs3b1IjWyIyDAQB0\n" +
            "OQTDeOFdpCoAQVeudTMGQnvc4JsNj7YoUmEhjTYdeZOOJupAKKPsZpRTKojLri4D\n" +
            "3kgBMPH4zfcFZCnTjNwaJSSG6hY8UK+iP8Zne1uBjH3dZjAIzVc16nVOlEQOCEMs\n" +
            "XDBLWVYqyK5vOX18vQYe0E2DygiCep7h7KJfZb8TugZMP1a/iw1uI/vyKuh7pEzK\n" +
            "cPuKyP5xAgMBAAECggEAF+dR4gMPpNHzhkhV6dABWyWz/8zkdIwH/2qayjm8DJA9\n" +
            "HHbAHejFoydowo5epfdF3YZFKAS6scaikkg72uV5dIOSTRvbF1LJz18etHD3qJbF\n" +
            "uR8f0dCVlzjCSi1et0fVP1vMsVRpYAY0hIrnq9Ye4r7+BWJFOgHtI5I0HWbyxdmK\n" +
            "xf6nYxTUYQQsutSdVm1WCLRYFn+5hDGLJI2ckFyYyyQTVC/zt5/3Q3UrV1tIzh3Q\n" +
            "9Xg0Hk9+ssVHVJG6Em1+SM8w7/rmhhfpO0o4Ys+0In6CQ45wNhgXvy/lvysnSahY\n" +
            "kDgHVF4TJhdtel/5c7Hjwm3F0SoQOShj6/E/NVgoMQKBgQDiSvTjuGZGJuamnIs+\n" +
            "SEh8emPhXJo8oBfe3bCKlt4gUgiDVeKE0558KOe/hNc9A0OnCowJVZ+RFrhv/JLB\n" +
            "ZS7VtCn4eIsDXF0E02jSuaFzLRUszxaD+y5EIiQi0U7F0Tfukj9y1SSKl7QhQK5b\n" +
            "qjnHWaQ3HcJ2LsRR+oHE/0440wKBgQDVHD5qw4cACYUUn83n9w3yT5UVYm/KBJT5\n" +
            "oMyNFdD/d1ZOnQ7idSqfYAyL5yYtCRH8yyMgu41SabiHueHhhTfJ1woxQYn53DL1\n" +
            "2EpPQPAxPOlqqJyEYyI32bZ7QcjCuqFP4NX1KU2oGqqKfBFqxyV0Pw6ZZThB7oik\n" +
            "m1G/g/zhKwKBgHsoeGeHcII1ocqD4HShOnIk2j9I+tiL+PHejbqfqXzFqMRx9bp8\n" +
            "wgCo8pgVYF9ga216HAToiNOaHf8041pGC1tblowYbvABUM7TkHU/elMI8kFU49go\n" +
            "2GFUg34/lDOtTleVWNrSjfSv4+VFIp/Y4WFRtEIa/D797PMA96eTm6rpAoGAbZPi\n" +
            "PD9hkRFUDnDFDTspPqjQ/XfqBzQn7dtSklDfcBxKko2Lc8HPMrfOe7lVFd8OEq/Y\n" +
            "1iHZ3sBhk9huXumC8BPUN9N9QK6KvYKDYxD+8DJjyuX75M6cb59QhzwiII6aF1qC\n" +
            "aVbdiUx82ExHIGJZzdYGibVUIgKm3AzJEfKp+pUCgYEAuGu+buoKs4G7ehaT/67g\n" +
            "tjNi4dWXysseeuFuiu9aTE54MbMtMWGBIyFn04oxDtlFDzLiaNT8DIGJdjNf00+V\n" +
            "4TKC4rYXxn8CceWNsYeivr0ETIXf9Bp9PLNEYWvjiAHGnXyczl12sqiQaeeopfWb\n" +
            "9lzmFIcqLHMuPz5Zda3IbAM=",
            "小程序应用私钥"),
    alipaylitePublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApKL0hIfkxZM+MSdoPY6ov0ea9NXIikB8g/BoSGVRGy3Y/QkuOUkJM8LTefm1AnFyDpNvvmM5YVil/ljY+XS27LnLE7Xc98tycmXh3iBxX/VTeUgX2OZNw9Xhq1KfNzJblgUEXTqcImsjoTBW0yq8OKxRqxOkwrwkayEQkXeX1XKpyPFYr+ZmqKe0XG1K+ZwxYUsysYuImQy7FaNl7l+lmmDKkatrUgvlvaNY3NPpLFSE3wAB8sGswYaGOkMD261UzTc2BEd0x318UGXN1wrId1pcXG8xxtPa+96mTeCyQzHKGKXlnAxzSRdw+1vn45yDMA2UYLUhIKuKIBfbuuUkZQIDAQAB", "" +
            "小程序应用公钥"),

    //芝麻信用
    sesameAppId("1001885", "应用id"),
    sesamePrivateKey("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAN2XCsk6TSeKLtuNleuQJqRIictl94aMKLHkvugQL7IO3ehvc4R4MQPQZFqkLn7uHxm3cLr2BM7X3C7sFSE6sCkRabjRgQHGf4G/r2RbTADe71dSZw+cslszIVWAxfAtpbIfsCvdxEaJqlnnESg/ygwllF73xf9k7WFkw7IyfBhjAgMBAAECgYEA3FXqJblRKrXebfXVUwxdQBHY4mUbwa/wbyMzKPGfW4Ao64yW4uvYYSCACRkkGpaQWjPrZhxnH33ItOePAyGmMvVD1DsAbrFVMTI8WEeWYrR44T5NUZpFNBnc+6xHfg6qHoklK6RbTIyHoE2mRQqsfdjHgizaZ9NPV00uKSD1ZFECQQD9mn/nmNIO6QkASAgE7wwl0rjhXr/h1V+g2xWd4UcZhS9xuPNSRRpXohUWOHLZ3VrAP14u/40N79xo9WHE46DFAkEA368Y/O8F52OszTXGJ15eeRiCyeIqsb7/3WnDrh02elYO31LaMZpOgctf+NHo4WOavVDPxZM9pyAsdVaA9J0XBwJBAITgCJguKBGL5B1zoFAFeBXxPNFItbz78Wj0oXThbkFe2Sb6wvKeJlk4IVhNJ1AjfMMx8IUrfJqKzK9pslpY8gkCQQCXNredxbgoBKn80C10z8bt9jP0ZoCWSJiQBb/TJMx/R04miswWXEpWVDY87yqPs9YXxVmwJym2oXlsmV3z/35hAkEAuZynpgBm++BMF4+4X7dChJm0hplFXzCCjPsYF2pwF53H/pQ8Ex/hUCliaBqo5jdJvOFT8LVPPechU8qETg6YIw==",
            "芝麻的商户私钥,商户公钥在对方"),
    sesamePublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKsIxceYftvcnCn2yt0hrVG+aVhrsilwFcE9ynnDMYR0VoQVi2HFqGWXTGuRNLFRMPLR3NyjWKKj69AR9BDOethDA1mjW6xxZsRQBPbDWAI5p9dNOm7QlOqYwd/SNOQakbyXA5SQDP3dOTRuP+6wnrZJef5fz2muNeSfOmsbWLhQIDAQAB",
            "芝麻提供的公钥,它的私钥在对方");


    private String constant;
    private String dec;

    ConfigConstants(String constant, String dec) {
        this.constant = constant;
        this.dec = dec;
    }

    public String getConstant() {
        return constant;
    }

    public String getDec() {
        return dec;
    }


}
