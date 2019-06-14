package com.mesa.reportservice.util;

/**
 * Created by wk1 on 2019/5/23.
 */

import org.apache.log4j.Logger;

public class Logs {
    private static Logger logger = Logger.getLogger(Logs.class);

    //debug方法，其他error、warn类似定义
    public static void debug(Object obj) {
        //如果更改日志出工工具，只需修改该行代码即可。如：可改为 System.out.println(obj);
        logger.debug(obj);
    }

    public static void error(Object obj) {
        //如果更改日志出工工具，只需修改该行代码即可。如：可改为 System.out.println(obj);
        logger.error(obj);
    }

    public static void info(Object obj) {
        //如果更改日志出工工具，只需修改该行代码即可。如：可改为 System.out.println(obj);
        logger.info(obj);
    }
}
