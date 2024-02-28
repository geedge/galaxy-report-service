package com.mesa.reportservice.enums;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public enum JobStates {
    /**
     * PENDING
     */
    PENDING("PENDING"),
    /**
     * RUNNING
     */
    RUNNING("RUNNING"),
    /**
     * DONE
     */
    DONE("DONE");

    private final String value;

    public String getValue() {
        return value;
    }

    JobStates(String value) {
        this.value = value;
    }
}
