package com.mesa.reportservice.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.HbaseProperties;
import com.mesa.reportservice.service.HbaseService;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * Created by wk1 on 2019/5/15.
 */

@Service
public class HbaseServiceImpl implements HbaseService {

    @Autowired
    private Connection hbaseConnection;

    @Autowired
    private HbaseProperties hbproperties;
    Log logger = Log.get();

    @Override
    public Boolean put(JobEntity jobEntity) {


        Boolean status = false;
        Table table = null;
        try {
            table = hbaseConnection.getTable(TableName.valueOf(hbproperties.getTable()));
            Put put = new Put(Bytes.toBytes(jobEntity.getJobId()));
            put.addColumn(Bytes.toBytes("response"), Bytes.toBytes("result"), Bytes.toBytes(jobEntity.getResult()));

            put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("excute_sql"), Bytes.toBytes(jobEntity.getQuerySql()));
            //put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("format_sql"), Bytes.toBytes(jobEntity.getFormatSql()));

            if (jobEntity.getRowsRead() != 0) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("read_rows"), Bytes.toBytes(jobEntity.getRowsRead()));
            }
            if (jobEntity.getQueryDurationMs() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("query_duration_ms"), Bytes.toBytes(jobEntity.getQueryDurationMs()));
            }
            if (jobEntity.getMemoryUsage() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("memory_usage"), Bytes.toBytes(jobEntity.getMemoryUsage()));
            }
            if (hbproperties.getCell_ttl_d() != null) {
                put.setTTL(hbproperties.getCell_ttl_d() * 86400 * 1000);
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


}
