package com.mesa.reportservice.exception;

import cn.hutool.log.Log;
import com.mesa.reportservice.util.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author 86158

 */
@RestControllerAdvice
public class SQSExceptionHandler {
	private Log logger = Log.get();

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(SQSException.class)
	@ResponseStatus(value= HttpStatus.BAD_REQUEST)
	public R handleSQSException(SQSException e){
		R r = new R();
		r.put("code", e.getCode());
		r.put("msg", e.getMsg());
		return r;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	public R handleException(Exception e){
		logger.error(e);
		return R.error().put("msg", e.getMessage());
	}
}
