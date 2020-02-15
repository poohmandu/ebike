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

package com.qdigo.ebike.common.core.errors;

import com.qdigo.ebike.common.core.errors.exception.QdigoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
@Slf4j
public class ExceptionTranslator {

    //409
    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorVM processConcurencyError(ConcurrencyFailureException ex) {
        log.error("409服务器内部异常:请求冲突", ex);
        return new ErrorVM(ErrorConstants.ERR_CONCURRENCY_FAILURE, "请求冲突");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorVM processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        log.error("400服务器内部异常:", ex);
        return processFieldErrors(fieldErrors);
    }

    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorVM processParameterizedValidationError(CustomParameterizedException ex) {
        log.error("400服务器内部异常:", ex);
        return ex.getErrorVM();
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorVM processServletRequestBindingValidationError(CustomParameterizedException ex) {
        log.error("400服务器内部异常:{}", ex.getMessage());
        return ex.getErrorVM();
    }

    //403
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorVM processAccessDeniedException(AccessDeniedException e) {
        log.error("403服务器内部异常:", e);
        return new ErrorVM(ErrorConstants.ERR_ACCESS_DENIED, e.getMessage());
    }

    private ErrorVM processFieldErrors(List<FieldError> fieldErrors) {
        ErrorVM dto = new ErrorVM(ErrorConstants.ERR_VALIDATION);
        for (FieldError fieldError : fieldErrors) {
            dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
        }
        return dto;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorVM processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("405服务器内部异常:", exception);
        return new ErrorVM(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVM> processRuntimeException(Exception ex, HttpServletRequest request) {
        BodyBuilder builder;
        ErrorVM errorVM;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);

        final String uri = request.getRequestURI();
        final String mobileNo = request.getHeader("mobileNo");
        if (responseStatus != null) {
            log.error("{}-[{}]服务器内部已知异常:", mobileNo, uri, ex);
            builder = ResponseEntity.status(responseStatus.value());
            errorVM = new ErrorVM("error." + responseStatus.value().value(), responseStatus.reason());
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            String message;
            if (ex instanceof QdigoRuntimeException) {
                log.error("{}-[{}]服务器已知的异常:", mobileNo, uri, ex);
                message = ex.getMessage();
            } else if (ex instanceof IOException) {
                log.error("{}-[{}]服务器IO异常:{}", mobileNo, uri, ex.getMessage());
                message = "服务器IO异常";
            } else {
                log.error("{}-[{}]服务器未知的异常:", mobileNo, uri, ex);
                message = "服务器未知异常";
            }
            //errorVM = new ErrorVM(ErrorConstants.ERR_INTERNAL_SERVER_ERROR, ex.getMessage());
            errorVM = new ErrorVM(message, ex.getMessage());
        }
        return builder.body(errorVM);
    }
}
