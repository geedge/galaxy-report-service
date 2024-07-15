package com.mesa.reportservice.exception;


import lombok.Getter;

/**
 * @author 86158
 */
@Getter
public enum SQSCode {
	/**
	 * 1-3位：异常类型（HTTP协议状态码）
	 * 4-6位：自然排序
	 */

	/**
	 * Message
	 */
	PARAM_SYNTAX_ERROR(400001,"Param Syntax Error ! ErrorCode = {0} queryId = {1}  jobId = {2} executeSql = {3}"),

	TEMPLATE_SQL_EXECUTION_ERROR(500001,"SQL Execution Error ! ErrorCode = {0} queryId = {1}  jobId = {2} executeSql = {3}"),
	TEMPLATE_SAVE_HBASE_ERROR(500002,"save HBase error ! ErrorCode = {0} queryId = {1} jobId = {2} executeSql = {3}"),
	TEMPLATE_SAVE_DATABASE_ERROR(500003,"save database error queryId = {0}  jobId = {1} {2}"),
	RESPONSE_DATA_ISNULL(500004,"response data is null"),
	RESPONSE_BODY_ISNULL(500005,"response body is null"),
	WRITE_HBASE_ERROR(500006,"write HBase error retry count"),
	OUT_OF_MEMORY_ERROR(500007,"OutOfMemory Error"),
	KILL_QUERY_ERROR(500008,"Kill Query Error"),
	TEMPLATE_UNKNOWN_ERROR(500009,"Unknown Error ! ErrorCode = {0} queryId = {1}  jobId = {2} executeSql = {3}"),
	UPDATE_STATUS_ERROR(500010,"failed to update database status"),
	TASK_CANNOT_EXECUTED(500011,"task cannot be executed"),
	UNKNOWN_ERROR(500999,"Unknown Error"),

	JOB_IS_NOT_EXIST(510000, "job is not exist"),
	//成功
    SUCCESS(200, "success"),
	//失败
    ERROR(999, "error");

	SQSCode(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	private Integer code;

	private String msg;
}
