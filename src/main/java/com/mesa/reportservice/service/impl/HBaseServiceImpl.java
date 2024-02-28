package com.mesa.reportservice.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.HBaseProperties;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.service.HBaseService;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class HBaseServiceImpl implements HBaseService {

    @Autowired
    private Connection hBaseConnection;

    @Autowired
    private HBaseProperties hBaseProperties;

    private static final Log logger = LogFactory.get();

    @Override
    public Boolean put(JobEntity jobEntity) {
        Boolean status = false;
        Table table = null;
        try {
            table = hBaseConnection.getTable(TableName.valueOf(hBaseProperties.getTable()));
            Put put = new Put(Bytes.toBytes(jobEntity.getJobId()));
            put.addColumn(Bytes.toBytes("response"), Bytes.toBytes("result"), Bytes.toBytes(jobEntity.getResult()));
            put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("execute_sql"), Bytes.toBytes(jobEntity.getQuerySql()));
            if (jobEntity.getRowsRead() != 0) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("read_rows"), Bytes.toBytes(jobEntity.getRowsRead()));
            }
            if (jobEntity.getQueryDurationMs() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("query_duration_ms"), Bytes.toBytes(jobEntity.getQueryDurationMs()));
            }
            if (jobEntity.getMemoryUsage() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("memory_usage"), Bytes.toBytes(jobEntity.getMemoryUsage()));
            }
            if (hBaseProperties.getCellTtlD() != null) {
                put.setTTL(convertDaysToMilliseconds(hBaseProperties.getCellTtlD()));
            }
            table.put(put);
            status = true;
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(table);
        }
        return status;
    }

    private long convertDaysToMilliseconds(Integer days) { return 1000L*3600*24*days; }
}
