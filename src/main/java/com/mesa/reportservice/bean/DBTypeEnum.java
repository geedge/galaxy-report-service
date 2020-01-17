package com.mesa.reportservice.bean;


public enum DBTypeEnum {
    CLICKHOUSE("clickhouse"),
    DRUID("druid"),
    HBASE("hbase"),;
    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
