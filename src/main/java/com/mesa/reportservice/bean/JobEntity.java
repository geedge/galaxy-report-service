package com.mesa.reportservice.bean;

/**
 * Created by wk1 on 2019/5/20.
 */
public class JobEntity {

    private int result_id;
    private String result_name;
    private String task_id;
    private String start_time;
    private String end_time;
    private String query_sql;
    private int status;
    private int excute_time;
    private long excute_row;
    private int excute_process;
    private String excute_detail;
    private int is_send_notice;
    private String op_time;
    private String query_id;
    private String query_duration_ms;
    private long read_rows;
    private String memory_usage;


    public int getResult_id() {
        return result_id;
    }

    public void setResult_id(int result_id) {
        this.result_id = result_id;
    }

    public String getResult_name() {
        return result_name;
    }

    public void setResult_name(String result_name) {
        this.result_name = result_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getQuery_sql() {
        return query_sql;
    }

    public void setQuery_sql(String query_sql) {
        this.query_sql = query_sql;
    }

    public int getExcute_time() {
        return excute_time;
    }

    public void setExcute_time(int excute_time) {
        this.excute_time = excute_time;
    }

    public long getExcute_row() {
        return excute_row;
    }

    public void setExcute_row(long excute_row) {
        this.excute_row = excute_row;
    }

    public int getExcute_process() {
        return excute_process;
    }

    public void setExcute_process(int excute_process) {
        this.excute_process = excute_process;
    }

    public String getExcute_detail() {
        return excute_detail;
    }

    public void setExcute_detail(String excute_detail) {
        this.excute_detail = excute_detail;
    }

    public int getIs_send_notice() {
        return is_send_notice;
    }

    public void setIs_send_notice(int is_send_notice) {
        this.is_send_notice = is_send_notice;
    }

    public String getOp_time() {
        return op_time;
    }

    public void setOp_time(String op_time) {
        this.op_time = op_time;
    }


    public String getQuery_id() {
        return query_id;
    }

    public void setQuery_id(String query_id) {
        this.query_id = query_id;
    }

    public String getQuery_duration_ms() {
        return query_duration_ms;
    }

    public void setQuery_duration_ms(String query_duration_ms) {
        this.query_duration_ms = query_duration_ms;
    }

    public long getRead_rows() {
        return read_rows;
    }

    public void setRead_rows(long read_rows) {
        this.read_rows = read_rows;
    }

    public String getMemory_usage() {
        return memory_usage;
    }

    public void setMemory_usage(String memory_usage) {
        this.memory_usage = memory_usage;
    }
}
