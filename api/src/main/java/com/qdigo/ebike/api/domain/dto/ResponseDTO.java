package com.qdigo.ebike.api.domain.dto;

import com.qdigo.ebike.common.core.util.R;
import lombok.ToString;

/**
 * Created by niezhao on 2017/6/19.
 */
@ToString
public class ResponseDTO<T> {
    private int statusCode;
    private String message;
    private T data;

    public ResponseDTO(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseDTO(int statusCode) {
        this.statusCode = statusCode;
    }

    public ResponseDTO() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ResponseDTO setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseDTO setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return this.statusCode <= 299 && this.statusCode >= 200;
    }

    public boolean isNotSuccess() {
        return !this.isSuccess();
    }

    public boolean isFail() {
        return this.statusCode >= 400 && this.statusCode <= 499;
    }

    public boolean isError() {
        return this.statusCode >= 500 && this.statusCode <= 599;
    }

    public R toResponse() {
        return R.ok(this.statusCode, this.message, this.data);
    }
}
