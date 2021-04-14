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


    private String getRowKey(String query_id) {

        String rowKey = "";
        String[] queryIdArray = query_id.split(":");
        rowKey = queryIdArray[1];
        return rowKey;

    }

    @Override
    public Boolean put(JobEntity jobEntity) {


        Boolean status = false;
        Table table = null;
        try {
            table = hbaseConnection.getTable(TableName.valueOf(hbproperties.getTable()));
            Put put = new Put(Bytes.toBytes(getRowKey(jobEntity.getQuery_id())));
            put.addColumn(Bytes.toBytes("response"), Bytes.toBytes("result"), Bytes.toBytes(jobEntity.getResult()));
            put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("result_id"), Bytes.toBytes(jobEntity.getResultId()));

            put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("excute_sql"), Bytes.toBytes(jobEntity.getQuerySql()));
            //put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("format_sql"), Bytes.toBytes(jobEntity.getFormatSql()));

            if (jobEntity.getExcuteRow() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("read_rows"), Bytes.toBytes(jobEntity.getExcuteRow()));
            }
            if (jobEntity.getQuery_duration_ms() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("query_duration_ms"), Bytes.toBytes(jobEntity.getQuery_duration_ms()));
            }
            if (jobEntity.getMemory_usage() != null) {
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("memory_usage"), Bytes.toBytes(jobEntity.getMemory_usage()));
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
