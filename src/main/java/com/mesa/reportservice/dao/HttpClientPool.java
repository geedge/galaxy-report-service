package com.mesa.reportservice.dao;

import com.mesa.reportservice.util.Logs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by wk1 on 2019/5/15.
 */
@Component
@ConfigurationProperties(prefix = "http")
public class HttpClientPool {

    private Integer maxTotal;

    private Integer defaultMaxPerRoute;

    private Integer connectTimeout;

    private Integer connectionRequestTimeout;

    private Integer socketTimeout;

    private boolean staleConnectionCheckEnabled;


    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setDefaultMaxPerRoute(Integer defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setStaleConnectionCheckEnabled(boolean staleConnectionCheckEnabled) {
        this.staleConnectionCheckEnabled = staleConnectionCheckEnabled;
    }

    /**
     * 首先实例化一个连接池管理器，设置最大连接数、并发连接数
     *
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        //最大连接数
        httpClientConnectionManager.setMaxTotal(maxTotal);
        //并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return httpClientConnectionManager;
    }

    /**
     * 实例化连接池，设置连接池管理器。
     * 这里需要以参数形式注入上面实例化的连接池管理器
     *
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {

        //HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder，可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        httpClientBuilder.setConnectionManager(httpClientConnectionManager);

        return httpClientBuilder;
    }

    /**
     * 注入连接池，用于获取httpClient
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    /**
     * Builder是RequestConfig的一个内部类
     * 通过RequestConfig的custom方法来获取到一个Builder对象
     * 设置builder的连接信息
     * 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     *
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
    }

    /**
     * 使用builder构建一个RequestConfig对象
     *
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        Logs.debug("httpclient  success");
        return builder.build();
    }

    /**
     * @author LBQ
     * @version 1.0.0
     * @ClassName HbasePool
     * @Description Hbase连接池
     * @Date 2018年5月31日 下午10:22:06
     */
    //@Component
    //@ConfigurationProperties(prefix = "hbase")

    public static class HbasePool {

       /* private static Logger logger = Logger.getLogger(HbasePool.class);

        private static Configuration conf = null;

        private static String zookeeper_quorum;

        private static String zookeeper_property_clientPort;

        private static String zookeeper_znode_parent;

     *//*   private static String client_pause;*//*

        private static String client_retries_number;

        private static String rpc_timeout;

        *//*private static String client_operation_timeout;

        private static String client_scanner_timeout_period;*//*


        public HbasePool() {
        }

        *//**
         * @Field @connectNumMax : 连接池最大数
         *//*
        private static Integer connectNumMax;

        *//**
         * @Field @connectNumMin : 连接池最小数
         *//*
        private static Integer connectNumMin;

        *//**
         * @Field @counter : 计数器
         *//*
        private static Integer counter = 0;

        private static LinkedBlockingQueue<Connection> queue = new LinkedBlockingQueue<Connection>();



        public static synchronized Configuration getConfiguration() {
            if (conf == null) {
                conf = HBaseConfiguration.create();
                conf.set("hbase.zookeeper.quorum", zookeeper_quorum);
                conf.set("hbase.zookeeper.property.clientPort", zookeeper_property_clientPort);
                conf.set("hbase.zookeeper.znode.parent", zookeeper_znode_parent);
                // conf.set("hase.client.pause", client_pause);
                conf.set("hbase.client.retries.number", client_retries_number);
                conf.set("hbase.rpc.timeout", rpc_timeout);
                // conf.set("hbase.client.operation.timeout", client_operation_timeout);
                // conf.set("hbase.client.scanner.timeout.period", client_scanner_timeout_period);
            }
            return conf;
        }




        *//**
         * @Description 归还连接
         * @param con
         *//*
        public static void returnConnect(Connection con) {
            if (con == null) {
                return;
            }
            if (counter > connectNumMin) {
                try {
                    con.close();
                } catch (IOException e) {
                    logger.error("归还连接失败！", e);
                } finally {
                    con=null;
                    counter--;
                }
            } else {
                queue.add(con);
            }
        }

        *//**
         * @Description 获取连接
         * @return
         * @throws Exception
         *//*
        public static Connection getConnection() throws Exception {

            System.out.println("huoqu hbase lianjie" );
            if (!queue.isEmpty()) {
                return queue.poll();
            } else {
                if (counter >= connectNumMax) {
                    throw new Exception("Hbase连接池已满，请释放连接资源！目前连接数已达:[" + counter + "]");
                } else {
                    Connection con;
                    try {
                        con = ConnectionFactory.createConnection(getConfiguration());
                        counter++;
                    } catch (IOException e) {
                        throw new Exception("Hbase连接创建失败，请检查连接配置！", e);
                    }
                    return con;
                }
            }
        }

        public void setConnectNumMax(Integer connectNumMax) {
            HbasePool.connectNumMax = connectNumMax;
        }

        public void setConnectNumMin(Integer connectNumMin) {
            HbasePool.connectNumMin = connectNumMin;
        }

        *//**
         * @Description 存活连接数
         * @return
         *//*
        public static Integer conNum() {
            return counter;
        }


        public void setZookeeper_quorum(String zookeeper_quorum) {
            HbasePool.zookeeper_quorum = zookeeper_quorum;
        }

        public void setZookeeper_property_clientPort(String zookeeper_property_clientPort) {
            HbasePool.zookeeper_property_clientPort = zookeeper_property_clientPort;
        }

        public void setZookeeper_znode_parent(String zookeeper_znode_parent) {
            HbasePool.zookeeper_znode_parent = zookeeper_znode_parent;
        }


        public void setClient_retries_number(String client_retries_number) {
            HbasePool.client_retries_number = client_retries_number;
        }

        public void setRpc_timeout(String rpc_timeout) {
            HbasePool.rpc_timeout = rpc_timeout;
        }
    */
    }
}

