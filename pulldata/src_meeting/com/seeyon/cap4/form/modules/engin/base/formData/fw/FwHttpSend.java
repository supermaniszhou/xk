package com.seeyon.cap4.form.modules.engin.base.formData.fw;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.common.GetFwTokenUtil;
import com.seeyon.common.HttpClient;
import com.seeyon.common.JDBCUtil;
import com.seeyon.common.ProptiesUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.JDBCAgent;
import net.sf.json.JSONArray;
import org.apache.commons.logging.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FwHttpSend implements Runnable {

    private static final Log LOGGER = CtpLogFactory.getLog(FwHttpSend.class);

    private Long id;
    private Map<String, Object> map = null;
    private List<String> list;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FwHttpSend() {
    }

    public FwHttpSend(Long id, Map<String, Object> map, List<String> list) {
        this.id = id;
        this.map = map;
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public void run() {
        this.toSendOfFw(this.map, this.id, this.list);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public void toSendOfFw(Map<String, Object> pMap, Long id, List<String> names) {
        ProptiesUtil prop = new ProptiesUtil();
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = null;
        //标题
        map = new HashMap<>();
        map.put("fieldName", "tzbt");
        map.put("fieldValue", (pMap.get("field0002").toString()).replaceAll("\n", "").replaceAll("\r", ""));
        mapList.add(map);
        //召开时间
        map = new HashMap<>();
        map.put("fieldName", "zksj");
        map.put("fieldValue", pMap.get("field0003") + "");
        mapList.add(map);
        //会议地点
        map = new HashMap<>();
        map.put("fieldName", "hydd");
        map.put("fieldValue", pMap.get("field0001") + "");
        mapList.add(map);
        //主文件
        String mainSql = "select a.file_url,a.filename from " + prop.getOaMeetingFormmain() + " f,CTP_ATTACHMENT a where f.id=" + id + " and a.REFERENCE=f.id and a.SUB_REFERENCE=f.FIELD0006";
        List<Map<String, Object>> mainFiles = getFile(prop, mainSql);
        map = new HashMap<>();
        map.put("fieldName", "zwj");
        map.put("fieldValue", mainFiles);
        mapList.add(map);
        //文号
        map = new HashMap<>();
        map.put("fieldName", "wh");
        map.put("fieldValue", pMap.get("field0007") + "");
        mapList.add(map);
        //其他附件
        String qtFileSql = "select a.file_url,a.filename from " + prop.getOaMeetingFormmain() + " f,CTP_ATTACHMENT a where f.id=" + id + " and a.REFERENCE=f.id and a.SUB_REFERENCE=f.FIELD0012";
        List<Map<String, Object>> qtFileList = getFile(prop, qtFileSql);
        map = new HashMap<>();
        map.put("fieldName", "qtwj");
        map.put("fieldValue", qtFileList);
        mapList.add(map);
        //发布单位
        map = new HashMap<>();
        map.put("fieldName", "fbdw");
        map.put("fieldValue", pMap.get("field0054") + "");
        mapList.add(map);
        //发布部门
        String fbrSql = "select m.name ,(select name from ORG_UNIT u where id=m.ORG_DEPARTMENT_ID) deptName from  ORG_MEMBER m where m.id=" + pMap.get("field0010");
        List<Map<String, Object>> result = JDBCUtil.doQuery(fbrSql);
        map = new HashMap<>();
        map.put("fieldName", "fbbm");
        map.put("fieldValue", result.get(0).get("deptname") + "");
        mapList.add(map);
        //发布人
        map = new HashMap<>();
        map.put("fieldName", "fbr");
        map.put("fieldValue", result.get(0).get("name") + "");
        mapList.add(map);
        //发布人手机号码
        map = new HashMap<>();
        map.put("fieldName", "fbrsjhm");
        map.put("fieldValue", pMap.get("field0011") + "");
        mapList.add(map);
        //发布人固定电话
        map = new HashMap<>();
        map.put("fieldName", "fbrgddh");
        map.put("fieldValue", pMap.get("field0026") + "");
        mapList.add(map);
        //会议联系人
        List<Map<String, Object>> resultLxr = null;
        if (null != pMap.get("field0059")) {
            String lxrSql = "select m.name ,(select name from ORG_UNIT u where id=m.ORG_DEPARTMENT_ID) deptName from  ORG_MEMBER m where m.id=" + pMap.get("field0059");
            resultLxr = JDBCUtil.doQuery(lxrSql);
        }
        map = new HashMap<>();
        map.put("fieldName", "hylxr");
        if (resultLxr.size() > 0) {
            map.put("fieldValue", resultLxr.get(0).get("name") + "");
        } else {
            map.put("fieldValue", "");
        }
        mapList.add(map);
        //会议联系人手机号码
        map = new HashMap<>();
        map.put("fieldName", "hylxrsjhm");
        map.put("fieldValue", null != pMap.get("field0060") ? pMap.get("field0060") + "" : "");
        mapList.add(map);
        //会议联系人固定电话
        map = new HashMap<>();
        map.put("fieldName", "hylxrgddh");
        map.put("fieldValue", null != pMap.get("field0061") ? pMap.get("field0061") + "" : "");
        mapList.add(map);
        //发布时间
        map = new HashMap<>();
        map.put("fieldName", "fbsj");
        map.put("fieldValue", pMap.get("field0013") + "");
        mapList.add(map);
        //截止报送时间
        map = new HashMap<>();
        map.put("fieldName", "jzbssj");
        map.put("fieldValue", pMap.get("field0022") + "");
        mapList.add(map);
        //通知对象
        StringBuilder sb = new StringBuilder();
        names.forEach(s -> {
            sb.append(s + "；");
        });
        map = new HashMap<>();
        map.put("fieldName", "tzdx");
        map.put("fieldValue", sb.toString());
        mapList.add(map);
        //会议编号
        map = new HashMap<>();
        map.put("fieldName", "hybh");
        map.put("fieldValue", pMap.get("field0028") + "");
        mapList.add(map);
        //开始日期
//        map = new HashMap<>();
//        map.put("fieldName", "rq");
//        map.put("fieldValue", );
//        mapList.add(map);
//        //开始时间
//        map = new HashMap<>();
//        map.put("fieldName", "sj");
//        map.put("fieldValue", );
//        mapList.add(map);
//        //结束日期
//        map = new HashMap<>();
//        map.put("fieldName", "jsrq");
//        map.put("fieldValue", );
//        mapList.add(map);
//        //结束时间
//        map = new HashMap<>();
//        map.put("fieldName", "jssj");
//        map.put("fieldValue", );
//        mapList.add(map);
        Map<String, String> param = new HashMap<>();
        param.put("requestName", "集团会议通知");
        param.put("workflowId", prop.getFwMeetingWorkflow());
        param.put("mainData", JSONArray.fromObject(mapList).toString());
        String resultInfo = JSONObject.toJSONString(param);
        if ("true".equals(prop.getFlag())) {
            System.out.println(resultInfo);
        }
        String back1 = GetFwTokenUtil.requestHttp(prop.getDocreate(), param, prop.getSendUserId());
        if ("true".equals(prop.getFlag())) {
            System.out.println("返回结果：" + back1);
        }
        if (back1.indexOf("requestid") != -1) {
            JSONObject object = JSONObject.parseObject(back1);
            Map data = (Map) object.get("data");
            String requestid = data.get("requestid").toString();
            String querySql = "select summary_id,fw_id from temp_fw_requrid_id where summary_id=" + id;
            List<Map<String, Object>> listTemp = JDBCUtil.doQuery(querySql);

            String insertsql = "insert into temp_fw_requrid_id(summary_id,fw_id) values(?,?)";
            String updateSql = "update temp_fw_requrid_id set fw_id=? where summary_id=?";
            Connection connection = null;
            PreparedStatement ps = null;
            try {
                connection = JDBCAgent.getRawConnection();
                if (listTemp.size() == 0) {
                    ps = connection.prepareStatement(insertsql);
                    ps.setString(1, id + "");
                    ps.setString(2, requestid);
                } else {
                    ps = connection.prepareStatement(updateSql);
                    ps.setString(1, requestid);
                    ps.setString(2, id + "");
                }
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeUtil(connection, ps, null);
            }
        }
    }

    public void closeUtil(Connection connection, PreparedStatement ps, ResultSet rs) {
        try {
            if (null != ps) {
                ps.close();
            }
            if (null != rs) {
                rs.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }


    public void toSendInterface() {

    }


    public List<Map<String, Object>> getFile(ProptiesUtil prop, String sql) {
        List<Map<String, Object>> fjList = new ArrayList<>();

        String token = GetFwTokenUtil.getOaToken();

        List<Map<String, String>> mainfileUrl = getFilePath(sql);
        Map<String, Object> fjMap = null;
        String fileDownpath = "/seeyon/rest/attachment/file/";
        for (int i = 0; i < mainfileUrl.size(); i++) {
            Map<String, String> map = mainfileUrl.get(i);
            String downloadUrl = prop.getOaUrl() + fileDownpath + map.get("fileUrl") + "?fileName=" + map.get("fileUrl") + "&token=" + token;
            fjMap = new HashMap<>();
            fjMap.put("filePath", downloadUrl);
            fjMap.put("fileName", map.get("filename").substring(0, map.get("filename").lastIndexOf(".")));
            fjList.add(fjMap);
        }
        return fjList;
    }

    public List<Map<String, String>> getFilePath(String sql) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, String>> list = new ArrayList<>();
        try {
            connection = JDBCAgent.getRawConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            Map<String, String> map = null;
            while (rs.next()) {
                String fileUrl = rs.getString("file_url");
                String filename = rs.getString("filename");
                map = new HashMap<>();
                map.put("fileUrl", fileUrl);
                map.put("filename", filename);
                list.add(map);
            }
        } catch (Exception e) {
            LOGGER.error("zhou:关联查询附件出错了！错误信息：" + e.getMessage());
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != ps) {
                    ps.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (Exception io) {
                io.printStackTrace();
            }

        }
        return list;
    }

}
