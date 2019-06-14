package com.mesa.reportservice.service;

/**
 * HBase数据库基本操作
 */

//@Service
public class HBaseService {
/*



    private Logger log =  LoggerFactory.getLogger(HBaseService.class);

    private Admin admin = null;
    private Connection connection = null;

    public HBaseService() {
        try {
            connection = ConnectionFactory.createConnection(HttpClientPool.HbasePool.getConfiguration());
            admin = connection.getAdmin();
        } catch (IOException e) {
            log.error("获取HBase连接失败");
        }
    }




    *//**
     * @Description 删除表
     * @param tableName
     * @throws Exception
     *//*
    public static void dropTable(String tableName) throws Exception {

        Connection con = null;
        Admin admin = null;
        try {
            // ============获取连接管理=============
            con = HttpClientPool.HbasePool.getConnection();
            admin = con.getAdmin();
            // ============查询表是否存在=============
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                throw new Exception("需要删除的表[" + tableName + "]-不存在！");
            }

            // ============删除表=============
            if (!admin.isTableDisabled(TableName.valueOf(tableName))) {
                admin.disableTable(TableName.valueOf(tableName));
            }
            admin.deleteTable(TableName.valueOf(tableName));
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                    //log.error("Hbase - Admin资源释放失败！", e);
                }
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }

       // logger.info("=====删除表,表名:" + tableName);
    }

    *//**
     * @Description 插入数据 - 单条模式
     * @param tableName String
     * @param   rowKey List<HbaseData> hbaseDataList
     * @throws Exception
     *//*
    public  void insertData(String tableName, String rowKey, List<HbaseData> hbaseDataList) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // ============进行数据解析,封装PUT============
            Put put = new Put(Bytes.toBytes(rowKey));
            for (HbaseData hbaseData : hbaseDataList) {
                String columnFamily = hbaseData.getColumnFamily();
                for (Map.Entry<String, String> entry : hbaseData.getColumnValueMap().entrySet()) {
                    put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(entry.getKey()),
                            Bytes.toBytes(entry.getValue()));
                }
            }
            table.put(put);
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }
        //logger.info("=====插入数据,表名:[" + tableName + "],rowKey:[" + rowKey + "],List<HbaseData>:" + hbaseDataList);
    }




    *//**
     * @Description 插入数据 - 批量插入模式
     * @param tableName String
     * @param hbaseDTOList String rowKey List<HbaseData> hbaseDataList
     * @throws Exception
     *//*
    public  void insertBatchData(String tableName, List<HbaseDataDTO> hbaseDTOList) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // ============进行数据解析,封装PUT============
            List<Put> putList = new ArrayList<>();
            for (int index = 0; index < hbaseDTOList.size(); index++) {
                HbaseDataDTO hbaseDTO = hbaseDTOList.get(index);
                Put put = new Put(Bytes.toBytes(hbaseDTO.getRowKey()));
                for (HbaseData hbaseData : hbaseDTO.getHbaseDataList()) {
                    String columnFamily = hbaseData.getColumnFamily();
                    for (Map.Entry<String, String> entry : hbaseData.getColumnValueMap().entrySet()) {
                        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(entry.getKey()),
                                Bytes.toBytes(entry.getValue()));
                    }
                }
                putList.add(put);
                // ===============设置成1000次,提交一次==============
                if (index % 1000 == 0) {
                    table.put(putList);
                    putList.clear();
                }
            }
            table.put(putList);
           // logger.info("=====插入数据,表名:[" + tableName + "],批量数据插入成功!");
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }
    }

    public static void deleteDataByRowKey(String tableName, String rowKey) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // 按rowkey删除
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            table.delete(delete);
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }

       // logger.info("=====删除数据,表名:[" + tableName + "],rowKey:[" + rowKey + "],删除成功!");
    }

    public static void deleteDataByFamily(String tableName, String rowKey, String family) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // 按family删除
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addFamily(Bytes.toBytes(family));
            table.delete(delete);
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }

     //   logger.info("=====删除数据,表名:[" + tableName + "],rowKey:[" + rowKey + "],family:[" + family + "],删除成功!");
    }

    public static void deleteDataByColumn(String tableName, String rowKey, String family, String column)
            throws Exception {

        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // 按column删除
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumns(Bytes.toBytes(family), Bytes.toBytes(column));
            table.delete(delete);
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }
       // logger.info("=====删除数据,表名:[" + tableName + "],rowKey:[" + rowKey + "],family:[" + family + "],column:[" + column
              //  + "],删除成功!");
    }

    *//**
     * @Description 查询数据(最新一条)
     * @param tableName String 表名
     * @param rowKey String 行键
     * @return
     * @throws Exception
     *//*
    public  Map<String, Object> queryData(String tableName, String rowKey) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // ============进行GET操作==================
            Get get = new Get(Bytes.toBytes(rowKey));
            Result result = table.get(get);

            Map<String, Object> map = analysisQueryResult(result);

            // ============封装返回结果===============
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", map.values());
            resultMap.put("totalCount", map.values().size());
            return resultMap;
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }
    }

    *//**
     * @Description 根据条件查询数据
     * @param tableName String 表名
     * @param rowKey String 行键
     * @param version int 版本数量(查多少条数据)
     * @param paramMap key-String-family(列族) value-String[]-columns(列名)
     * @return
     * @throws Exception
     *//*
    public static Map<String, Object> queryDataByParams(String tableName, String rowKey, int version,
                                                        Map<String, String[]> paramMap) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // ============进行GET操作==================
            Get get = new Get(Bytes.toBytes(rowKey));
           // get.getMaxResultsPerColumnFamily(version);

            // ============组装条件==================
            assembleCondition(paramMap, get);
            Result result = table.get(get);
            Map<String, Object> map = analysisQueryResult(result);

            // ============封装返回结果===============
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", map.values());
            resultMap.put("totalCount", map.values().size());
            return resultMap;
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }
    }

    *//**
     * @Description 根据条件查询数据(根据时间进行限定)
     * @param tableName String 表名
     * @param rowKey String 行键
     * @param version int 版本数量(查多少条数据)
     * @param startTime long 开始时间戳 如果仅传开始时间戳,结束时间戳传0,则查开始时间戳对应的版本
     * @param endTime long 结束时间戳
     * @param paramMap key-String-family(列族) value-String[]-columns(列名)
     * @return
     * @throws Exception
     *//*
    public static Map<String, Object> queryDataByParamsAndTime(String tableName, String rowKey, int version,
                                                               long startTime, long endTime, Map<String, String[]> paramMap) throws Exception {
        Connection con = null;
        Table table = null;
        try {
            // ============获取连接管理==================
            con = HttpClientPool.HbasePool.getConnection();
            table = con.getTable(TableName.valueOf(tableName));

            // ============进行GET操作==================
            Get get = new Get(Bytes.toBytes(rowKey));
            get.getMaxResultsPerColumnFamily();
            if (endTime != 0) {
                get.setTimeRange(startTime, endTime);
            } else if (startTime != 0) {
                get.setTimeStamp(startTime);
            }
            // ============组装条件==================
            assembleCondition(paramMap, get);
            Result result = table.get(get);
            Map<String, Object> map = analysisQueryResult(result);

            // ============封装返回结果===============
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", map.values());
            resultMap.put("totalCount", map.values().size());
            return resultMap;
        } finally {
            if (table != null) {
                table.close();
            }
            if (con != null) {
                HttpClientPool.HbasePool.returnConnect(con);
            }
        }
    }

    *//**
     * @Description 装载条件
     * @param paramMap
     * @param get
     *//*
    private static void assembleCondition(Map<String, String[]> paramMap, Get get) {
        if (paramMap != null && !paramMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                if (entry.getValue() == null) {
                    get.addFamily(Bytes.toBytes(entry.getKey()));
                } else {
                    for (String column : entry.getValue()) {
                        get.addColumn(Bytes.toBytes(entry.getKey()), Bytes.toBytes(column));
                    }
                }
            }
        }
    }

    *//**
     * @Description 解析Result
     * @param result
     * @return
     *//*
    @SuppressWarnings("unchecked")
    private static Map<String, Object> analysisQueryResult(Result result) {
        Map<String, Object> map = new HashMap<>();
        for (Cell cell : result.listCells()) {
            Long timeStamp = cell.getTimestamp();
            String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            String familyRs = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String keyRs =
                    Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String valueRs = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

            // =========参数封装=========
            Map<String, Object> map2 = (Map<String, Object>) map.get(timeStamp.toString());
            if (map2 == null) {
                map2 = new HashMap<>();
            }
            Map<String, Object> map3 = (Map<String, Object>) map2.get(familyRs);
            if (map3 == null) {
                map3 = new HashMap<>();
            }
            map3.put(keyRs, valueRs);
            map2.put(familyRs, map3);
            map2.put("rowKey", rowKey);
            map2.put("timeStamp", timeStamp.toString());
            map.put(timeStamp.toString(), map2);
        }
        return map;
    }




    @SuppressWarnings("unchecked")
    private static Map<String, Object> analysisScanResult(Result result) {
        Map<String, Object> map = new HashMap<>();
        for (Cell cell : result.listCells()) {
            Long timeStamp = cell.getTimestamp();
            String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            String familyRs = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String keyRs =
                    Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String valueRs = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

            // =========参数封装=========
            Map<String, Object> map2 = (Map<String, Object>) map.get(familyRs);
            if (map2 == null) {
                map2 = new HashMap<>();
            }
            map2.put(keyRs, valueRs);
            map.put(familyRs, map2);
            map.put("rowKey", rowKey);
            map.put("timeStamp", timeStamp.toString());
        }
        return map;
    }






    *//**
     * 查询库中所有表的表名
     * shell command: list
     *//*
    public List<String> getAllTableNames() {
        List<String> result = new ArrayList<>();
        try {
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                result.add(tableName.getNameAsString());
            }
        } catch (IOException e) {
            log.error("获取所有表的表名失败", e);
        } finally {
            close(admin, null, null);
        }
        return result;
    }

    *//**
     * 遍历查询指定表中的所有数据
     * shell command: scan 'user'
     *//*
    public Map<String, Map<String, String>> getResultScanner(String tableName) {
        Scan scan = new Scan();
        return this.queryData(tableName, scan);
    }

    *//**
     * 通过表名以及过滤条件查询数据
     *//*
    private Map<String, Map<String, String>> queryData(String tableName,
                                                       Scan scan) {
        // <rowKey,对应的行数据>
        Map<String, Map<String, String>> result = new HashMap<>();

        ResultScanner rs = null;
        // 获取表
        Table table = null;
        try {
            table = getTable(tableName);
            rs = table.getScanner(scan);
            for (Result r : rs) {
                // 每一行数据
                Map<String, String> columnMap = new HashMap<>();
                String rowKey = null;
                // 行键，列族和列限定符一起确定一个单元（Cell）
                for (Cell cell : r.listCells()) {
                    if (rowKey == null) {
                        rowKey = Bytes.toString(cell.getRowArray(),
                                cell.getRowOffset(), cell.getRowLength());
                    }
                    columnMap.put(
                            // 列限定符
                            Bytes.toString(cell.getQualifierArray(),
                                    cell.getQualifierOffset(),
                                    cell.getQualifierLength()),
                            // 列族
                            Bytes.toString(cell.getValueArray(),
                                    cell.getValueOffset(),
                                    cell.getValueLength()));
                }

                if (rowKey != null) {
                    result.put(rowKey, columnMap);
                }
            }
        } catch (IOException e) {
            log.error(MessageFormat.format("遍历查询指定表中的所有数据失败,tableName:{0}",
                    tableName), e);
        } finally {
            close(null, rs, table);
        }

        return result;
    }

    *//**
     * 根据tableName和rowKey精确查询行数据
     *//*
    public Map<String, String> getRowData(String tableName, String rowKey) {
        // 返回的键值对
        Map<String, String> result = new HashMap<>();

        Get get = new Get(Bytes.toBytes(rowKey));
        // 获取表
        Table table = null;
        try {
            table = getTable(tableName);
            Result hTableResult = table.get(get);
            if (hTableResult != null && !hTableResult.isEmpty()) {
                for (Cell cell : hTableResult.listCells()) {
                    result.put(
                            Bytes.toString(cell.getQualifierArray(),
                                    cell.getQualifierOffset(),
                                    cell.getQualifierLength()),
                            Bytes.toString(cell.getValueArray(),
                                    cell.getValueOffset(),
                                    cell.getValueLength()));
                }
            }
        } catch (IOException e) {
            log.error(MessageFormat.format(
                    "查询一行的数据失败,tableName:{0},rowKey:{1}", tableName, rowKey), e);
        } finally {
            close(null, null, table);
        }

        return result;
    }

    *//**
     * 为表添加 or 更新数据
     *//*
    public void putData(String tableName, String rowKey, String familyName,
                        String[] columns, String[] values) {
        // 获取表
        Table table = null;
        try {
            table = getTable(tableName);

            putData(table, rowKey, tableName, familyName, columns, values);
        } catch (Exception e) {
            log.error(MessageFormat.format(
                    "为表添加 or 更新数据失败,tableName:{0},rowKey:{1},familyName:{2}",
                    tableName, rowKey, familyName), e);
        } finally {
            close(null, null, table);
        }
    }

    private void putData(Table table, String rowKey, String tableName,
                         String familyName, String[] columns, String[] values) {
        try {
            // 设置rowkey
            Put put = new Put(Bytes.toBytes(rowKey));

            if (columns != null && values != null
                    && columns.length == values.length) {
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i] != null && values[i] != null) {
                        put.addColumn(Bytes.toBytes(familyName),
                                Bytes.toBytes(columns[i]),
                                Bytes.toBytes(values[i]));
                    } else {
                        throw new NullPointerException(MessageFormat.format(
                                "列名和列数据都不能为空,column:{0},value:{1}", columns[i],
                                values[i]));
                    }
                }
            }

            table.put(put);
            log.debug("putData add or update data Success,rowKey:" + rowKey);
            table.close();
        } catch (Exception e) {
            log.error(MessageFormat.format(
                    "为表添加 or 更新数据失败,tableName:{0},rowKey:{1},familyName:{2}",
                    tableName, rowKey, familyName), e);
        }
    }

    *//**
     * 根据表名 获取table
     * Used to communicate with a single HBase table.
     * Table can be used to get, put, delete or scan data from a table.
     *//*
    private Table getTable(String tableName) throws IOException {
        return connection.getTable(TableName.valueOf(tableName));
    }

    *//**
     * 关闭流
     *//*
    private void close(Admin admin, ResultScanner rs, Table table) {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                log.error("关闭Admin失败", e);
            }
        }

        if (rs != null) {
            rs.close();
        }

        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                log.error("关闭Table失败", e);
            }
        }
    }*/
}
