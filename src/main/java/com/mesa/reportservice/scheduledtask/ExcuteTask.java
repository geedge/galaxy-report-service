package com.mesa.reportservice.scheduledtask;

import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.service.HttpAPIService;
import com.mesa.reportservice.util.ClickhouseConfig;
import com.mesa.reportservice.util.ConfigUtil;
import com.mesa.reportservice.util.Logs;

import java.net.SocketException;

import static com.mesa.reportservice.util.ClickhouseConfig.getJobUrl;

/**
 * Created by wk1 on 2019/5/31.
 */

//@Component

public class ExcuteTask {


    private HttpAPIService hp;

    public ExcuteTask(HttpAPIService hp) {

        this.hp = hp;
    }

    public void dotask(JobEntity je) {

        String joburl = getJobUrl(je.getQuery_sql(), je.getQuery_id());
        HttpResult hr = new HttpResult();
        int k = 3;
        do {
            try {
                hr = hp.doPost(joburl);
                String killurl = ClickhouseConfig.getKillUrl(je.getQuery_id());
                hp.doPost(killurl);
                if (hr.getCode() != 200 && hr.getBody().toLowerCase().contains("timeout")) {
                    k--;
                }
                else {
                    break;
                }
            } catch (SocketException e) {
                k--;
                je.setExcute_detail("查询失败" + e.toString());
                je.setStatus(4);
                Logs.error("查询失败" + e.toString());
            } catch (Exception e) {
                je.setStatus(4);
                je.setExcute_detail("查询失败" + e.toString());
                Logs.error("查询失败" + e.toString());
                break;
            }
        }
        while (k > 0);

        je.setResult(hr.getBody());
        if (hr.getCode() == 200 && !hr.getBody().equals("") && !hr.getBody().isEmpty()) {
            je.setResult(hr.getBody());
            je.setExcute_detail("success");
            je.setExcute_time(0);
            je.setExcute_process(100);
            je.setStatus(2);
          //  Logs.info("success queryid=" + je.getQuery_id() + " sql=" + je.getQuery_sql());
        } else {
            je.setResult("");
            je.setStatus(4);
            je.setExcute_detail("查询clickhouse失败" + hr.getBody());
            Logs.error("查询clickhouse失败" + hr.getBody());
          //  Logs.info("error queryid=" + je.getQuery_id() + " sql=" + je.getQuery_sql());

        }
        JobEntity ji = (JobEntity) je.clone();
        ConfigUtil.mapresult.put(ji.getQuery_id(), ji);
    }


}
