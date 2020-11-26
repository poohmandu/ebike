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

package com.qdigo.ebike.controlcenter.config;

import org.springframework.web.context.request.async.CallableProcessingInterceptor;

//rebuild
public class CallableProcessInterceptor implements CallableProcessingInterceptor {

    //@Override
    //public <T> void preProcess(NativeWebRequest request, Callable<T> task) {
    //    WebLogAspect.ctxInit();
    //}
    //
    //@Override
    //public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) {
    //    try {
    //        if (concurrentResult instanceof Exception) {
    //            Exception e = (Exception) concurrentResult;
    //            WebLogAspect.throwing(e);
    //        } else {
    //            WebLogAspect.returning(concurrentResult);
    //        }
    //    } finally {
    //        WebLogAspect.ctxClear();
    //    }
    //}

}
