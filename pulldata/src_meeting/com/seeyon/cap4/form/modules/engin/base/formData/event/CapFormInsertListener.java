package com.seeyon.cap4.form.modules.engin.base.formData.event;

import com.seeyon.cap4.form.modules.engin.base.formData.fw.FwHttpSend;
import com.seeyon.common.GetFwTokenUtil;
import com.seeyon.common.JDBCUtil;
import com.seeyon.common.ProptiesUtil;
import com.seeyon.ctp.util.annotation.ListenEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapFormInsertListener {

    @ListenEvent(event = CapFormInsertDataEvent.class, async = true)
    public void doInsert(CapFormInsertDataEvent event) {
        Long id = event.getId();
        Map<String, Object> map = event.getDataMap();
        List<String> nameList = event.getList();

        ProptiesUtil prop = new ProptiesUtil();
        String type = event.getType();
        if (type.equals("cancel")) {//取消会议
            deleteCommon(prop, id);
        } else if (type.equals("new")) {//新发
            newCommon(id, map, nameList);
        } else if (type.equals("retry")) {
            deleteCommon(prop, id);
            newCommon(id, map, nameList);
        }

    }

    public void newCommon(Long id, Map<String, Object> map, List<String> nameList) {
        FwHttpSend fwHttpSend = new FwHttpSend(id, map, nameList);
        Thread thread = new Thread(fwHttpSend);
        try {
            Thread.sleep(5000);//沉睡5秒后执行
            thread.start();
        } catch (InterruptedException in) {
            in.printStackTrace();
        }
    }

    public void deleteCommon(ProptiesUtil prop, Long id) {
        String sql = "select fw_id from temp_fw_requrid_id where summary_id=" + id;
        List<Map<String, Object>> resultMap = JDBCUtil.doQuery(sql);
        if (resultMap.size() > 0) {
            Map<String, Object> requridMap = resultMap.get(0);
            String requrid = requridMap.get("fw_id") + "";
            Map<String, String> param = new HashMap<>();
            param.put("requestId", requrid);
            param.put("otherParams", "{\"ismonitor\":\"1\"}");
            //调用泛微删除流程的接口
            String result = GetFwTokenUtil.requestHttp("/api/workflow/paService/deleteRequest", param,new ProptiesUtil().getSendUserId());
            if ("true".equals(prop.getFlag())) {
                System.out.println(result);
            }
        }
    }
}
