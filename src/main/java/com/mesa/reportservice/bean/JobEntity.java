package com.mesa.reportservice.bean;

/**
 * Created by wk1 on 2019/5/20.
 */
public class JobEntity implements Cloneable {

    private Integer resultId;

    private String resultName;

    private Integer jobId;

    private String startTime;

    private String endTime;

    private String querySql;

    private Integer status;

    private Integer excuteTime;

    private Long excuteRow;

    private Integer excuteProcess;

    private String excuteDetail;

    private Integer isSendNotice;

    private String opTime;

    private String chartType;

    private String issuedTime;

    private String query_id;

    private String query_duration_ms;

    private int resultRows;


    private String memory_usage;

    private String result;

    private int excute_status;

    private String formatSql;


    private int isValid;

    private long beginTime;

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(Integer excuteTime) {
        this.excuteTime = excuteTime;
    }

    public Long getExcuteRow() {
        return excuteRow;
    }

    public void setExcuteRow(Long excuteRow) {
        this.excuteRow = excuteRow;
    }

    public Integer getExcuteProcess() {
        return excuteProcess;
    }

    public void setExcuteProcess(Integer excuteProcess) {
        this.excuteProcess = excuteProcess;
    }

    public String getExcuteDetail() {
        return excuteDetail;
    }

    public void setExcuteDetail(String excuteDetail) {
        this.excuteDetail = excuteDetail;
    }

    public Integer getIsSendNotice() {
        return isSendNotice;
    }

    public void setIsSendNotice(Integer isSendNotice) {
        this.isSendNotice = isSendNotice;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(String issuedTime) {
        this.issuedTime = issuedTime;
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


    public String getMemory_usage() {
        return memory_usage;
    }

    public void setMemory_usage(String memory_usage) {
        this.memory_usage = memory_usage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getExcute_status() {
        return excute_status;
    }

    public void setExcute_status(int excute_status) {
        this.excute_status = excute_status;
    }


    public int getResultRows() {
        return resultRows;
    }

    public void setResultRows(int resultRows) {
        this.resultRows = resultRows;
    }

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }


    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    @Override
    public Object clone() {
        JobEntity o = null;
        try {
            o = (JobEntity) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return o;
    }
}
