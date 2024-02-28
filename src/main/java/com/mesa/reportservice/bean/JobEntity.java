package com.mesa.reportservice.bean;

import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.log4j.spi.LoggerFactory;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Data
@TableName("saved_query_job")
public class JobEntity implements Cloneable {

    private String jobId;

    private String querySql;

    private String state;

    private String resultName;

    private float doneProgress;

    private Integer isFailed;

    private String resultMessage;

    private Integer elapsed;

    private long rowsRead;

    private long bytesRead;

    private long resultRows;

    private long resultBytes;

    private Integer isValid;

    private long startTime;

    private long endTime;

    private long lastUpdateTime;

    private long generatedTime;

    private String queryId;

    private Integer executeStatus;

    private String result;

    private String memoryUsage;

    private String queryDurationMs;

    @Override
    public Object clone() {
        JobEntity o = null;
        try {
            o = (JobEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            LogFactory.get().error(e);
        }
        return o;
    }
}
