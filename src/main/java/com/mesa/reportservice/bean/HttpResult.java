package com.mesa.reportservice.bean;

import lombok.Data;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Data
public class HttpResult {

    private int code;

    private String body;

    public HttpResult() {
    }
}
