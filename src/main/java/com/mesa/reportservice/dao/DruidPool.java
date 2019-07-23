package com.mesa.reportservice.dao;

/**
 * Created by wk1 on 2018/9/27.
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.google.common.collect.Maps;
import com.mesa.reportservice.util.Logs;
import org.junit.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;


@Component
@ConfigurationProperties(prefix = "db")
public class DruidPool {


    private static String url;
    private static String drivers;
    private static String user;
    private static String password;
    private static Integer initialsize;
    private static Integer minidle;
    private static Integer maxactive;
    private static String filters;

    public DruidPool() {

    }

    public void setUrl(String url) {
        DruidPool.url = url;
    }


    public void setDrivers(String drivers) {
        DruidPool.drivers = drivers;
    }


    public void setUser(String user) {
        DruidPool.user = user;
    }


    public void setPassword(String password) {
        DruidPool.password = password;
    }


    public void setInitialsize(Integer initialsize) {
        DruidPool.initialsize = initialsize;
    }


    public void setMinidle(Integer minidle) {
        DruidPool.minidle = minidle;
    }


    public void setMaxactive(Integer maxactive) {
        DruidPool.maxactive = maxactive;
    }


    public void setFilters(String filters) {
        DruidPool.filters = filters;
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource dataSourcedb = null;

        if (dataSourcedb == null) {
            dataSourcedb = new DruidDataSource();
            //    props.load(DruidPool.class.getClassLoader().getResourceAsStream("clickhouse.properties"));
            //设置连接参数
//                dataSource.setUrl("jdbc:clickhouse://"+HttpClientUtils.getInstance().httpGet("http://"+FlowWriteConfig.DB_URL+":8077/getckurl?ip=2222&topic=eeeeee")+":8123/k18_ods");
            dataSourcedb.setUrl(url);
            dataSourcedb.setDriverClassName(drivers);
            dataSourcedb.setUsername(user);
            dataSourcedb.setPassword(password);
            //配置初始化大小、最小、最大
            dataSourcedb.setInitialSize(initialsize);
            dataSourcedb.setMinIdle(minidle);
            dataSourcedb.setMaxActive(maxactive);
            //连接泄漏监测
            dataSourcedb.setRemoveAbandoned(true);
            dataSourcedb.setRemoveAbandonedTimeout(1800);
            dataSourcedb.setDefaultAutoCommit(false);
            //配置获取连接等待超时的时间
            dataSourcedb.setMaxWait(30000);
            //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            dataSourcedb.setTimeBetweenEvictionRunsMillis(30000);
            //防止过期
            dataSourcedb.setValidationQuery("SELECT 'x'");
            dataSourcedb.setTestWhileIdle(true);
            dataSourcedb.setTestOnBorrow(true);
            dataSourcedb.setFilters(filters);
            // this.dataSourcedb = dataSourcedb;


        }
        Logs.debug("druid success");
        return dataSourcedb;

    }

    @Bean
    public ServletRegistrationBean statViewServlet() {
        //创建servlet注册实体
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //设置ip白名单
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        //设置ip黑名单，如果allow与deny共同存在时,deny优先于allow
        servletRegistrationBean.addInitParameter("deny", "192.168.0.19");
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "druid");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        //是否可以重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * druid 过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean statFilter() {
        //创建过滤器
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //设置过滤器过滤路径
        filterRegistrationBean.addUrlPatterns("/*");
        //忽略过滤的形式
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
/*

    @Test
    public void demo02() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            //使用配置文件来配置文件
            Properties pr = new Properties();
            //   pr.load(new FileInputStream("config/druid.properties"));

            InputStream is = DruidPool.class.getClassLoader().getResourceAsStream("druid.properties");
            //InputStream is = new FileInputStream(System.getProperty("user.dir")+File.separator+"config"+File.separator+"db.properties");

            pr.load(is);
            DataSource dataSource = DruidDataSourceFactory.createDataSource(pr);


            //1,创建Druid连接池对象
            //DruidDataSource dataSource = new DruidDataSource();
            //dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            //dataSource.setUrl("jdbc:mysql://localhost:3306/mytest-day08");
            //dataSource.setUsername("root");
            //dataSource.setPassword("123");


            //工具类中获得连接
            //conn = MyUtils.getConnertion01();

            //用Druid来连接
            conn = dataSource.getConnection();


            //2,执行数据库语句
            String sql = "SELECT * FROM accoumt";

            //3,用prepareStatement获取sql语句
            ps = conn.prepareStatement(sql);

            //4,执行sql语句,查询用executeQuery,增删改用executeUpdate
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " " + rs.getString("name") + " " + rs.getDouble("money"));
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {

        }
    }*/

