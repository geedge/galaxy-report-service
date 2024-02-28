package com.mesa.reportservice.util;

/**
 *
 * @author lijinyang
 * @date: 2024/01/12/16:33
 */
public class Constant {

    /**
     * jobMessage
     */
    public static final String OK = "OK";
    public static final String CANCEL = "CANCEL";
    public static final String EXECUTING = "EXECUTING";
    public static final String WRITE_DATA_ERROR = "Write Data Error";
    public static final String PARAM_SYNTAX_ERROR = "Param Syntax Error";
    public static final String SQL_EXECUTION_ERROR = "SQL Execution Error";
    public static final String UNKNOWN_ERROR = "Unknown Error";
    public static final String DATABASE_ERROR = "Database Error";
    public static final String RE_EXECUTION = "Re Execution";
    public static final String QUERY_TIME_OUT = "SQL Execution Error execute query time out";
    public static final String RESULT_TOO_LARGE = "Result Too Large";

    /**
     * jobStatus
     */
    public static final int JOB_SUCCESS = 20000666;
    public static final int JOB_ERROR = 555999;
    public static final int JOB_EXECUTION_TIMEOUT = 500001;
    public static final int JOB_SQL_EXECUTION_ERROR_CODE = 50001300;
    public static final int JOB_BAD_REQUEST_THRESHOLD = 40000000;
    public static final int JOB_INTERNAL_SERVER_ERROR_THRESHOLD = 50000000;

    /**
     * loggerMessage
     */
    public static final String LOG_SUCCESS_SAVE_TO_HBASE = "success save to HBase ! queryId = {0} jobId = {1} executeSql = {2}";
    public static final String LOG_NO_PENDING_TASKS = "no pending tasks";
    public static final String LOG_START_EXECUTING_TASKS = "start executing task";
    public static final String LOG_START_VIEWING_RESULTS = "start viewing results";

    /**
     * URL
     */
    public static final String URL_GET_QUERY_ID = "{0}/v1/sql/query/query_id?query=";
    public static final String URL_QUERY_FOR_EXECUTE = "{0}/sql?originalSQL=";
    public static final String URL_QUERY_FOR_PROCESS = "{0}/v1/sql/query/{1}/progress";
    public static final String URL_QUERY_FOR_CANCEL = "{0}/v1/sql/query/{1}";
}
