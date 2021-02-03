package com.seeyon.apps.ext.pulldata.listener;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seeyon.apps.collaboration.event.CollaborationAffairsAssignedEvent;
import com.seeyon.apps.collaboration.event.CollaborationFinishEvent;
import com.seeyon.apps.collaboration.event.CollaborationStartEvent;
import com.seeyon.apps.collaboration.event.CollaborationStepBackEvent;
import com.seeyon.apps.ext.pulldata.event.EdocCancelEvent;
import com.seeyon.apps.ext.pulldata.event.EdocExchangeSendEvent;
import com.seeyon.cap4.form.modules.engin.base.formData.fw.FwHttpSend;
import com.seeyon.common.GetFwTokenUtil;
import com.seeyon.common.HttpClient;
import com.seeyon.common.JDBCUtil;
import com.seeyon.common.ProptiesUtil;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.filemanager.manager.FileManagerImpl;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.manager.EdocManager;
import net.sf.json.JSONArray;
import org.apache.commons.logging.Log;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdocExchangeSendListener {
    private static final Log LOGGER = CtpLogFactory.getLog(FwHttpSend.class);

    private FileManager fileManager = new FileManagerImpl();
    private EdocManager edocManager = (EdocManager) AppContext.getBean("edocManager");

    /**
     * 流程发起监听事件
     */
    @ListenEvent(event = CollaborationStartEvent.class, async = true)
    public void start(CollaborationStartEvent event) {
    }

    /**
     * 流程完成
     */
    @ListenEvent(event = CollaborationFinishEvent.class, async = true)
    public void finish(CollaborationFinishEvent event) {
        ProptiesUtil pUtil = new ProptiesUtil();
        String debugger = pUtil.getValueByKey("oa.debugger.flag");

        String templates = pUtil.getValueByKey("oa.gfgs.templateCode");
        String templateCode = null;
        try {
            templateCode = event.getTemplateCode();
            if (null != templateCode && !"".equals(templateCode)) {
                if (templates.contains(templateCode)) {
                    Long summaryId = event.getSummaryId();
                    if ("gfqsbg".equals(templateCode)) {
                        //获取批示，审批，拟办意见
                        String sql = "select id,field0005,field0006,field0007,field0008,field0011 from formmain_0596 where id =(select form_recordid from COL_SUMMARY where id =" + summaryId + ")";
                        //获取办理意见时上传的附件
                        String fileSql = "select content,relate_info from CTP_COMMENT_ALL where module_id =" + summaryId;
                        Connection connection = null;
                        PreparedStatement ps = null;
                        ResultSet rs = null;
                        try {
                            List<Map<String, Object>> opinionList = JDBCUtil.doQuery(sql);
                            if (opinionList.size() > 0) {
                                Map<String, Object> opMap = opinionList.get(0);
                                String requireId = (String) opMap.get("field0011");
                                connection = JDBCAgent.getRawConnection();
                                ps = connection.prepareStatement(fileSql);
                                rs = ps.executeQuery();
                                List<Map<String, Object>> fjList = new ArrayList<>();

                                while (rs.next()) {
                                    String fileJson = rs.getString("relate_info");
                                    com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(fileJson);
                                    if (jsonArray.size() > 0) {
                                        String fileDownpath = "/seeyon/rest/attachment/file/";
                                        String token = GetFwTokenUtil.getOaToken();
                                        Map<String, Object> fjMap = null;
                                        for (int i = 0; i < jsonArray.size(); i++) {
                                            Map<String, Object> fmap = (Map<String, Object>) jsonArray.get(i);
                                            fjMap = new HashMap<>();
                                            String fileUrl = (String) fmap.get("fileUrl");
                                            String fileName = (String) fmap.get("filename");
                                            String downloadUrl = pUtil.getOaUrl() + fileDownpath + fileUrl + "?fileName=" + fileUrl + "&token=" + token;
                                            fjMap.put("filePath", downloadUrl);
                                            fjMap.put("fileName", fmap.get("filename"));
                                            fjList.add(fjMap);
                                        }
                                    }
                                }
                                //调用泛微接口
                                Map<String, Object> map = null;
                                List<Map<String, Object>> mapList = new ArrayList<>();
                                //拟办意见
                                map = new HashMap<>();
                                map.put("fieldName", "jtnbyj");
                                map.put("fieldValue", opinionList.get(0).get("field0006"));
                                mapList.add(map);
                                //批示意见
                                map = new HashMap<>();
                                map.put("fieldName", "jtpsyj");
                                map.put("fieldValue", opinionList.get(0).get("field0007"));
                                mapList.add(map);
                                //办理意见
                                map = new HashMap<>();
                                map.put("fieldName", "jtblyj");
                                map.put("fieldValue", opinionList.get(0).get("field0008"));
                                mapList.add(map);
                                //附件问题
                                Map<String, Object> fj = new HashMap<>();
                                fj.put("fieldName", "jtblyjfj");
                                fj.put("fieldValue", fjList);
                                mapList.add(fj);
                                Map<String, String> param = new HashMap<>();
                                param.put("mainData", JSONArray.fromObject(mapList).toString());
                                if ("true".equals(debugger)) {
                                    param.put("requestId", "206210");
                                    String result = requestHttp("/api/workflow/paService/submitRequest", param);
                                    System.out.println(result);
                                } else {
                                    if (null != requireId && !"".equals(requireId)) {
                                        param.put("requestId", requireId);
                                        String result = requestHttp("/api/workflow/paService/submitRequest", param);
                                        System.out.println(result);
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
                            } catch (SQLException s) {
                                s.printStackTrace();
                            }

                        }
                    } else if ("gfqjsp".equals(templateCode)) {
                        //股份公司请假审批意见回写
                        String qjTableName = pUtil.getValueByKey("oa.gfgs.qj.table");
                        String qjTableYjColumn = pUtil.getValueByKey("oa.gfgs.qj.table.yj");
                        String yjSql = "select " + qjTableYjColumn + ",field0024 from " + qjTableName + " where id =(select form_recordid from COL_SUMMARY where id =" + summaryId + ")";
                        List<Map<String, Object>> mapList = JDBCUtil.doQuery(yjSql);
                        if (mapList.size() > 0) {
                            Map<String, Object> map = mapList.get(0);
                            //调用泛微接口
                            Map<String, Object> fmap = null;
                            List<Map<String, Object>> fwList = new ArrayList<>();
                            //拟办意见
                            fmap = new HashMap<>();
                            fmap.put("fieldName", "jtfhyj");
                            fmap.put("fieldValue", map.get(qjTableYjColumn));
                            fwList.add(fmap);
                            Map<String, String> paramf = new HashMap<>();
                            paramf.put("mainData", JSONArray.fromObject(fwList).toString());
                            if (null != map.get("field0024")) {
                                String requestId = map.get("field0024") + "";
                                if (null != requestId && !"".equals(requestId)) {
                                    paramf.put("requestId", requestId);
                                    String result = requestHttp("/api/workflow/paService/submitRequest", paramf);
                                    if ("true".equals(debugger)) {
                                        System.out.println(result);
                                    }
                                }
                            } else {
                                if ("true".equals(debugger)) {
                                    paramf.put("requestId", "564570");
                                    String result = requestHttp("/api/workflow/paService/submitRequest", paramf);
                                    System.out.println(result);
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("zhou:流程结束意见回写出错了：" + e.getMessage());
        }
    }

    /**
     * 下一节点处理信息
     *
     * @param event
     */
    @ListenEvent(event = CollaborationAffairsAssignedEvent.class, async = true)
    public void assigned(CollaborationAffairsAssignedEvent event) throws UnsupportedEncodingException, EdocException {
        ProptiesUtil prop = new ProptiesUtil();
        String templates = prop.getValueByKey("oa.gfgs.templateCode");
        String templateCode = null;
        try {
            templateCode = event.getTemplateCode();
            if (null != templateCode && !"".equals(templateCode)) {
                if (templates.contains(templateCode)) {
                    CtpAffair currentAffair = event.getCurrentAffair();
                    Long summaryId = event.getSummaryId();
                    Long affairId = currentAffair.getId();
                    String url = prop.getServerUrl() + "/api/integration/wstest?summaryid=" + summaryId + "&affairid=" + affairId + "";

                    Map<String, Object> objectMap = GetFwTokenUtil.testRegist(prop.getServerUrl());
                    String token = GetFwTokenUtil.testGetoken(objectMap);
                    String appId = prop.getAppId();
                    String spk = StrUtil.nullToEmpty((String) objectMap.get("spk"));
                    RSA rsa = new RSA(null, spk);
                    String userId = rsa.encryptBase64(prop.getSendUserId(), CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("appid", appId);
                    headers.put("token", token);
                    headers.put("userid", userId);
                    headers.put("Content-Type", "application/json; charset=utf-8");
//                    String back1 = HttpClient.httpGet(url, headers, "utf-8");
//                    System.out.println(back1);
                }
            }

        } catch (Exception e) {
            LOGGER.error("zhou:EdocExchangeSendListener监听类中的下一节点处理信息assigned报错了：" + e.getMessage());
        }

    }

    /**
     * 退回、回退
     * 泛微调用致远OA接口发起请示报告协同，在审批退回时调用泛微退回接口
     *
     * @param event
     */
    @ListenEvent(event = CollaborationStepBackEvent.class, async = true)
    public void rollback(CollaborationStepBackEvent event) {
        ProptiesUtil prop = new ProptiesUtil();
        String debugger = prop.getValueByKey("oa.debugger.flag");

        String templates = prop.getValueByKey("oa.gfgs.templateCode");
        String templateCode = null;
        try {
            templateCode = event.getTemplateCode();
            if (null != templateCode && !"".equals(templateCode)) {
                Long summaryId = event.getSummaryId();
                if ("gfqsbg".equals(templateCode)) {
                    String sql = "select id,field0005,field0006,field0007,field0008,field0011 from formmain_0596 where id =(select form_recordid from COL_SUMMARY where id =" + summaryId + ")";
                    List<Map<String, Object>> list = JDBCUtil.doQuery(sql);
                    if (list.size() > 0) {
                        Map<String, Object> map = list.get(0);
                        String requireId = (String) map.get("field0011");
                        Map<String, String> param = new HashMap<>();
                        if ("true".equals(debugger)) {
                            param.put("requestId", "206209");
                            String result = requestHttp("/api/workflow/paService/rejectRequest", param);
                            System.out.println(result);
                        } else {
                            if (null != requireId && !"".equals(requireId)) {
                                param.put("requestId", requireId);
                                String result = requestHttp("/api/workflow/paService/rejectRequest", param);
                                System.out.println(result);
                            }
                        }
                    }
                } else if ("gfqjsp".equals(templateCode)) {
                    String qjTableName = prop.getValueByKey("oa.gfgs.qj.table");
                    String qjSql = "select field0024 from " + qjTableName + " where id =(select form_recordid from COL_SUMMARY where id =" + summaryId + ")";
                    List<Map<String, Object>> qjMap = JDBCUtil.doQuery(qjSql);
                    if (qjMap.size() > 0) {
                        Map<String, Object> map = qjMap.get(0);

                        Map<String, String> param = new HashMap<>();
                        if (null != map.get("field0024")) {
                            String requestId = map.get("field0024") + "";
                            if (null != requestId && !"".equals(requestId)) {
                                param.put("requestId", requestId);
                                String result = requestHttp("/api/workflow/paService/rejectRequest", param);
                                System.out.println(result);
                            }
                        } else {
                            if ("true".equals(debugger)) {
                                param.put("requestId", "564569");
                                String result = requestHttp("/api/workflow/paService/rejectRequest", param);
                                System.out.println(result);
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("zhou:在回退监听接口中的rollback方法，获取末班编号出问题了：" + e.getMessage());
        }

    }

    public String requestHttp(String reqUrl, Map<String, String> param) {
        ProptiesUtil pUtil = new ProptiesUtil();
        String address = pUtil.getServerUrl();
        Map<String, Object> objectMap = GetFwTokenUtil.testRegist(address);
        String token = GetFwTokenUtil.testGetoken(objectMap);
        String appId = pUtil.getAppId();
        String spk = StrUtil.nullToEmpty((String) objectMap.get("spk"));
        RSA rsa = new RSA(null, spk);
        String userId = rsa.encryptBase64(pUtil.getStepBack(), CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        Map<String, String> headers = new HashMap<>();
        headers.put("appid", appId);
        headers.put("token", token);
        headers.put("userid", userId);
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        String url = address + reqUrl;
        String back1 = HttpClient.httpPostForm(url, param, headers, "utf-8");
        return back1;
    }

    /**
     * 快速发文撤销，调用泛微撤销接口
     *
     * @param event
     */
    @ListenEvent(event = EdocCancelEvent.class, async = true)
    public void cancel(EdocCancelEvent event) {
        ProptiesUtil prop = new ProptiesUtil();

        List<CtpAffair> affairs = event.getList();
        for (int i = 0; i < affairs.size(); i++) {
            CtpAffair ctpAffair = affairs.get(i);
            String memberId = ctpAffair.getMemberId() + "";
            if (memberId.equals(prop.getOaPendingMemberId())) {
                Long summaryId = event.getSummaryId();
                String sql = "select summary_id,fw_id from temp_fw_requrid_id where summary_id=" + summaryId + "";
                List<Map<String, Object>> list = JDBCUtil.doQuery(sql);
                String delUrl = prop.getServerUrl() + "/api/workflow/paService/deleteRequest";
                Map<String, String> map = new HashMap<>();
                map.put("requestId", (String) list.get(0).get("fw_id"));
                map.put("otherParams", "{\"ismonitor\":\"1\"}");

                Map<String, Object> objectMap = GetFwTokenUtil.testRegist(prop.getServerUrl());
                String token = GetFwTokenUtil.testGetoken(objectMap);
                String appId = prop.getAppId();
                String spk = StrUtil.nullToEmpty((String) objectMap.get("spk"));
                RSA rsa = new RSA(null, spk);
                String userId = rsa.encryptBase64(prop.getSendUserId(), CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
                Map<String, String> headers = new HashMap<>();
                headers.put("appid", appId);
                headers.put("token", token);
                headers.put("userid", userId);
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                String back1 = HttpClient.httpPostForm(delUrl, map, headers, "utf-8");
                System.out.println(back1);
            }
        }
    }

    @ListenEvent(event = EdocExchangeSendEvent.class, async = true)
    public void send(EdocExchangeSendEvent event) throws BusinessException, FileNotFoundException, UnsupportedEncodingException, SQLException {
        ProptiesUtil pUtil = new ProptiesUtil();
        Long summaryId = event.getSummaryId();
        EdocSummary edocSummary = event.getEdocSummary();

        //todo 发文类型，字段泛微还没提供
        //[徐矿集团]在这里添加获取发文类型返回到页面上显示，zhou:2021-01-23 16:55 开始
        String sqlExtend = "select list3 from EDOC_SUMMARY_EXTEND where summary_id=" + summaryId;
        String sendEdocType = "";
        try (JDBCAgent jdbcAgent = new JDBCAgent();) {
            jdbcAgent.execute(sqlExtend);
            List<Map<String, Object>> mapListEx = jdbcAgent.resultSetToList();
            if (mapListEx.size() > 0) {
                Map<String, Object> mapEx = mapListEx.get(0);
                String list3 = mapEx.get("list3") + "";
                if ("0".equals(list3)) {
                    //党委发文
                    sendEdocType = "dw";
                } else if ("0".equals(list3)) {
                    //行政发文
                    sendEdocType = "xz";
                } else {
                    sendEdocType = "";
                }
            }
        } catch (Exception e) {
            LOGGER.error("zhou:EdocExchangeSendListener中send出错了：" + e.getMessage());
        }
        //[徐矿集团]在这里添加获取发文类型返回到页面上显示，zhou:2021-01-23 16:55 截止

        //在此判断行政发文、党委发文
        if (!"".equals(sendEdocType)) {
            if ("dw".equals(sendEdocType)) {
                //党委发文直接发送到股份公司下的单位
                if (isSendGfgs(edocSummary.getSendToId())) {

                }
            } else if ("xz".equals(sendEdocType)) {
                //行政发文发到股份公司
                if (isSendGfgs(edocSummary.getSendToId())) {
                    xzfwToGfgs(edocSummary, summaryId.longValue(), true);
                }
            }
        }
    }

    /**
     * 在判断一下下发单位有没有股份公司及子部门
     */
    public boolean isSendGfgs(String sendToId) {
        String[] ids = sendToId.split(",");
        boolean flag = false;
        //在此判断所选的机构组是不是股份公司机构组
        for (int i = 0; i < ids.length; i++) {
            String id_2 = ids[i].split("\\|")[1];
            if (id_2.equals(new ProptiesUtil().getGfTeamId())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 行政发文发给股份公司
     */
    public void xzfwToGfgs(EdocSummary edocSummary, long summaryId, boolean flag) throws SQLException {
        ProptiesUtil pUtil = new ProptiesUtil();

        //获取gfgs绑定的泛微人员id
        List<String> fwUserId = getFwUserIdList(edocSummary.getSendToId(), flag);
        String sendToId = edocSummary.getSendToId();
        String[] sendtoidArr = sendToId.split(",");
        String getTeamMember = "select u.name,u.id from (select * from EDOC_OBJ_TEAM_MEMBER where TEAM_ID =" + pUtil.getGfTeamId() + ") s,ORG_UNIT u where s.member_id=u.id ";
        String unitSql = "select id,name,path from org_unit where id=?";
        Connection connection = JDBCAgent.getRawConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> unitName = new ArrayList<>();
        for (String s : sendtoidArr) {
            String sId = s.split("\\|")[1];
            if (sId.equals(pUtil.getGfTeamId())) {
                List<Map<String, Object>> mapList = JDBCUtil.doQuery(getTeamMember);
                for (int i = 0; i < mapList.size(); i++) {
                    unitName.add((String) mapList.get(i).get("name"));
                }
            } else {
                ps = connection.prepareStatement(unitSql);
                ps.setString(1, sId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String path = rs.getString("path");
                    String p = path.substring(0, 12);
                    if (path.length() == 16 && p.equals(pUtil.getGfPath())) {
                        unitName.add(rs.getString("name"));
                    }
                }
            }

        }


        if (unitName.size() > 0) {
            String sql = "select id,reference,filename,file_url,mime_type,attachment_size,createdate from CTP_ATTACHMENT where reference =" + summaryId;
            List<Map<String, Object>> list = JDBCUtil.doQuery(sql);
            List<String> pathList = new ArrayList<>();

            List<Map<String, Object>> fjList = new ArrayList<>();
            if (list.size() > 0) {

                Map<String, Object> fjMap = null;
                String fileDownpath = "/seeyon/rest/attachment/file/";
                ProptiesUtil prop = new ProptiesUtil();
                String token = GetFwTokenUtil.getOaToken();
                for (int i = 0; i < list.size(); i++) {
                    BigDecimal bigDecimal = (BigDecimal) list.get(i).get("file_url");
                    fjMap = new HashMap<>();
                    String h = (String) list.get(i).get("filename");
                    String fileName = bigDecimal.longValue() + "" + h.substring(h.lastIndexOf("."));
                    String downloadUrl = prop.getOaUrl() + fileDownpath + bigDecimal.longValue() + "?fileName=" + fileName + "&token=" + token;
                    //                fjMap.put("filePath", "base64:" + Base64.getEncoder().encodeToString(downloadUrl.getBytes("utf-8")));
                    fjMap.put("filePath", downloadUrl);
                    fjMap.put("fileName", list.get(i).get("filename"));
                    fjList.add(fjMap);
                }
            }

            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("fieldName", "wjbt");
            map.put("fieldValue", edocSummary.getSubject().replaceAll("\r|\n", ""));
            mapList.add(map);
            map = new HashMap<>();
            map.put("fieldName", "rq");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = sdf.format(edocSummary.getStartTime());
            map.put("fieldValue", startDate);
            mapList.add(map);
            //下发单位
            map = new HashMap<>();
            String op = unitName.toString();
            map.put("fieldName", "xsdw");
            map.put("fieldValue", op.substring(1, op.length() - 1));
            mapList.add(map);

            map = new HashMap<>();
            map.put("fieldName", "lwdw1");
            map.put("fieldValue", edocSummary.getSendDepartment());
            mapList.add(map);
            map = new HashMap<>();
            map.put("fieldName", "lwh");
            map.put("fieldValue", !"".equals(edocSummary.getDocMark()) && null != edocSummary.getDocMark() ? edocSummary.getDocMark() : "");
            mapList.add(map);
            //附件问题
            Map<String, Object> fj = new HashMap<>();
            fj.put("fieldName", "wjzw");
            fj.put("fieldValue", fjList);
            mapList.add(fj);


            Map<String, String> param = new HashMap<>();
            param.put("requestName", "集团发文");
            param.put("workflowId", pUtil.getWorkflowId());
            param.put("mainData", JSONArray.fromObject(mapList).toString());

//        Map<String, Object> otherParams = new HashMap<>();
//        otherParams.put("isnextflow ", "1");
//        otherParams.put("delReqFlowFaild ", "1");
//
//        param.put("otherParams", otherParams.toString());


            String address = pUtil.getServerUrl();
            Map<String, Object> objectMap = GetFwTokenUtil.testRegist(address);
            String token = GetFwTokenUtil.testGetoken(objectMap);
            String appId = pUtil.getAppId();
            String spk = StrUtil.nullToEmpty((String) objectMap.get("spk"));
            RSA rsa = new RSA(null, spk);
            String userId = rsa.encryptBase64(pUtil.getSendUserId(), CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
            Map<String, String> headers = new HashMap<>();
            headers.put("appid", appId);
            headers.put("token", token);
            headers.put("userid", userId);
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            String url = address + pUtil.getDocreate();
            String back1 = HttpClient.httpPostForm(url, param, headers, "utf-8");
            System.out.println("返回结果：" + back1);
            JSONObject object = JSONObject.parseObject(back1);
            Map data = (Map) object.get("data");
            String requestid = data.get("requestid").toString();
            System.out.println(requestid);
            String insertsql = "insert into temp_fw_requrid_id(summary_id,fw_id) values(?,?)";
            try {
                ps = connection.prepareStatement(insertsql);
                ps.setString(1, summaryId + "");
                ps.setString(2, requestid);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeUtil(connection, ps, rs);
            }
        }
    }

    /**
     * 方法说明：此方法是根据机构组来获取泛微人员信息的
     * <p>
     * 获取属于股份公司的机构中机要员的id，根据id获取泛微系统在人员信息中维护的id
     * <p>
     * return：返回结果：获取到的泛微人员id的集合
     */
    public List<String> getFwUserIdList(String sendToId, boolean flag) {
        ProptiesUtil proptiesUtil = new ProptiesUtil();
        List<String> fwUserIdList = new ArrayList<>();

        if (flag) {
            //返回单位的id、描述信息
            String sql = "select  nvl(DESCRIPTION,'-') description,id from ORG_UNIT where id in (select u.id from (select * from EDOC_OBJ_TEAM_MEMBER where TEAM_ID =" + proptiesUtil.getGfTeamId() + ") s,ORG_UNIT u where s.member_id=u.id )";
            List<Map<String, Object>> maps = JDBCUtil.doQuery(sql);
            for (int i = 0; i < maps.size(); i++) {
                Map<String, Object> map = maps.get(i);
                String type = (String) map.get("description");
                //根据单位描述的内容区分该单位是否属于股份公司
                if (type.equals("股份公司")) {
                    String unitId = map.get("id") + "";
                    //根据单位id关联“单位机要员”，根据单位机要员id关联人员信息中扩展字段“泛微人员编号”，返回“泛微人员编号”字段对应的内容
                    String getFwUserIdSql = "SELECT nvl(ext_attr_2,'0') fwuserid from ADDRESSBOOK where member_id in (select  SOURCE_ID  from ORG_RELATIONSHIP where OBJECTIVE0_ID=" + unitId + " and OBJECTIVE1_ID=(select id from org_role where ORG_ACCOUNT_ID=" + unitId + " and IS_ENABLE=1 and code ='UnitJyy'))";
                    List<Map<String, Object>> fwUserIds = JDBCUtil.doQuery(getFwUserIdSql);
                    for (int j = 0; j < fwUserIds.size(); j++) {
                        Map<String, Object> fwUserIdMap = fwUserIds.get(i);
                        //获取维护的泛微的人员编号
                        String fwuserid = fwUserIdMap.get("fwuserid") + "";
                        if (!"0".equals(fwuserid)) {
                            fwUserIdList.add(fwuserid);
                        }
                    }
                }
            }
        }

        return fwUserIdList;
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


    public FileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public EdocManager getEdocManager() {
        return edocManager;
    }
}
