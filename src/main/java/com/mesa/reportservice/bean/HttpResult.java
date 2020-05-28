package com.mesa.reportservice.bean;

/**
 * Created by wk1 on 2019/5/15.
 */
public class HttpResult {

    private int code;
    private String body;
    private byte[] bodybyte;

    public HttpResult(int code, String body) {

        this.code = code;
        this.body = body;
    }

    public HttpResult() {

    }

    public byte[] getBodybyte() {
        return bodybyte;
    }

    public void setBodybyte(byte[] bodybyte) {
        this.bodybyte = bodybyte;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
