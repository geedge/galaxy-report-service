package com.mesa.reportservice.exception;


import lombok.Data;

/**
 * 自定义异常
 *
 * @author lijinyang
 */
@Data
public class SQSException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    private String msg;
    private int code = SQSCode.ERROR.getCode();

    public SQSException(SQSCode sqsCode) {
    	this.code = sqsCode.getCode();
    	this.msg = sqsCode.getMsg();
    }

    public SQSException(String msg) {
    	super(msg);
		this.msg = msg;
	}

	public SQSException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public SQSException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}

	public SQSException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
