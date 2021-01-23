package com.seeyon.cap4.form.modules.engin.base.formData;

import com.seeyon.apps.collaboration.constants.ColConstant;
import com.seeyon.apps.collaboration.enums.ColHandleType;
import com.seeyon.apps.collaboration.enums.CollaborationEnum;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.index.manager.IndexManager;
import com.seeyon.apps.taskmanage.api.TaskmanageApi;
import com.seeyon.cap4.common.content.mainbody.MainbodyService;
import com.seeyon.cap4.form.bean.FormAuthViewBean;
import com.seeyon.cap4.form.bean.FormAuthViewFieldBean;
import com.seeyon.cap4.form.bean.FormAuthorizationTableBean;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormBindAuthBean;
import com.seeyon.cap4.form.bean.FormBindBean;
import com.seeyon.cap4.form.bean.FormConditionActionBean;
import com.seeyon.cap4.form.bean.FormDataBean;
import com.seeyon.cap4.form.bean.FormDataMasterBean;
import com.seeyon.cap4.form.bean.FormDataSubBean;
import com.seeyon.cap4.form.bean.FormFieldBean;
import com.seeyon.cap4.form.bean.FormFieldComEnum;
import com.seeyon.cap4.form.bean.FormFormulaBean;
import com.seeyon.cap4.form.bean.FormFormulaBean.FormulaBaseBean;
import com.seeyon.cap4.form.bean.FormFormulaBean.FormulaDataFieldBean;
import com.seeyon.cap4.form.bean.FormFormulaBean.FormulaFunctionBean;
import com.seeyon.cap4.form.bean.FormFormulaBean.FormulaValueBean;
import com.seeyon.cap4.form.bean.FormQueryResult;
import com.seeyon.cap4.form.bean.FormQueryTypeEnum;
import com.seeyon.cap4.form.bean.FormQueryWhereClause;
import com.seeyon.cap4.form.bean.FormRelationshipBean;
import com.seeyon.cap4.form.bean.FormRelationshipMapBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.cap4.form.bean.FormTriggerBean;
import com.seeyon.cap4.form.bean.FormTriggerBean.TriggerBusinessType;
import com.seeyon.cap4.form.bean.FormViewBean;
import com.seeyon.cap4.form.bean.SimpleObjectBean;
import com.seeyon.cap4.form.bean.fieldCtrl.FormFieldCtrl;
import com.seeyon.cap4.form.bean.fieldCtrl.FormFieldCustomCtrl;
import com.seeyon.cap4.form.bean.fieldCtrl.FormFieldUtil;
import com.seeyon.cap4.form.modules.engin.authorization.CAP4FormAuthDesignManager;
import com.seeyon.cap4.form.modules.engin.base.formData.event.CapFormInsertDataEvent;
import com.seeyon.cap4.form.modules.engin.bind.CAP4FormBindDesignManager;
import com.seeyon.cap4.form.modules.engin.formula.FormulaEnums;
import com.seeyon.cap4.form.modules.engin.formula.FormulaEnums.DataFieldType;
import com.seeyon.cap4.form.modules.engin.formula.FormulaEnums.FormulaVar;
import com.seeyon.cap4.form.modules.engin.formula.FormulaEnums.FunctionSymbol;
import com.seeyon.cap4.form.modules.engin.formula.FormulaEnums.SystemDataField;
import com.seeyon.cap4.form.modules.engin.formula.FormulaFunctionUitl;
import com.seeyon.cap4.form.modules.engin.formula.FormulaUtil;
import com.seeyon.cap4.form.modules.engin.formula.validate.Stack;
import com.seeyon.cap4.form.modules.engin.relation.CAP4FormRelationActionManager;
import com.seeyon.cap4.form.modules.engin.relation.CAP4FormRelationRecordDAO;
import com.seeyon.cap4.form.modules.engin.trigger.CAP4FormTriggerManager;
import com.seeyon.cap4.form.service.CAP4FormCacheManager;
import com.seeyon.cap4.form.service.CAP4FormManager;
import com.seeyon.cap4.form.util.Enums;
import com.seeyon.cap4.form.util.Enums.FieldAccessType;
import com.seeyon.cap4.form.util.Enums.FieldType;
import com.seeyon.cap4.form.util.Enums.FlowDealOptionsType;
import com.seeyon.cap4.form.util.Enums.FormDataRatifyFlagEnum;
import com.seeyon.cap4.form.util.Enums.FormDataStateEnum;
import com.seeyon.cap4.form.util.Enums.FormLogOperateType;
import com.seeyon.cap4.form.util.Enums.FormType;
import com.seeyon.cap4.form.util.Enums.FromDataFinishedFlagEnum;
import com.seeyon.cap4.form.util.Enums.MasterTableField;
import com.seeyon.cap4.form.util.Enums.SubTableField;
import com.seeyon.cap4.form.util.Enums.UpdateDataType;
import com.seeyon.cap4.form.util.FormConstant;
import com.seeyon.cap4.form.util.FormLogUtil;
import com.seeyon.cap4.form.util.FormSearchUtil;
import com.seeyon.cap4.form.util.FormUtil;
import com.seeyon.cap4.form.util.StringUtils;
import com.seeyon.cap4.monitor.histories.HistoryObj;
import com.seeyon.cap4.monitor.histories.HistoryObjManager;
import com.seeyon.cap4.monitor.histories.ISaveData;
import com.seeyon.cap4.monitor.utils.CAP4MonitorUtil;
import com.seeyon.common.JDBCUtil;
import com.seeyon.common.ProptiesUtil;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.barCode.manager.BarCodeManager;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.constants.Constants;
import com.seeyon.ctp.common.content.affair.AffairManager;
import com.seeyon.ctp.common.content.affair.AffairUtil;
import com.seeyon.ctp.common.content.comment.Comment;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.lbs.manager.LbsManager;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.event.EventDispatcher;
import com.seeyon.ctp.form.modules.bind.FormLogManager;
import com.seeyon.ctp.form.modules.engin.relation.FormRelationRecordDAO;
import com.seeyon.ctp.form.modules.serialNumber.SerialCalRecordManager;
import com.seeyon.ctp.form.po.CtpFormula;
import com.seeyon.ctp.form.po.FormRelation;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.*;
import com.seeyon.ctp.workflow.wapi.WorkflowApiManager;
import com.seeyon.v3x.system.signet.domain.V3xHtmDocumentSignature;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.system.signet.manager.V3xHtmDocumentSignatManager;
import org.apache.commons.logging.Log;
import org.apache.taglibs.standard.functions.Functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CAP4FormDataManagerImpl implements CAP4FormDataManager {
    private static final Log LOGGER = CtpLogFactory.getLog(CAP4FormDataManagerImpl.class);
    private CAP4FormCacheManager cap4FormCacheManager;
    private CAP4FormDataDAO cap4FormDataDAO;
    private FormRelationRecordDAO formRelationRecordDAO;
    private FormLogManager formLogManager;
    private FileManager fileManager;
    private FileToExcelManager fileToExcelManager;
    private OrgManager orgManager;
    private AffairManager affairManager;
    private CAP4FormManager cap4FormManager;
    private CAP4FormAuthDesignManager CAP4FormAuthDesignManager;
    private LbsManager lbsManager;
    private IndexManager indexManager;
    private EnumManager enumManagerNew;
    private CAP4FormBindDesignManager cap4FormBindDesignManager;
    private SignetManager signetManager;
    private V3xHtmDocumentSignatManager htmSignetManager;
    private BarCodeManager barCodeManager;
    private AttachmentManager attachmentManager;
    private SerialCalRecordManager serialCalRecordManager;
    private CAP4FormRelationActionManager cap4FormRelationActionManager;
    private CAP4FormTriggerManager cap4FormTriggerManager;
    private CAP4FormRelationRecordDAO cap4FormRelationRecordDAO;
    private Pattern toDatePattern = Pattern.compile("to_date\\(([^()]*)\\)");
    /**
     * 审核节点权限常量
     */
    private static final String AUDIT = "formaudit";
    /**
     * 核定节点权限常量
     */
    private static final String vouch = "vouch";

    /**
     * 填单过程中所需计算方法
     *
     * @param form
     * @param resultFormFieldBean
     * @param cacheMasterData
     * @param subDataBean
     * @param fillBackFields
     * @param authViewBean
     * @param needDealSysRelation
     * @throws BusinessException
     */
    @Override
    public void calc4EditingForm(FormBean form, FormFieldBean resultFormFieldBean, FormDataMasterBean cacheMasterData, FormDataSubBean subDataBean, Set<String> fillBackFields, FormAuthViewBean authViewBean, boolean needDealSysRelation) throws BusinessException {
        String errorFormulaStr = "";
        Long recordId = (subDataBean == null) ? 0L : subDataBean.getId();
        try {
            String formulaType = FormulaEnums.getFormulaTypeByFieldType(resultFormFieldBean.getFieldType());
            //单元格计算之后的值
            Object value = null;
            //当前计算结果是否和缓存中的值一致，用于判断是否要刷新此单元格值改变所要影响的条件和公式计算
            boolean isChange = false;
            //计算的Map是通过FormFieldBean的数据类型确定
            Map<String, Object> params = cacheMasterData.getFormulaMap(formulaType);
            params.put("formDataBean", cacheMasterData);//sumif averif maxif minif所需使用参数
            params.put("formBean", form);
            FormDataBean dataBean = null;
            boolean isDelRow = false;
            if (!resultFormFieldBean.isMasterField() && subDataBean != null) {
                //如果计算的是明细表字段，但是传递的明细表行不是该字段所在行数据，则不做处理
                if (!resultFormFieldBean.getOwnerTableName().equals(subDataBean.getFormTable().getTableName())) {
                    return;
                }
                dataBean = subDataBean;
                if (dataBean != null) {
                    //子表行内的计算要添加本行的数据
                    params.putAll(dataBean.getFormulaMap(formulaType));
                    params.put("subDataBean", dataBean);
                } else {//删除行的时候dataBean已经被移除缓存了,如果结果字段是子表行字段就不需要进行计算本行字段的计算了
                    isDelRow = true;
                }
            } else {
                dataBean = cacheMasterData;
            }

            if (!isDelRow) {
                FormFormulaBean formulaBean = getFormFormulaBean(form, cacheMasterData, resultFormFieldBean, subDataBean, resultFormFieldBean.isMasterField());
                if (null == formulaBean) {
                    return;
                }
                String forumlaStr = formulaBean.getExecuteFormulaForGroove();
                //对计算公式做一些特殊替换处理
                forumlaStr = replaceFormulaStr(resultFormFieldBean, formulaType, forumlaStr, form, cacheMasterData, subDataBean == null ? 0L : subDataBean.getId(), params);

                // 将计算式类型放入到参数中
                params.put("formulaType", formulaType);
                errorFormulaStr = "数据：" + cacheMasterData.getId() + " 字段：" + resultFormFieldBean.getName() + resultFormFieldBean.getDisplay() + " forumlaStr:" + forumlaStr;
                value = FormulaUtil.doResult(forumlaStr, params);//更改cacheMasterData中对应于resultFormFieldBean的值为value
                value = FormUtil.formatCalcValue(resultFormFieldBean, value, formulaType);
                Object oldVal = dataBean.getFieldValue(resultFormFieldBean.getName());//注释部分代码解决bug CAPF-14184
                if (null == oldVal) {
                    oldVal = "";
                }
                if (!String.valueOf(value).equals(String.valueOf(oldVal))) {
                    isChange = true;
                }
                if (isPreRow(resultFormFieldBean) && "".equals(value)) {
                    isChange = false;
                }
                // 20180930 增加，当isChange=true时才调用，处理前端回填。否则场景会出问题：计算结果字段是系统关联条件字段，根据fillBackFields判断的是否改变去触发系统关联，否则条件没变就去算了系统关联，导致修改的映射值又被覆盖
                if (isChange) dataBean.addFieldValue(resultFormFieldBean.getName(), value, true, fillBackFields);
            }
            if (isChange) {
                if (resultFormFieldBean.isMasterField()) {
                    //如果是主表字段参与的计算，需要将子表行id修改成0或者空，这样后续会依据此值来判断是否要循环计算重复行
                    calcAllWithFieldIn(form, resultFormFieldBean, cacheMasterData, null, fillBackFields, authViewBean, needDealSysRelation);
                } else {
                    calcAllWithFieldIn(form, resultFormFieldBean, cacheMasterData, subDataBean, fillBackFields, authViewBean, needDealSysRelation);
                }
            }

        } catch (StackOverflowError e) {
            LOGGER.error(errorFormulaStr + "  " + e.getMessage(), e);
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(errorFormulaStr + "  " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 刷新某条表单数据的全部初始值、表内关联、计算、表间关联。
     * 注意：在调用此方法之前不要处理数据的初始值、表内关联、表间关联、计算等，否则会有值变化之后没有刷新关联的情况存在。
     *
     * @param formBean            表单定义
     * @param masterData          待刷新的数据对象
     * @param auth                权限
     * @param needFillBack        是否要构造返回值
     * @param needCalSN           是否需要计算流水号
     * @param needDealSysRelation 是否需要刷新系统条件类型的关联表单
     * @param dealDefaultVal      是否需要带出初始值
     * @return
     * @throws BusinessException
     */
    @Override
    public Set<String> calcAll(FormBean formBean, FormDataMasterBean masterData, FormAuthViewBean auth, boolean needFillBack, boolean needCalSN, boolean needDealSysRelation, boolean dealDefaultVal) throws BusinessException {
        Long start = System.currentTimeMillis();
        Set<String> fillBackFields = null;
        if (needFillBack) {
            fillBackFields = new HashSet<String>();
        }
        Map<String, Map<String, Boolean>> changeTag = null;
        //新建的时候FormDataBean中每个字段的值给了默认空值，也算数据变化
        Object o = AppContext.getThreadContext(FormConstant.fieldChangeTag);
        if (null != o) {
            changeTag = (Map<String, Map<String, Boolean>>) o;
        } else {
            changeTag = new HashMap<String, Map<String, Boolean>>();
            //添加线程变量，在FormDataBean的addFieldValue中判断字段在此次线程请求中是否发生改变。
            AppContext.putThreadContext(FormConstant.fieldChangeTag, changeTag);
        }
        Map<String, Object> routeMap = new HashMap<String, Object>();
        try {
            //主表结构
            FormTableBean masterTableBean = formBean.getMasterTableBean();
            List<FormFieldBean> masterFields = masterTableBean.getFields();
            for (FormFieldBean field : masterFields) {
                calc(formBean, masterData, auth, field, null, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
            }
            Map<String, List<FormDataSubBean>> subDataBeans = masterData.getSubTables();
            Iterator<String> it = subDataBeans.keySet().iterator();
            while (it.hasNext()) {
                String tName = it.next();
                List<FormDataSubBean> subRows = subDataBeans.get(tName);
                if (null != subRows && subRows.size() > 0) {
                    FormTableBean subTableBean = formBean.getTableByTableName(tName);
                    List<FormFieldBean> subFields = subTableBean.getFields();
                    if (subFields.size() > 0) {
                        for (FormDataSubBean subRow : subRows) {
                            for (FormFieldBean subField : subFields) {
                                calc(formBean, masterData, auth, subField, subRow, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new BusinessException(e);
        } finally {
            //方法最后移除字段变化标记
            AppContext.removeThreadContext(FormConstant.fieldChangeTag);
        }
        Long end = System.currentTimeMillis();
        LOGGER.info("calcAll总时间：" + (end - start));

        return fillBackFields;
    }

    /**
     * CAP4表单计算全部接口的单个字段计算接口，此接口会递归计算表内关联、表间关联、计算公式
     * ！！！注意，此接口中如果牵涉到修改FormDataMasterBean数据对象中的值，一定要调用FormDataBean中的public void addFieldValue(String fieldName, Object fieldValue,Set<String> fillBackFields)方法
     *
     * @param formBean            表单定义信息
     * @param data                数据对象
     * @param auth                权限
     * @param resultField         当前计算那个字段
     * @param subData             重复表数据行对象
     * @param routeMap            路由map
     * @param fillBackFields      存储需要回填的字段名
     * @param dealDefaultVal      是否需要处理初始值
     * @param needCalSN           是否需要计算流水号
     * @param needDealSysRelation 是否需要刷新关联表单
     * @throws BusinessException
     */
    private void calc(FormBean formBean, FormDataMasterBean data, FormAuthViewBean auth, FormFieldBean resultField, FormDataSubBean subData, Map<String, Object> routeMap, Set<String> fillBackFields, boolean dealDefaultVal, boolean needCalSN, boolean needDealSysRelation) throws BusinessException {
        boolean currentIsMaster = resultField.isMasterField();
        String fieldName = resultField.getName();
        if ((!currentIsMaster && null == subData) || (currentIsMaster && null == data)) {
            LOGGER.error("调用计算方法calc参数传递错误，当前计算结果字段是" + (currentIsMaster ? "主表" : "明细表") + "字段，但是传入的数据对象为空！");
            if (!routeMap.containsKey(fieldName)) {
                routeMap.put(fieldName, null);
            }
            return;
        }
        String routeKey = currentIsMaster ? fieldName : subData.generageFillbackKey(fieldName);
        if (!routeMap.containsKey(routeKey)) {
            //不包含才计算，如果已经包含，说明已经计算过，不再计算
            routeMap.put(routeKey, null);
            FormDataBean currentData = currentIsMaster ? data : subData;
            /****************************初始值处理start************************************/
            //通过参数dealDefaultVal判断是否处理初始值
            if (dealDefaultVal && null != auth) {
                dealFieldDefaultVal(currentData, auth, needCalSN, resultField, fillBackFields);
            }
            /****************************初始值处理end************************************/
            /****************************表内关联处理start************************************/
            FormRelation relation = resultField.getFormRelation();
            if (relation != null) {
                FormFieldBean fromFieldBean = formBean.getFieldBeanByName(relation.getToRelationAttr());
                calc(formBean, data, auth, fromFieldBean, subData, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
                if (resultField.getInputTypeEnum().getKey().equals(FormFieldComEnum.SELECT.getKey())) {
                    AppContext.putThreadContext(FormConstant.isFrom, FormConstant.calcAll);
                    cap4FormRelationActionManager.dealEnumRelation(formBean, data, auth, resultField, subData, fillBackFields, needDealSysRelation, null);
                    AppContext.removeThreadContext(FormConstant.isFrom);
                } else {
                    if (cap4FormRelationActionManager.getInnerRelationValueChanged(fromFieldBean, subData)) {
                        FormFieldBean toFormFieldBean = formBean.getFieldBeanByName(relation.getToRelationAttr());
                        AppContext.putThreadContext(FormConstant.isFrom, FormConstant.calcAll);
                        cap4FormRelationActionManager.dealRelationByType(formBean, data, auth, resultField, subData, fillBackFields, toFormFieldBean.getPossibleRelationAttrType(), needDealSysRelation);
                        AppContext.removeThreadContext(FormConstant.isFrom);
                    }
                }
            }
            /****************************表内关联处理end************************************/
            /****************************表间关联处理start************************************/
            if (needDealSysRelation && cap4FormRelationActionManager.isSysRelation(formBean.getId(), resultField.getName())) {
                FormRelationshipBean formRelationshipBean = cap4FormRelationActionManager.getFormRelationshipBeanByFieldName(formBean.getId(), fieldName);
                FormRelationshipMapBean formRelationshipMapBean = formRelationshipBean.getRelationMapList().get(0);
                Map<String, String> contionFields = cap4FormRelationActionManager.getRelationFormulaFieldMap(formRelationshipMapBean.getConditionFormula());
                for (String contionField : contionFields.keySet()) {
                    FormFieldBean contionFieldBean = formBean.getFieldBeanByName(contionField);
                    boolean contionFieldIsMaster = contionFieldBean.isMasterField();
                    if (contionFieldIsMaster) {
                        calc(formBean, data, auth, contionFieldBean, subData, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
                    } else {
                        List<FormDataSubBean> subDatas = data.getSubTables().get(contionFieldBean.getOwnerTableName());
                        if (subDatas != null) {
                            for (FormDataSubBean tempSubData : subDatas) {
                                calc(formBean, data, auth, contionFieldBean, tempSubData, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
                            }
                        }
                    }
                }
                Map<String, Map<String, Boolean>> changeFields = (Map<String, Map<String, Boolean>>) AppContext.getThreadContext(FormConstant.fieldChangeTag);
                for (String contionField : contionFields.keySet()) {
                    FormFieldBean contionFieldBean = formBean.getFieldBeanByName(contionField);
                    Map<String, Boolean> changeField = changeFields.get(contionFieldBean.getOwnerTableName());
                    // changeFields中是从表时，其key是字段名和数据id组合，contionField字段是没有从表数据id
                    boolean hasContainField = false;
                    if (changeField != null) {
                        for (String changeFieldName : changeField.keySet()) {
                            if (changeFieldName.startsWith(contionField)) {
                                hasContainField = true;
                                break;
                            }
                        }
                    }
                    if (hasContainField || cap4FormRelationActionManager.forceDealSysRelation(resultField, auth)) {
                        AppContext.putThreadContext(FormConstant.isFrom, FormConstant.calcAll);
                        cap4FormRelationActionManager.dealSysRelation(formBean, data, auth, resultField, subData, fillBackFields);
                        AppContext.removeThreadContext(FormConstant.isFrom);
                        Map<String, String> formRelationFieldMap = formRelationshipMapBean.getFormRelationFieldMap();
                        for (String fromField : formRelationFieldMap.keySet()) {
                            FormFieldBean tempField = formBean.getFieldBeanByName(fromField);
                            boolean tempFieldIsmaster = tempField.isMasterField();
                            if (tempFieldIsmaster) {
                                routeMap.put(fromField, null);
                            } else {
                                if (subData == null) {
                                    routeMap.put(fromField, null);
                                } else {
                                    routeMap.put(subData.generageFillbackKey(fromField), null);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            /****************************表间关联处理end************************************/
            /****************************公式计算处理start************************************/
            boolean isCalcResultField = !Strings.isEmpty(resultField.getFormConditionList());
            if (isCalcResultField) {//是计算结果字段
                //解析普通计算公式中的字段列表fields
                Set<FormFieldBean> inFormulaFields = resultField.getInFormulaFields();
                for (FormFieldBean tempField : inFormulaFields) {
                    boolean tempFieldIsmaster = tempField.isMasterField();
                    if ((currentIsMaster && tempFieldIsmaster) || ((!currentIsMaster) && tempFieldIsmaster) || ((!currentIsMaster) && (!tempFieldIsmaster))) {//(当前主-参与者是主)或者(当前从，参与者是主)或者(当前从，参与者从)
                        calc(formBean, data, auth, tempField, subData, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
                    } else if (currentIsMaster && !tempFieldIsmaster) {//当前主，参与者是从
                        String subTableName = tempField.getOwnerTableName();
                        List<FormDataSubBean> subDatas = data.getSubTables().get(subTableName);
                        for (FormDataSubBean tempSubData : subDatas) {
                            calc(formBean, data, auth, tempField, tempSubData, routeMap, fillBackFields, dealDefaultVal, needCalSN, needDealSysRelation);
                        }
                    }
                }
                //结果字段类型得到公式计算类型
                String formulaType = FormulaEnums.getFormulaTypeByFieldType(resultField.getFieldType());
                FormFormulaBean formulaBean = getFormFormulaBean(formBean, data, resultField, subData, currentIsMaster);
                if (null == formulaBean) {
                    return;
                }
                String forumlaStr = formulaBean.getExecuteFormulaForGroove();
                //获取计算式参数Map
                Map<String, Object> param = data.getFormulaMap(formulaType);
                //sumif averif maxif minif所需使用参数
                param.put("formDataBean", data);
                param.put("formBean", formBean);
                //如果是明细表字段，参数中需要加上该行的明细表数据
                if (!currentIsMaster && null != subData) {
                    param.putAll(subData.getFormulaMap(formulaType));
                    param.put("subDataBean", subData);
                }
                //对计算公式做一些特殊替换处理
                forumlaStr = replaceFormulaStr(resultField, formulaType, forumlaStr, formBean, data, subData == null ? 0L : subData.getId(), param);
                param.put("formulaType", formulaType);
                Object result = FormulaUtil.doResult(forumlaStr, param);
                result = FormUtil.formatCalcValue(resultField, result, formulaType);
                if (!currentIsMaster) {
                    subData.addFieldValue(fieldName, result, true, fillBackFields);
                } else {
                    data.addFieldValue(fieldName, result, true, fillBackFields);
                }
            }
            //此字段不是计算结果字段也不是关联字段
            routeMap.put(routeKey, data.getFieldValue(fieldName));
            //如果当前字段不是初始值、计算结果、关联结果并且数据对象中也不存在这个值，则放空，否则如果当前字段参与了其他字段的计算会取不到应有的值
            if (!currentData.containsField(fieldName)) {
                currentData.addFieldValue(fieldName, null, false, fillBackFields);
            }
            /****************************公式计算处理end************************************/
        }
    }

    /**
     * 获取字段的计算公式bean，因为公式分普通计算和高级计算，高级计算需要通过groovy计算一次
     *
     * @param formBean
     * @param data
     * @param resultField
     * @param subData
     * @param currentIsMaster
     * @return
     * @throws BusinessException
     */
    private FormFormulaBean getFormFormulaBean(FormBean formBean, FormDataMasterBean data, FormFieldBean resultField, FormDataSubBean subData, boolean currentIsMaster) throws BusinessException {
        boolean isOrdinaryFormula = resultField.isOrdinaryFormula();
        FormFormulaBean formulaBean = null;
        if (isOrdinaryFormula) {//普通计算公式
            //取得普通计算公式formulaStr
            formulaBean = resultField.getFormulaBean(null);
        } else {
            //通过MasterdataBean构造groovy参数 用来计算高级公式中的条件，应该用哪个计算结果公式做计算
            Map<String, Object> conditionMap = data.getFormulaMap(FormulaEnums.componentType_condition);
            conditionMap.put("formDataBean", data);
            conditionMap.put("formBean", formBean);
            //如果是明细表字段，参数中需要加上该行的明细表数据
            if (!currentIsMaster && null != subData) {
                conditionMap.putAll(subData.getFormulaMap(FormulaEnums.componentType_condition));
            }
            formulaBean = resultField.getFormulaBean(conditionMap);
        }
        return formulaBean;
    }

    /**
     * 给单个字段生成初始值
     *
     * @param formData  数据对象，如果是主表字段，需要传入FormDataMasterBean，如果是明细表字段，需要传入FormDataSubBean
     * @param auth      权限
     * @param needCalSN 是否需要生成流水号
     * @param field     需要生成初始值的字段
     * @throws BusinessException
     */
    @Override
    public void procFieldDefaultVal(FormDataBean formData, FormAuthViewBean auth, boolean needCalSN, FormFieldBean field) throws BusinessException {
        dealFieldDefaultVal(formData, auth, needCalSN, field, null);
    }

    /**
     * 主从表字段生成初始值
     *
     * @param formData
     * @param auth
     * @param needCalSN
     * @param field
     * @param fillBackFields
     * @throws BusinessException
     */
    private void dealFieldDefaultVal(FormDataBean formData, FormAuthViewBean auth, boolean needCalSN, FormFieldBean field, Set<String> fillBackFields) throws BusinessException {
        String fieldName = field.getName();
        FormAuthViewFieldBean fieldAuth = auth.getFormAuthorizationField(fieldName);
        if (fieldAuth != null) {
            //赋初始值的时候不赋流水号默认值，流水号的初始值在保存时获取
            Object value = formData.getFieldValue(fieldName);
            boolean forceDefault = (fieldAuth.getIsInitNull() == 1);//根据是否强制初始值来判断
            boolean isSerialField = fieldAuth.isSerialNumberDefaultValue();//是否是流水号初始值
            if (forceDefault || (needCalSN && isSerialField) || (fieldAuth.getDefaultValue() != null && value == null && Strings.isNotBlank(fieldAuth.getAccess()) && !fieldAuth.getAccess().equals(FieldAccessType.design.getKey()) && !isSerialField)) {
                String[] defaultValue = fieldAuth.getValue();
                if (forceDefault || !StringUtil.checkNull(String.valueOf(defaultValue[0]))) {
                    value = defaultValue[0];
                    formData.addFieldValue(fieldName, field.getDefaultVal4Db(value), fillBackFields);
                }
                //需要初始值的情况，生成一个，这里需要谨慎使用，因为流水号生成了，就不能复原了，禁止那些加载的时候传参数为true来生成流水号
                //目前这个只用于无流程不弹出窗口修改数据的情况
                if (needCalSN && isSerialField && StringUtil.checkNull(String.valueOf(value))) {
                    String serialValue = fieldAuth.getSerialNumber();
                    //这里报错就报错吧，如果客户要求数据修复就更麻烦了。。。报错就让客户把流水号初始值字段长度改大吧。
                    if (FormFieldComEnum.TEXTAREA != field.getInputTypeEnum() && serialValue.length() > field.getMaxLength(false)) {
                        value = serialValue.substring(0, field.getMaxLength(true));
                        formData.addFieldValue(fieldName, field.getDefaultVal4Db(value), fillBackFields);
                    } else {
                        value = serialValue;
                        formData.addFieldValue(fieldName, field.getDefaultVal4Db(value), fillBackFields);
                    }
                    LOGGER.info("字段：" + field.getDisplay() + "(" + fieldName + ")" + "通过权限:" + auth.getName() + "(" + auth.getId() + ")产生流水号 : " + formData.getFieldValue(fieldName));
                }
            }
        }
    }

    /**
     * @param resultField
     * @param formulaType
     * @param forumlaStr
     * @param formBean
     * @param cacheMasterData
     * @param recordId
     * @param param
     * @return
     * @throws BusinessException
     */
    private String replaceFormulaStr(FormFieldBean resultField, String formulaType, String forumlaStr, FormBean formBean, FormDataMasterBean cacheMasterData, Long recordId, Map<String, Object> param) throws BusinessException {
        //动态组合单个字段变量需要自己做替换处理，为了满足现实格式，然后再给groovey执行
        if (formulaType.equals(FormulaEnums.formulaType_varchar)) {
            String[] strs = forumlaStr.split("\\+");
            for (String str : strs) {
                str = str.trim();
                FormFieldBean field = formBean.getFieldBeanByName(str);
                if (field != null) {
                    Object val = null;
                    if (field.isMasterField()) {
                        val = cacheMasterData.getFieldValue(str);
                    } else {
                        val = cacheMasterData.getSubDataMapById(field.getOwnerTableName(), recordId).get(field.getName());
                    }
                    String valueStr = String.valueOf(field.getDisplayValue(val, false)[1]);
                    valueStr = valueStr.replace("\\", "\\\\").replace("'", "\\'");
                    valueStr = valueStr.replaceAll("\r", "@@SEEYON_V5_huiche@@");
                    valueStr = valueStr.replaceAll("\n", "@@SEEYON_V5_huanghang@@");
                    if (valueStr.contains("script")) {
                        valueStr = valueStr.replaceAll("script", "@seeyon_script");
                    }
                    param.put(field.getName(), valueStr);
                }
            }
            //动态组合必须先程序计算所有to_date函数以保证日期是字符串类型才能进行动态组合
            if (forumlaStr.indexOf(FunctionSymbol.to_date.getKey()) >= 0) {
                Matcher matcher = toDatePattern.matcher(forumlaStr);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String group1 = matcher.group(1);
                    matcher.appendReplacement(sb, group1);
                }
                matcher.appendTail(sb);
                forumlaStr = sb.toString();
            }
        }
        //没有流水号的运算式，要特殊处理，因为有些地方需要立刻计算，有些地方需要事后计算
        int hasSN = forumlaStr.indexOf(DataFieldType.serialNumber.getKey());
        if (hasSN >= 0) {//记录下计算式里含有流水号的;这里通过线程变量记录一下字段的名称、表单id以及数据id,格式:字段名称_表单id_数据id_字表数据id
            String tempSerialData = null;
            //如果是主表字段，但是传入了recordId，则再处理key的时候需要记录的是0，而不是传入的recordId，这种在新增、删除重复行的时候刷新计算就会出现这种情况
            //OA-128417 主表的流水号动态组合字段又参与了重复表的动态组合，重复表增、减行，会导致主表流水号动态组合消失。
            if (resultField.isMasterField()) {
                tempSerialData = resultField.getName() + FormConstant.DOWNLINE
                        + formBean.getId() + FormConstant.DOWNLINE + cacheMasterData.getId()
                        + FormConstant.DOWNLINE + "0";
            } else {
                tempSerialData = resultField.getName() + FormConstant.DOWNLINE
                        + formBean.getId() + FormConstant.DOWNLINE + cacheMasterData.getId()
                        + FormConstant.DOWNLINE + recordId;
            }
            AppContext.putThreadContext(FormConstant.serialCalFunctionKey, tempSerialData);
        }
        return forumlaStr;
    }

    /**
     * 计算出给定单元格在表单中所参与的所有计算结果
     *
     * @param form                表单定义FormBean
     * @param formFieldBean       变化字段
     * @param cacheMasterData     表单数据对象
     * @param formDataSubBean
     * @param fillBackFields      如果需要回填，需要传入用来回填的set
     * @param authViewBean        权限
     * @param needDealSysRelation 是否需要刷新关联
     * @throws BusinessException
     */
    @Override
    public void calcAllWithFieldIn(FormBean form, FormFieldBean formFieldBean, FormDataMasterBean cacheMasterData,
                                   FormDataSubBean formDataSubBean, Set<String> fillBackFields, FormAuthViewBean authViewBean, boolean needDealSysRelation)
            throws BusinessException {
        try {
            Long recordId = formDataSubBean == null ? 0L : formDataSubBean.getId();
            String changeFieldName = formFieldBean.getName();
            List<FormFieldBean> allFields = form.getAllFieldBeans();
            for (FormFieldBean tempFormField : allFields) {
                /************************判断方法传入字段是否参与了当前循环字段的计算，如果参与了则计算****************************/
                //如果表单单元格计算式中包含fieldName则进行计算，将计算结果保存在cacheMasterData中
                List<FormConditionActionBean> actions = tempFormField.getFormConditionList();
                if (actions != null && actions.size() > 0) {
                    if (FormulaUtil.isFieldExistFormula(actions, changeFieldName) && !changeFieldName.equalsIgnoreCase(tempFormField.getName())) {
                        if (tempFormField.isMasterField()) {
                            calc4EditingForm(form, tempFormField, cacheMasterData, formDataSubBean, fillBackFields, authViewBean, needDealSysRelation);
                        } else {
                            //如果传递了行号，则只就算行号那行，否则计算全部行
                            if (recordId != null && recordId.longValue() != -1L && recordId.longValue() != 0L) {
                                if (isPreRow(tempFormField)) {
                                    List<FormDataSubBean> subDataBeans = cacheMasterData.getSubData(tempFormField.getOwnerTableName());
                                    for (FormDataSubBean subDataBean : subDataBeans) {
                                        calc4EditingForm(form, tempFormField, cacheMasterData, subDataBean, fillBackFields, authViewBean, needDealSysRelation);
                                    }
                                } else {
                                    calc4EditingForm(form, tempFormField, cacheMasterData, formDataSubBean, fillBackFields, authViewBean, needDealSysRelation);
                                }
                            } else {
                                List<FormDataSubBean> subDataBeans = cacheMasterData.getSubData(tempFormField.getOwnerTableName());
                                if (subDataBeans != null) {
                                    for (FormDataSubBean subDataBean : subDataBeans) {
                                        calc4EditingForm(form, tempFormField, cacheMasterData, subDataBean, fillBackFields, authViewBean, needDealSysRelation);
                                    }
                                }
                            }
                        }
                    }
                }
                /***********************计算end*******************************/
            }
            /*******************判断当前传入字段是否是循环字段的表内关联对象***********/
            if (formFieldBean.isInInnerRelation()) {
                if (formFieldBean.getInputTypeEnum().getKey().equals(FormFieldComEnum.SELECT.getKey())) {
                    cap4FormRelationActionManager.dealEnumRelation(form, cacheMasterData, authViewBean, formFieldBean, formDataSubBean, fillBackFields, needDealSysRelation, null);
                } else {
                    cap4FormRelationActionManager.dealRelationByType(form, cacheMasterData, authViewBean, formFieldBean, formDataSubBean, fillBackFields, formFieldBean.getPossibleRelationAttrType(), needDealSysRelation);
                }
            }
            /****************************表内关联处理end******************************/
            /*******************判断当前传入字段是否是循环字段的表间关联对象***********/
            if (formFieldBean.isInRelationCondition() && (needDealSysRelation || (fillBackFields != null && fillBackFields.contains((formFieldBean.isSubField() && formDataSubBean != null) ? formDataSubBean.generageFillbackKey(formFieldBean.getName()) : cacheMasterData.generageFillbackKey(formFieldBean.getName()))))) {
                // needDealSysRelation指定计算系统关联 或者 结果字段中有当前字段 才去刷新
                cap4FormRelationActionManager.dealSysRelation(form, cacheMasterData, authViewBean, formFieldBean, formDataSubBean, fillBackFields);
            }
            /****************************表间关联处理end******************************/
        } catch (StackOverflowError e) {
            LOGGER.error(e.getMessage(), e);
            throw new BusinessException(ResourceUtil.getString("form.data.formula.calc.error.label"));
        }
    }

    /**
     * 带条件的权限，条件满足之后处理权限的变更,高级权限
     *
     * @param formBean            表单bean
     * @param oldFormAuthViewBean 条件满足之后需要转换为的条件
     * @param formDataMasterBean  表单数据
     * @throws NumberFormatException
     * @throws BusinessException
     */
    @Override
    public Map<String, Map<String, String>> dealFormRightChangeResult(FormBean formBean, FormAuthViewBean oldFormAuthViewBean, FormDataMasterBean formDataMasterBean, Set<String> fillBackFields) throws NumberFormatException, BusinessException {
        Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
        Map<String, FormAuthViewFieldBean> apartAuthMap = null;
        //判断是否高级的时候还应该加上父权限ID是否为0
        if (oldFormAuthViewBean == null) {
            apartAuthMap = new HashMap<String, FormAuthViewFieldBean>();
            formDataMasterBean.getExtraMap().put("apartAuthMap", apartAuthMap);
            return resultMap;
        }
        Long pid = oldFormAuthViewBean.getParentId();
        FormAuthViewBean originalAuth;
        if (pid != null && pid != 0L && !oldFormAuthViewBean.isApartSetAuth()) {
            originalAuth = formBean.getAuthViewBeanById(pid);
        } else {
            originalAuth = oldFormAuthViewBean;
        }
        // 没有设置高级权限
        if (originalAuth.getAdvanceAuthType() == -1) {
            apartAuthMap = new HashMap<String, FormAuthViewFieldBean>();
            formDataMasterBean.getExtraMap().put("apartAuthMap", apartAuthMap);
            return resultMap;
        }
        // 获取上次计算缓存的权限
        if (formDataMasterBean.getExtraAttr("apartAuthMap") != null) {
            apartAuthMap = (Map<String, FormAuthViewFieldBean>) formDataMasterBean.getExtraAttr("apartAuthMap");
        } else {
            apartAuthMap = new HashMap<String, FormAuthViewFieldBean>();
            formDataMasterBean.getExtraMap().put("apartAuthMap", apartAuthMap);
        }
        // 增加判断权限上次平台与本次平台是否是一样，如果来回切换，需要清空缓存的权限，bug CAPF-12815，原样表单和原表单来回切换打开表单都是从新计算了高级权限
        FormViewBean formViewBean = formBean.getFormView(oldFormAuthViewBean.getFormViewId());
        Object formViewType = formDataMasterBean.getExtraMap().get("formViewType");
        if (formViewType == null) {
            formDataMasterBean.getExtraMap().put("formViewType", formViewBean.getFormViewType());
        } else {
            if (!String.valueOf(formViewType).equals(formViewBean.getFormViewType())) {
                apartAuthMap = new HashMap<String, FormAuthViewFieldBean>();
                formDataMasterBean.getExtraMap().put("apartAuthMap", apartAuthMap);
                formDataMasterBean.getExtraMap().put("formViewType", formViewBean.getFormViewType());
            }
        }
        if (originalAuth != null) {
            Map<String, Object> formDataMap = formDataMasterBean.getFormulaMap(FormulaEnums.componentType_condition);
            // 计算后的新权限
            FormAuthViewBean newFormAuthViewBean = oldFormAuthViewBean.isApartSetAuth() ? originalAuth : originalAuth.getRightFormAuthViewBean(formDataMap);
            List<FormTableBean> subTableBeans = formBean.getSubTableBean();
            for (FormTableBean subTableBean : subTableBeans) {
                Map<String, String> subTableAuth = new HashMap<String, String>();
                FormAuthorizationTableBean authTableBean = newFormAuthViewBean.getSubTableAuth(subTableBean.getDisplay());
                subTableAuth.put("add", authTableBean.isAllowAdd() ? "1" : "0");
                subTableAuth.put("delete", authTableBean.isAllowDelete() ? "1" : "0");
                resultMap.put(subTableBean.getTableName(), subTableAuth);
            }
            //权限发生变化的时候将结果权限放到缓存中
            formDataMasterBean.putExtraAttr(FormConstant.viewRight, newFormAuthViewBean);
            // 是否满足条件改变权限，需使用一个缓存存上次时候计算过满足条件的权限，如果满足条件放进去，如果不满足拿出来做对比，如果里面没有，说明上一次也是不满足，不返回字段的权限信息
            if (oldFormAuthViewBean.getId().longValue() != newFormAuthViewBean.getId().longValue() || oldFormAuthViewBean.isApartSetAuth()) {
                List<FormFieldBean> fieldBeenList = formBean.getMasterTableBean().getFields();
                for (FormFieldBean fieldBean : fieldBeenList) {
                    Map<String, String> fieldAuth = new HashMap<String, String>();
                    //高级权限-分开设置情况
                    if (oldFormAuthViewBean.isApartSetAuth()) {
                        List<FormAuthViewFieldBean> subAuthConditions = newFormAuthViewBean.getConditionFieldAuthList(fieldBean.getName());
                        // 判断是否是分开设置字段
                        if (Strings.isNotEmpty(subAuthConditions)) {
                            FormAuthViewFieldBean advanceFieldAuthBean = newFormAuthViewBean.getFormAuthorizationField(fieldBean.getName(), formDataMap);
                            FormAuthViewFieldBean cacheAuth = apartAuthMap.get(fieldBean.getName());
                            // 与缓存中的做对比，如果存在，并且一致就不在返回
                            if (cacheAuth == null || (cacheAuth != null && (!cacheAuth.getAccess().equalsIgnoreCase(advanceFieldAuthBean.getAccess()) || cacheAuth.getIsNotNull() != advanceFieldAuthBean.getIsNotNull()))) {
                                fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, advanceFieldAuthBean.getAccess()));
                                fieldAuth.put("isNotNull", String.valueOf(advanceFieldAuthBean.getIsNotNull()));
                                resultMap.put(fieldBean.getName(), fieldAuth);
                                if (fillBackFields != null) {
                                    fillBackFields.add(fieldBean.getName());
                                }
                                apartAuthMap.put(fieldBean.getName(), advanceFieldAuthBean);
                            }
                            // 如果此字段参与其他计算，需要返回此字段权限信息
                            if (fillBackFields != null && fillBackFields.contains(fieldBean.getName())) {
                                fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, advanceFieldAuthBean.getAccess()));
                                fieldAuth.put("isNotNull", String.valueOf(advanceFieldAuthBean.getIsNotNull()));
                                resultMap.put(fieldBean.getName(), fieldAuth);
                                apartAuthMap.put(fieldBean.getName(), advanceFieldAuthBean);
                            }
                            // 流水号非隐藏权限，全部变成浏览权限
                            if (fieldBean.isSn() && !FieldAccessType.hide.getKey().equals(advanceFieldAuthBean.getAccess())) {
                                fieldAuth.put("auth", FieldAccessType.browse.getKey());
                                fieldAuth.put("isNotNull", "0");
                            }
                        }
                    } else {//高级权限-统一设置情况
                        FormAuthViewFieldBean formAuthViewFieldBean = newFormAuthViewBean.getFormAuthorizationField(fieldBean.getName());
                        FormAuthViewFieldBean cacheAuth = apartAuthMap.get(fieldBean.getName());
                        FormAuthViewFieldBean oldFormAuthViewFieldBean = cacheAuth == null ? oldFormAuthViewBean.getFormAuthorizationField(fieldBean.getName()) : cacheAuth;
                        if ((!oldFormAuthViewFieldBean.getAccess().equalsIgnoreCase(formAuthViewFieldBean.getAccess())) || (oldFormAuthViewFieldBean.getIsNotNull() != formAuthViewFieldBean.getIsNotNull())) {
                            fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                            fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                            resultMap.put(fieldBean.getName(), fieldAuth);
                            if (fillBackFields != null) {
                                fillBackFields.add(fieldBean.getName());
                            }
                            apartAuthMap.put(fieldBean.getName(), formAuthViewFieldBean);
                        }
                        if (fillBackFields != null && fillBackFields.contains(fieldBean.getName())) {
                            fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                            fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                            resultMap.put(fieldBean.getName(), fieldAuth);
                            apartAuthMap.put(fieldBean.getName(), formAuthViewFieldBean);
                        }
                        if (fieldBean.isSn() && !FieldAccessType.hide.getKey().equals(formAuthViewFieldBean.getAccess())) {
                            fieldAuth.put("auth", FieldAccessType.browse.getKey());
                            fieldAuth.put("isNotNull", "0");
                        }
                    }
                }
                Map<String, List<FormDataSubBean>> subMap = formDataMasterBean.getSubTables();
                for (FormTableBean subTable : formBean.getSubTableBean()) {
                    List<FormDataSubBean> subDataLine = subMap.get(subTable.getTableName());
                    if (subDataLine != null && subDataLine.size() > 0) {
                        List<FormFieldBean> subFieldList = subTable.getFields();
                        for (FormDataSubBean tempFormDataSubBean : subDataLine) {
                            Map<String, Object> dataMap = tempFormDataSubBean.getFormulaMap(FormulaEnums.componentType_condition);
                            for (FormFieldBean fieldBean : subFieldList) {
                                Map<String, String> fieldAuth = new HashMap<String, String>();
                                String authKey = fieldBean.getName() + "_" + tempFormDataSubBean.getId();
                                if (oldFormAuthViewBean.isApartSetAuth()) {//高级权限-分开设置情况
                                    formDataMap.putAll(dataMap);
                                    List<FormAuthViewFieldBean> subAuthConditions = newFormAuthViewBean.getConditionFieldAuthList(fieldBean.getName());
                                    // 判断是否是分开设置字段
                                    if (Strings.isNotEmpty(subAuthConditions)) {
                                        FormAuthViewFieldBean advanceFieldAuthBean = newFormAuthViewBean.getFormAuthorizationField(fieldBean.getName(), formDataMap);
                                        FormAuthViewFieldBean cacheAuth = apartAuthMap.get(authKey);
                                        if (cacheAuth == null || (cacheAuth != null && (!cacheAuth.getAccess().equalsIgnoreCase(advanceFieldAuthBean.getAccess()) || cacheAuth.getIsNotNull() != advanceFieldAuthBean.getIsNotNull()))) {
                                            fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, advanceFieldAuthBean.getAccess()));
                                            fieldAuth.put("isNotNull", String.valueOf(advanceFieldAuthBean.getIsNotNull()));
                                            resultMap.put(authKey, fieldAuth);
                                            if (fillBackFields != null) {
                                                fillBackFields.add(authKey);
                                            }
                                            apartAuthMap.put(authKey, advanceFieldAuthBean);
                                        }
                                        if (fillBackFields != null && fillBackFields.contains(authKey)) {
                                            fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, advanceFieldAuthBean.getAccess()));
                                            fieldAuth.put("isNotNull", String.valueOf(advanceFieldAuthBean.getIsNotNull()));
                                            resultMap.put(authKey, fieldAuth);
                                            apartAuthMap.put(authKey, advanceFieldAuthBean);
                                        }
                                        if (fieldBean.isSn() && !FieldAccessType.hide.getKey().equals(advanceFieldAuthBean.getAccess())) {
                                            fieldAuth.put("auth", FieldAccessType.browse.getKey());
                                            fieldAuth.put("isNotNull", "0");
                                        }
                                    }
                                } else {//高级权限-统一设置
                                    FormAuthViewFieldBean formAuthViewFieldBean = newFormAuthViewBean.getFormAuthorizationField(fieldBean.getName());
                                    FormAuthViewFieldBean cacheAuth = apartAuthMap.get(authKey);
                                    FormAuthViewFieldBean oldFormAuthViewFieldBean = cacheAuth == null ? oldFormAuthViewBean.getFormAuthorizationField(fieldBean.getName()) : cacheAuth;
                                    if ((!oldFormAuthViewFieldBean.getAccess().equalsIgnoreCase(formAuthViewFieldBean.getAccess())) || (oldFormAuthViewFieldBean.getIsNotNull() != formAuthViewFieldBean.getIsNotNull())) {
                                        fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                                        fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                                        resultMap.put(authKey, fieldAuth);
                                        if (fillBackFields != null) {
                                            fillBackFields.add(authKey);
                                        }
                                        apartAuthMap.put(authKey, formAuthViewFieldBean);
                                    }
                                    if (fillBackFields != null && fillBackFields.contains(authKey)) {
                                        fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                                        fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                                        resultMap.put(authKey, fieldAuth);
                                        apartAuthMap.put(authKey, formAuthViewFieldBean);
                                    }
                                    if (fieldBean.isSn() && !FieldAccessType.hide.getKey().equals(formAuthViewFieldBean.getAccess())) {
                                        fieldAuth.put("auth", FieldAccessType.browse.getKey());
                                        fieldAuth.put("isNotNull", "0");
                                    }
                                }
                            }
                        }
                    }
                }
            } else {// 计算结果是一个pid和统一设置，传入的oldFormAuthViewBean是上一次计算后的权限，不用changeAuthFields缓存上次权限
                List<FormFieldBean> fieldBeenList = formBean.getMasterTableBean().getFields();
                for (FormFieldBean fieldBean : fieldBeenList) {
                    Map<String, String> fieldAuth = new HashMap<String, String>();
                    FormAuthViewFieldBean oldFormAuthViewFieldBean = oldFormAuthViewBean.getFormAuthorizationField(fieldBean.getName());
                    FormAuthViewFieldBean formAuthViewFieldBean = newFormAuthViewBean.getFormAuthorizationField(fieldBean.getName());
                    if (!oldFormAuthViewFieldBean.getAccess().equalsIgnoreCase(formAuthViewFieldBean.getAccess()) || oldFormAuthViewFieldBean.getIsNotNull() != formAuthViewFieldBean.getIsNotNull()) {
                        fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                        fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                        resultMap.put(fieldBean.getName(), fieldAuth);
                        if (fillBackFields != null) {
                            fillBackFields.add(fieldBean.getName());
                        }
                        apartAuthMap.put(fieldBean.getName(), formAuthViewFieldBean);
                    }
                    // 如果此字段参与其他计算，需要返回此字段权限信息
                    if (fillBackFields != null && fillBackFields.contains(fieldBean.getName())) {
                        fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                        fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                        resultMap.put(fieldBean.getName(), fieldAuth);
                        apartAuthMap.put(fieldBean.getName(), formAuthViewFieldBean);
                    }
                    if (fieldBean.isSn() && !FieldAccessType.hide.getKey().equals(formAuthViewFieldBean.getAccess())) {
                        fieldAuth.put("auth", FieldAccessType.browse.getKey());
                        fieldAuth.put("isNotNull", "0");
                    }
                }
                Map<String, List<FormDataSubBean>> subMap = formDataMasterBean.getSubTables();
                for (FormTableBean subTable : formBean.getSubTableBean()) {
                    List<FormDataSubBean> subDataLine = subMap.get(subTable.getTableName());
                    if (subDataLine != null && subDataLine.size() > 0) {
                        List<FormFieldBean> subFieldList = subTable.getFields();
                        for (FormDataSubBean tempFormDataSubBean : subDataLine) {
                            for (FormFieldBean fieldBean : subFieldList) {
                                Map<String, String> fieldAuth = new HashMap<String, String>();
                                String authKey = fieldBean.getName() + "_" + tempFormDataSubBean.getId();
                                FormAuthViewFieldBean formAuthViewFieldBean = newFormAuthViewBean.getFormAuthorizationField(fieldBean.getName());
                                FormAuthViewFieldBean oldFormAuthViewFieldBean = oldFormAuthViewBean.getFormAuthorizationField(fieldBean.getName());
                                if ((!oldFormAuthViewFieldBean.getAccess().equalsIgnoreCase(formAuthViewFieldBean.getAccess())) || (oldFormAuthViewFieldBean.getIsNotNull() != formAuthViewFieldBean.getIsNotNull())) {
                                    fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                                    fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                                    resultMap.put(authKey, fieldAuth);
                                    if (fillBackFields != null) {
                                        fillBackFields.add(authKey);
                                    }
                                    apartAuthMap.put(authKey, formAuthViewFieldBean);
                                }
                                // 如果此字段参与其他计算，需要返回此字段权限信息
                                if (fillBackFields != null && fillBackFields.contains(authKey)) {
                                    fieldAuth.put("auth", this.getFieldFinalAccess(fieldBean, formAuthViewFieldBean.getAccess()));
                                    fieldAuth.put("isNotNull", String.valueOf(formAuthViewFieldBean.getIsNotNull()));
                                    resultMap.put(authKey, fieldAuth);
                                    apartAuthMap.put(authKey, formAuthViewFieldBean);
                                }
                                if (fieldBean.isSn() && !FieldAccessType.hide.getKey().equals(formAuthViewFieldBean.getAccess())) {
                                    fieldAuth.put("auth", FieldAccessType.browse.getKey());
                                    fieldAuth.put("isNotNull", "0");
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 如果高级权限将计算结果字段置为 编辑 或者 追加后，那么需要强制转为浏览
     *
     * @param formFieldBean
     * @param access
     * @return
     */
    private String getFieldFinalAccess(FormFieldBean formFieldBean, String access) {
        if ((FieldAccessType.add.getKey().equals(access)
                || FieldAccessType.edit.getKey().equals(access))
                && formFieldBean.isCalcField()) {
            return FieldAccessType.browse.getKey();
        }
        return access;
    }

    /**
     * 将前台提交的表单数据和后台缓存的表单数据进行增量合并
     *
     * @param masterData
     * @param cacheData
     * @param fb
     * @return 日志信息
     */
    @Override
    public void mergeFormData(FormDataMasterBean masterData, FormDataMasterBean cacheData, FormBean fb) {
        Map<String, Object> masterDataMap = masterData.getRowData();
        for (Entry<String, Object> objEntry : masterDataMap.entrySet()) {
            //日志记录
            //判断是否需要记录详细日志
            FormFieldBean ffb = fb.getFieldBeanByName(objEntry.getKey());
            if (ffb != null) {
                //替换主表数据
                cacheData.addFieldValue(objEntry.getKey(), objEntry.getValue());
            }
            if (ffb == null && "unique_MD5".equals(objEntry.getKey())) {
                cacheData.addFieldValue(objEntry.getKey(), objEntry.getValue());
            }
        }
        Map<String, List<FormDataSubBean>> frontSubDataMapList = masterData.getSubTables();
        Map<String, List<FormDataSubBean>> cacheSubDataMapList = cacheData.getSubTables();
        //用来存储被删除的数据
        Map<String, List<FormDataSubBean>> delMap = null;
        //循环后端数据，将前端提交数据的记录id和缓存中存在的id进行对比，如果前端提交的id在cacheData中已经没有了，则cacheData中删除该id对应的数据
        for (Entry<String, List<FormDataSubBean>> cacheDataEntry : cacheSubDataMapList.entrySet()) {
            String subTableName = cacheDataEntry.getKey();
            List<FormDataSubBean> cacheSubDataList = cacheDataEntry.getValue();

            List<FormDataSubBean> frontSubDataList = frontSubDataMapList.get(subTableName);
            List<Long> cacheNotShowSubDataIds = cacheData.getNotShowSubDataIds(subTableName);
            if (frontSubDataList == null) {
                //前端提交页面视图有可能只有部分子表，所以frontSubDataList有可能是空，如果frontSubDataList为空，则不做处理
                continue;
            }
            boolean hasEqualsData = false;
            for (FormDataSubBean cacheSubLineData : cacheSubDataList) {
                for (FormDataSubBean frontSubLineData : frontSubDataList) {
                    hasEqualsData = (cacheSubLineData.getId().longValue() == frontSubLineData.getId().longValue()) ? true : false;
                    if (hasEqualsData) {
                        break;
                    }
                }
                if (!hasEqualsData) {
                    if (delMap == null) {
                        delMap = new HashMap<String, List<FormDataSubBean>>();
                    }
                    if (delMap.get(subTableName) == null) {
                        delMap.put(subTableName, new ArrayList<FormDataSubBean>());
                    }
                    if (cacheNotShowSubDataIds == null || !cacheNotShowSubDataIds.contains(cacheSubLineData.getId())) {//前端虽然没有提交过来，但是后台缓存的没有显示的id中还有，则不将其删除，不然会导致没有加载的重复表数据丢失
                        delMap.get(subTableName).add(cacheSubLineData);
                    }
                }
            }
        }
        if (delMap != null && delMap.size() > 0) {
            for (Entry<String, List<FormDataSubBean>> delEntry : delMap.entrySet()) {
                for (FormDataSubBean delSubBean : delEntry.getValue()) {
                    cacheSubDataMapList.get(delEntry.getKey()).remove(delSubBean);
                }
            }
        }
        //循环前端数据，将后端缓存没有的则进行添加，如果后端缓存有，则进行值合并
        for (Entry<String, List<FormDataSubBean>> frontDataEntry : frontSubDataMapList.entrySet()) {
            String subTableName = frontDataEntry.getKey();
            List<FormDataSubBean> frontSubDataList = frontDataEntry.getValue();
            List<FormDataSubBean> cacheSubDataList = cacheSubDataMapList.get(subTableName);
            int i = 0;
            for (FormDataSubBean frontSubLineData : frontSubDataList) {
                boolean hasEqualsData = false;
                if (cacheSubDataList == null) {
                    cacheSubDataList = new ArrayList<FormDataSubBean>();
                    cacheSubDataMapList.put(subTableName, cacheSubDataList);
                }
                for (FormDataSubBean cacheSubLineData : cacheSubDataList) {
                    hasEqualsData = (cacheSubLineData.getId().longValue() == frontSubLineData.getId().longValue()) ? true : false;
                    if (hasEqualsData) {
                        Map<String, Object> frontRowDataMap = frontSubLineData.getRowData();
                        for (Entry<String, Object> entry : frontRowDataMap.entrySet()) {
                            cacheSubLineData.addFieldValue(entry.getKey(), entry.getValue());
                        }
                        break;
                    }
                }
                //如果有相等的需要合并数据行
                if (!hasEqualsData) {
                    //如果没有则添加
                    cacheSubDataList.add(i, frontSubLineData);
                }
                i++;
            }
        }
    }

    private boolean isPreRow(FormFieldBean resultBean) {
        boolean isPreRow = false;
        List<FormConditionActionBean> fcabs = resultBean.getFormConditionList();
        if (fcabs != null && fcabs.size() > 0) {
            for (FormConditionActionBean fcab : fcabs) {
                if (isPreRow) {
                    break;
                }
                List<CtpFormula> cfs = fcab.getAllFormulaList();
                for (CtpFormula cf : cfs) {
                    String expression = cf.getExpression();
                    if (expression != null && expression.replaceAll(" ", "").indexOf(FunctionSymbol.preRow.getKey() + "(") > -1) {// && cf.getExpression().trim().startsWith(FunctionSymbol.preRow.getKey())
                        isPreRow = true;
                        break;
                    }
                }
            }
        }
        return isPreRow;
    }

    /**
     * @return List<CtpContentAll>
     * @throws BusinessException
     */
    @Override
    public boolean delFormData(Long formId, String ids, Long templateId) throws BusinessException, SQLException {
        return this.delFormData(formId, ids, templateId, true);
    }

    /**
     * 加锁
     *
     * @param formId
     * @param ids
     * @return
     * @throws SQLException
     * @throws BusinessException
     * @throws NumberFormatException
     */
    @Override
    public String setLock(Long formId, String ids, Long templateId) throws NumberFormatException, BusinessException,
            SQLException {
        String returnStr = "1";
        StringBuilder logs = new StringBuilder();
        FormBean fb = cap4FormCacheManager.getForm(formId);
        //取得需要记录日志的列
        List<String> fieldsList = fb.getBind().getLogFieldList();
        List<Object> value = new ArrayList<Object>();
        String values = "";
        String id[] = ids.split(",");
        //先判断需要加锁的数据是否有被锁定的。
        for (int j = 0; j < id.length; j++) {
            String lockInfo = cap4FormManager.checkDataLockFormEdit(id[j], true, false);
            if (Strings.isNotBlank(lockInfo)) {
                returnStr = ResourceUtil.getString("form.unflow.setLock.tips", (j + 1), lockInfo);
                return returnStr;
            }
        }
        List<String> updateId = new ArrayList<String>();
        for (int j = 0; j < id.length; j++) {
            FormDataMasterBean fdmb = cap4FormDataDAO.selectDataByMasterId(Long.parseLong(id[j]), fb, null);
            //取得日志记录列的数据
            for (String name : fieldsList) {
                String[] names = name.split("\\.");
                FormFieldBean ffb = fb.getFieldBeanByName(names[1]);
                value = fdmb.getDataList(ffb.getName());
                if (value != null) {
                    value = FormUtil.getNotNullItem(value);
                    if (value.size() == 0) {
                        values = "";
                    } else {
                        values = getDisplayValue(ffb, value);
                    }
                } else {
                    values = "";
                }
                logs.append(FormLogUtil.getLogForLockorUnlock(fb, ffb, values,
                        FormDataStateEnum.UNFLOW_LOCKED.getKey()));
                LOGGER.debug(logs);
            }
            // 更新锁状态
            // cap4FormDataDAO.updateLockState(id[j], fb.getMasterTableBean().getTableName(),FormDataStateEnum.UNFLOW_LOCKED.getKey());
            updateId.add(id[j]);
            //记录日志
            formLogManager.saveOrUpdateLog(formId, fb.getFormType(),
                    Long.parseLong(id[j]), AppContext.currentUserId(), FormLogOperateType.LOCK.getKey(),
                    logs.toString(), fdmb.getStartMemberId(), fdmb.getStartDate());
            logs.setLength(0);
        }
        if (updateId.size() > 0) {
            // BUG CAPF-4684 批量提交 以前的循环提交有执行失败的update 或者耗时很长
            Date dateQueryStart = new Date();
            String updateIds = org.apache.commons.lang.StringUtils.join(updateId.toArray(), ",");
            cap4FormDataDAO.updateLockState(updateIds, fb.getMasterTableBean().getTableName(), FormDataStateEnum.UNFLOW_LOCKED.getKey());
            LOGGER.info("setLockOrUnlock lock update query time " + ((new Date()).getTime() - dateQueryStart.getTime()) + "ms , params ids : " + updateIds);
        }
        return returnStr;
    }

    //组装重复行字段的值，不然会显示id那些
    private String getDisplayValue(FormFieldBean ffb, List<Object> value) throws BusinessException {
        StringBuffer sb = new StringBuffer();
        if (ffb.isMasterField()) {
            Object display = ffb.getDisplayValue(value.get(0), false)[1];
            return display == null ? "" : display.toString();
        } else {
            int index = 0;
            for (Object obj : value) {
                if (index > 0) {
                    sb.append(",");
                }
                Object display = ffb.getDisplayValue(obj, false)[1];
                sb.append(display == null ? "" : display.toString());
                index++;
            }
        }
        return sb.toString();
    }

    /**
     * 解锁
     *
     * @param formId
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @Override
    public boolean unLock(Long formId, String ids, Long templateId) throws BusinessException, SQLException {
        StringBuilder logs = new StringBuilder();
        FormBean fb = cap4FormCacheManager.getForm(formId);

        //取得需要记录日志的列
        List<String> fieldsList = fb.getBind().getLogFieldList();
        List<FormFieldBean> list = fb.getAllFieldBeans();
        String[] fields = new String[list.size()];
        //记录所有需要保存日志的列
        List<FormFieldBean> logfields = new ArrayList<FormFieldBean>();
        int i = 0;
        for (FormFieldBean ffb : list) {
            //日志记录
            //判断是否需要记录详细日志,日志设置里列+数据唯一的列都要记录
            if (fb.getBind().getLogFieldList().contains(ffb.getOwnerTableName() + "." + ffb.getName())) {
                if (ffb.isMasterField()) {
                    fields[i] = ffb.getName();
                }
                logfields.add(ffb);
            }
            i++;
        }
        List<Object> value = new ArrayList<Object>();
        String values = "";
        String id[] = ids.split(",");
        List<String> updateId = new ArrayList<String>();
        for (int j = 0; j < id.length; j++) {
            if (StringUtil.checkNull(id[j])) {
                continue;
            }
            //取得日志记录列的数据
            FormDataMasterBean fdmb = cap4FormDataDAO.selectDataByMasterId(Long.parseLong(id[j]), fb, fields);
            for (FormFieldBean ffb : logfields) {
                value = fdmb.getDataList(ffb.getName());
                if (value != null) {
                    value = FormUtil.getNotNullItem(value);
                    if (value.size() == 0) {
                        values = "";
                    } else {
                        values = getDisplayValue(ffb, value);
                    }
                } else {
                    values = null;
                }
                logs.append(FormLogUtil.getLogForLockorUnlock(fb, ffb, values,
                        FormDataStateEnum.UNFLOW_UNLOCK.getKey()));
            }
            // 更新锁状态
            // cap4FormDataDAO.updateLockState(id[j], fb.getMasterTableBean().getTableName(),FormDataStateEnum.UNFLOW_UNLOCK.getKey());
            updateId.add(id[j]);
            //记录日志
            formLogManager.saveOrUpdateLog(formId, fb.getFormType(),
                    Long.parseLong(id[j]), AppContext.currentUserId(), FormLogOperateType.UNLOCK.getKey(),
                    logs.toString(), fdmb.getStartMemberId(), fdmb.getStartDate());
            logs.setLength(0);
        }
        if (updateId.size() > 0) {
            // BUG CAPF-4684 批量提交 以前的循环提交有执行失败的update 或者耗时很长
            Date dateQueryStart = new Date();
            String updateIds = org.apache.commons.lang.StringUtils.join(updateId.toArray(), ",");
            cap4FormDataDAO.updateLockState(updateIds, fb.getMasterTableBean().getTableName(), FormDataStateEnum.UNFLOW_UNLOCK.getKey());
            LOGGER.info("setLockOrUnlock lock update query time " + ((new Date()).getTime() - dateQueryStart.getTime()) + "ms , params ids : " + updateIds);
        }
        return true;
    }

    /**
     * 无流程高级查询
     *
     * @param flipInfo
     * @param params
     * @param forExport
     * @return
     * @throws BusinessException
     */
    @Override
    public FlipInfo getFormMasterDataListByFormId(FlipInfo flipInfo, Map<String, Object> params, boolean forExport) throws BusinessException {
        Long formId = Long.parseLong(params.get("formId") + "");
        FormBean formBean = cap4FormCacheManager.getForm(formId.longValue());
        String templateIdStr = String.valueOf(params.get("formTemplateId"));
        String auth = FormUtil.getUnflowFormAuth(formBean, templateIdStr);
        flipInfo = getFormDataList(flipInfo, params, formBean, false);
        //处理组织机构等id类型数据，这样列表里面显示出来的是显示值，如人员id对应应该显示人员姓名
        //OA-62881  表单信息管理和基础数据列表中隐藏字段显示了  调用查询已经实现的接口完成
        flipInfo.setData(FormUtil.setShowValueList(formBean, auth, flipInfo.getData(), false, forExport));
        return flipInfo;
    }

    /**
     * 查询表单列表数据
     *
     * @param flipInfo
     * @param params
     * @param reverse  排序是否反转，用来控制取第一条或者最后一条数据,将原先正常情况下的最后一条数据变成第一条数据
     * @return
     * @throws BusinessException
     */
    @Override
    public FlipInfo getFormDataList(FlipInfo flipInfo, Map<String, Object> params, FormBean formBean, boolean reverse) throws BusinessException {
        FormQueryResult queryResult = this.getFormDataQueryResult(flipInfo, params, formBean, reverse);
        return queryResult.getFlipInfo();
    }

    @Override
    public FormQueryResult getFormDataQueryResult(FlipInfo flipInfo, Map<String, Object> params, FormBean formBean, boolean reverse) throws BusinessException {
        Long currentUserId = AppContext.currentUserId();
        boolean isNeedPage = true;
        FormQueryTypeEnum queryType = FormQueryTypeEnum.infoManageQuery;
        FormQueryWhereClause customCondition = getCustomConditionFormQueryWhereClause(formBean, params);
        //M3无流程支持自定义排序20170214
        List<Map<String, Object>> customOrderBy = (List<Map<String, Object>>) params.get("userOrderBy");
        Set<String> customShowFields = (Set<String>) params.get("customShowFields");
        FormQueryWhereClause relationSqlWhereClause = getRelationConditionFormQueryWhereClause(formBean, params);
        Long templeteId = Long.parseLong(Strings.isBlank(String.valueOf(params.get("formTemplateId") == null ? "" : params.get("formTemplateId"))) ? "0" : params.get("formTemplateId") + "");
        return getFormQueryResult(currentUserId, flipInfo, isNeedPage, formBean, templeteId, queryType,
                customCondition, customOrderBy, customShowFields, relationSqlWhereClause, reverse);
    }

    @Override
    public FormQueryWhereClause getRelationConditionFormQueryWhereClause(FormBean formBean, Map<String, Object> params) {
        try {
            String fromFormIdStr = ParamUtil.getString(params, "fromFormId");
            if (Strings.isNotBlank(fromFormIdStr)) {
                Long fromFormId = ParamUtil.getLong(params, "fromFormId");
                Long fromRecordId = ParamUtil.getLong(params, "fromRecordId");
                Long fromDataId = ParamUtil.getLong(params, "fromDataId");
                String fromRelationAttrStr = ParamUtil.getString(params, "fromRelationAttr");

                FormBean fromForm = cap4FormCacheManager.getForm(fromFormId);
                FormDataMasterBean fromData = cap4FormManager.getSessioMasterDataBean(fromDataId);
                FormFieldBean fromRelationAttr = fromForm.getFieldBeanByName(fromRelationAttrStr);
                FormFormulaBean conditionFormFormulaBean = this.changeFormFormulaBean(fromForm, fromData, fromRecordId, fromRelationAttr);
                if (null != conditionFormFormulaBean) {
                    AppContext.putThreadContext("isRelationCondition", true);//添加线程变量，用于判断是不是关联sql生成，主要用来判断多部门include的情况
                    FormQueryWhereClause whereClause = conditionFormFormulaBean.getExecuteFormulaForWhereClauseSQL(formBean, true, true);
                    AppContext.removeThreadContext("isRelationCondition");
                    String relationSql = whereClause.getAllSqlClause();
                    if (!StringUtil.checkNull(relationSql)) {
                        for (FormTableBean table : formBean.getTableList()) {
                            String tablePrefix = FormBean.R_PREFIX + Functions.substringAfter(table.getTableName(), "_");
                            if (relationSql.indexOf(tablePrefix) != -1) {
                                relationSql = relationSql.replace(tablePrefix, table.getTableName());
                            }
                        }
                    }
                    whereClause.setAllSqlClause(relationSql);
                    return whereClause;
                }
            }
        } catch (Exception e) {
            LOGGER.info("", e);
        }
        return null;
    }

    /**
     * 获得自定义查询sql对象：条件和参数
     *
     * @param formBean
     * @param customParams
     * @return
     */
    private FormQueryWhereClause getCustomConditionFormQueryWhereClause(FormBean formBean, Map<String, Object> customParams) {
        FormQueryWhereClause whereClause = new FormQueryWhereClause();
        StringBuilder sb = new StringBuilder("");
        List<Object> queryParams = new ArrayList<Object>();
        //增加查询条件
        Iterator<String> ite = customParams.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            if (customParams.get(key) == null || Strings.isBlank(String.valueOf(customParams.get(key)))) {
                continue;
            }
            if (!"sortStr".equals(key) && !"formId".equals(key) && !"formTemplateId".equals(key)
                    && !"fromFormId".equals(key) && !"fromDataId".equals(key) && !"fromRecordId".equals(key)
                    && !"fromRelationAttr".equals(key) && !"userOrderBy".equals(key)) {
                if ("userConditions".equals(key)) {
                    List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
                    Object temO = customParams.get(key);
                    if (temO instanceof Map) {
                        tempList.add((Map) temO);
                    } else {
                        tempList.addAll((List) temO);
                    }
                    FormQueryWhereClause myWhereClause = FormUtil.getSQLStrWhereClause(tempList, formBean, true);
                    if (null != myWhereClause && Strings.isNotBlank(myWhereClause.getAllSqlClause())) {
                        sb.append(myWhereClause.getAllSqlClause());
                    }
                    if (null != myWhereClause && null != myWhereClause.getQueryParams() && !myWhereClause.getQueryParams().isEmpty()) {
                        queryParams.addAll(myWhereClause.getQueryParams());
                    }
                } else {
                }
            }
        }
        whereClause.setQueryParams(queryParams);
        whereClause.setAllSqlClause(sb.toString());
        return whereClause;
    }

    /**
     * 处理某一个关联表单字段的过滤条件
     *
     * @param fromForm
     * @param fromData
     * @param fromRecordId
     * @param fromRelationAttr
     * @return
     * @throws Exception
     */
    private FormFormulaBean changeFormFormulaBean(FormBean fromForm, FormDataMasterBean fromData, Long fromRecordId, FormFieldBean fromRelationAttr) throws Exception {
        if (null == fromRelationAttr.getFormRelation() || null == fromRelationAttr.getFormRelation().getViewConditionId()) {//没有设置过滤条件时这儿为空要做防护
            return null;
        }
        String subTableName = fromRelationAttr.isSubField() ? fromRelationAttr.getOwnerTableName() : null;
        return this.changeFormFormulaBean(fromForm, fromData, fromRecordId, subTableName, fromRelationAttr.getFormRelation().getViewConditionId());
    }

    /**
     * 处理某一个关联表单字段的过滤条件
     *
     * @param fromForm
     * @param fromData
     * @param fromRecordId
     * @param subTableName
     * @param conditionId
     * @return
     * @throws Exception
     */
    @Override
    public FormFormulaBean changeFormFormulaBean(FormBean fromForm, FormDataMasterBean fromData, Long fromRecordId, String subTableName, Long conditionId) throws Exception {
        if (conditionId == null || conditionId == 0) {
            return null;
        }
        FormFormulaBean formFormulaBean = cap4FormCacheManager.loadFormFormulaBean(fromForm, conditionId);
        FormFormulaBean conditionFormFormulaBean = (FormFormulaBean) formFormulaBean.clone();
        List<FormulaBaseBean> formulaBaseBeans = conditionFormFormulaBean.getFormulaBeanList();
        List<FormulaBaseBean> replaceFormulaBaseBeans = new ArrayList<FormulaBaseBean>();

        String masterTableName = fromForm.getMasterTableBean().getTableName();
        String masterTablePre = FormBean.M_PREFIX + Functions.substringAfter(masterTableName, "_");
        subTableName = Strings.isBlank(subTableName) ? "" : subTableName;
        String subTablePre = Strings.isBlank(subTableName) ? "" : FormBean.M_PREFIX + Functions.substringAfter(subTableName, "_");

        for (FormulaBaseBean formulaBaseBean : formulaBaseBeans) {
            if (formulaBaseBean instanceof FormulaDataFieldBean) {
                FormulaBaseBean changeBean = this.changeFormulaBaseBean(conditionFormFormulaBean, (FormulaDataFieldBean) formulaBaseBean, fromForm, fromData, fromRecordId, masterTablePre, subTableName, subTablePre);
                replaceFormulaBaseBeans.add(changeBean);
            } else if (formulaBaseBean instanceof FormulaFunctionBean) {
                List<FormulaBaseBean> temp = ((FormulaFunctionBean) formulaBaseBean).getList();
                List<FormulaBaseBean> list = new ArrayList<FormulaBaseBean>();
                for (FormulaBaseBean tempBean : temp) {
                    if (tempBean instanceof FormulaDataFieldBean) {
                        FormulaBaseBean changeBean = this.changeFormulaBaseBean(conditionFormFormulaBean, (FormulaDataFieldBean) tempBean, fromForm, fromData, fromRecordId, masterTablePre, subTableName, subTablePre);
                        list.add(changeBean);
                    } else {
                        list.add(tempBean);
                    }
                }
                ((FormulaFunctionBean) formulaBaseBean).setList(list);
                replaceFormulaBaseBeans.add(formulaBaseBean);
            } else {
                replaceFormulaBaseBeans.add(formulaBaseBean);
            }
        }
        conditionFormFormulaBean.setFormulaBeanList(replaceFormulaBaseBeans);
        return conditionFormFormulaBean;
    }

    /**
     * 替换关联表单的过滤条件里面的当前表变量
     *
     * @param formFormulaBean
     * @param formulaDataFieldBean
     * @param fromForm
     * @param fromData
     * @param fromRecordId
     * @param masterTablePre
     * @param subTableName
     * @param subTablePre
     * @return
     * @throws BusinessException
     */
    private FormulaBaseBean changeFormulaBaseBean(FormFormulaBean formFormulaBean, FormulaDataFieldBean formulaDataFieldBean, FormBean fromForm, FormDataMasterBean fromData, Long fromRecordId, String masterTablePre, String subTableName, String subTablePre) throws BusinessException {
        String value = formulaDataFieldBean.getValue();
        if (value.contains(masterTablePre) || (Strings.isNotBlank(subTablePre) && value.contains(subTablePre))) {
            String fieldName = value.substring(value.indexOf(".") + 1, value.length());
            FormFieldBean field = fromForm.getFieldBeanByName(fieldName);
            if (field == null) {//固定字段
                field = MasterTableField.getEnumByKey(fieldName).getFormFieldBean();
            }
            String fieldValue = "";
            if (field.isMasterField()) {
                fieldValue = formatSqlParm(field, fromData);
            } else {
                FormDataSubBean subBean = fromData.getFormDataSubBeanById(subTableName, fromRecordId);
                fieldValue = formatSqlParm(field, subBean);
            }
            if (field.getFieldType().equals(FieldType.DECIMAL.getKey())) {
                if (StringUtil.checkNull(fieldValue)) {
                    fieldValue = "0";//为空按默认值处理
                }
            } else if (field.getFieldType().equals(FieldType.TIMESTAMP.getKey())) {
                if (StringUtil.checkNull(fieldValue)) {
                    fieldValue = FormulaFunctionUitl.DEFAULT_DATE;//为空按默认值处理
                }
                fieldValue = FunctionSymbol.to_date.getKey() + "('" + fieldValue + "')";
            } else if (field.getFieldType().equals(FieldType.DATETIME.getKey())) {
                if (StringUtil.checkNull(fieldValue)) {
                    fieldValue = FormulaFunctionUitl.DEFAULT_DATE_TIME;//为空按默认值处理
                }
                fieldValue = FunctionSymbol.to_date.getKey() + "('" + fieldValue + "')";
            } else {
                if (!StringUtil.checkNull(fieldValue)) {
                    fieldValue = "'" + fieldValue + "'";
                }
            }

            FormulaValueBean newBean = formFormulaBean.new FormulaValueBean();
            newBean.setValue(fieldValue);
            return newBean;
        } else {
            return formulaDataFieldBean;
        }
    }

    /**
     * 格式化字段的值，如果没有，则赋上默认值（主要针对日期和日期时间字段）
     *
     * @param field
     * @param fromData
     * @return
     */
    private String formatSqlParm(FormFieldBean field, FormDataBean fromData) {
        String fieldValue;
        Object obj = fromData.getFieldValue(field.getName());
        if (field.getFieldType().equals(FieldType.TIMESTAMP.getKey())) {
            if (StringUtil.checkNull(String.valueOf(obj))) {
                fieldValue = FormulaFunctionUitl.DEFAULT_DATE;//为空按默认值处理
            } else {
                if (obj instanceof Date) {
                    fieldValue = DateUtil.format((Date) obj, DateUtil.YEAR_MONTH_DAY_PATTERN);
                } else {
                    fieldValue = String.valueOf(obj);
                }
            }
        } else if (field.getFieldType().equals(FieldType.DATETIME.getKey())) {
            if (StringUtil.checkNull(String.valueOf(obj))) {
                fieldValue = FormulaFunctionUitl.DEFAULT_DATE_TIME;//为空按默认值处理
            } else {
                if (obj instanceof Date) {
                    fieldValue = DateUtil.formatDateTime((Date) obj);
                } else {
                    fieldValue = String.valueOf(obj);
                }
            }
        } else {
            fieldValue = String.valueOf(obj);
        }
        return fieldValue;
    }

    @Override
    public void updateDataState(ColSummary colSummary, CtpAffair affair, ColHandleType type, List<Comment> list)
            throws BusinessException, SQLException {
        FormBean formBean = cap4FormCacheManager.getForm(colSummary.getFormAppid());
        LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 数据ID：" + colSummary.getFormRecordid() + " 操作类型是：" + type.name());
        List<FormFieldBean> fieldBeans = formBean.getFieldsByType(FormFieldComEnum.FLOWDEALOPITION);
        Map<String, Object> map = new HashMap<String, Object>();
        FormTriggerBean.TriggerPoint conditionState = null;

        List<String> tempList;
        Map<String, List<String>> subRelationFieldList = new HashMap<String, List<String>>();
        Map<String, Object> subMap;
        Map<String, Map<String, Object>> tableDataMap = new HashMap<String, Map<String, Object>>();
        boolean needTrigger = false;//是否执行触发：发起、自动发起、暂存待办、提交，以及非子流程的流程结束时为true
        boolean needFillBackDealOpition = false;
        boolean needCalcAll = false;
        String triggerEventSourceEnum = "";
        switch (type) {
            case save://保存待发
                this.getStateMap(map, false, colSummary.getStartMemberId(), DateUtil.currentTimestamp(), true);
                break;
            case send://发送
            case autosend://自动发起
                needTrigger = true;
                conditionState = FormTriggerBean.TriggerPoint.FlowSend;
                if (this.isChildrenWorkFlow(colSummary)) {
                    needTrigger = false;
                }
                //子流程发起不更新创建时间和创建人
                this.getStateMap(map, true, colSummary.getStartMemberId(), DateUtil.currentTimestamp(), needTrigger);
                triggerEventSourceEnum = FormTriggerBean.TriggerEventSourceEnum.send.getKey();
                break;
            case wait://暂存待办
                needFillBackDealOpition = true;
                needTrigger = true;
                conditionState = FormTriggerBean.TriggerPoint.FlowSend;
                triggerEventSourceEnum = FormTriggerBean.TriggerEventSourceEnum.wait.getKey();
                break;
            case finish://正常提交
                //正常提交时，先给出默认状态值，然后根据是审核和核定给对应的值
                //子流程除了核定或者审核通过，都不修改状态为未审核状态 OA-59367
                if ((colSummary.getNewflowType() == null || colSummary.getNewflowType() != 1) && (colSummary.isAudited() == null || !colSummary.isAudited())) { //如果已经审核通过了，则不在修改状态为默认状态
                    this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_UNAUDITED.getKey(), AppContext.currentUserId(), DateUtil.currentTimestamp());
                }
                LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 操作类型是：" + type.name() + "提交处理");
                conditionState = FormTriggerBean.TriggerPoint.FlowSend;
                triggerEventSourceEnum = FormTriggerBean.TriggerEventSourceEnum.finish.getKey();

                //审核
                if (AUDIT.equals(affair.getNodePolicy())) {
                    //审核通过
                    if (colSummary.isAudited()) {
                        this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_AUDITEDPASS.getKey(), AppContext.currentUserId(), DateUtil.currentTimestamp());
                        LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 操作类型是：" + type.name() + "审核通过");
                        triggerEventSourceEnum = FormTriggerBean.TriggerEventSourceEnum.audit.getKey();
                        //审核不通过
                    } else {
                        this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_AUDITEDUNPASS.getKey(), AppContext.currentUserId(), DateUtil.currentTimestamp());
                    }
                    needCalcAll = true;
                }
                //核定
                else if (vouch.equals(affair.getNodePolicy())) {
                    //核定通过
                    if (Strings.equals(colSummary.getVouch(), 1)) {
                        this.getStateMap(map, UpdateDataType.FORM_VOUCH, FormDataRatifyFlagEnum.FLOW_VOUCHPASS.getKey(), AppContext.currentUserId(), DateUtil.currentTimestamp());
                        conditionState = FormTriggerBean.TriggerPoint.FlowState_Ratify;
                        LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 操作类型是：" + type.name() + "核定通过");
                        triggerEventSourceEnum = FormTriggerBean.TriggerEventSourceEnum.ratify.getKey();
                        //核定不通过
                    } else {
                        this.getStateMap(map, UpdateDataType.FORM_VOUCH, FormDataRatifyFlagEnum.FLOW_VOUCHUNPASS.getKey(), AppContext.currentUserId(), DateUtil.currentTimestamp());
                    }
                    needCalcAll = true;
                }

                needTrigger = true;
                needFillBackDealOpition = true;
                break;
            case stepBack://回退
                //上一节点是审核，设置状态为未审核
                if (AUDIT.equals(affair.getNodePolicy())) {
                    needCalcAll = true;
                    this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_AUDITEDUNPASS.getKey(), 0L, DateUtil.currentTimestamp());
                }
                //上一节点是核定，设置状态为未核定
                else if (vouch.equals(affair.getNodePolicy())) {
                    needCalcAll = true;
                    this.getStateMap(map, UpdateDataType.FORM_VOUCH, FormDataRatifyFlagEnum.FLOW_VOUCHUNPASS.getKey(), 0L, DateUtil.currentTimestamp());
                }
                needFillBackDealOpition = true;
                break;
            case stepStop://终止
                if (!this.isChildrenWorkFlow(colSummary)) {
                    map.put(MasterTableField.finishedflag.getKey(), FromDataFinishedFlagEnum.STOP.getKey());
                }
                needCalcAll = true;
                try {
                    cap4FormTriggerManager.rollBackWithHolding(colSummary.getFormRecordid());
                } catch (Exception e) {
                    LOGGER.error("终止预提数据还原报错", e);
                }
                needFillBackDealOpition = true;
                LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 操作类型是：" + type.name() + "流程终止");
                break;
            case repeal://撤销
                //撤销流程、回退到发起者节点都走的此分支
                this.getStateMap(map, false, colSummary.getStartMemberId(), DateUtil.currentTimestamp(), true);

                for (FormFieldBean formFieldBean : fieldBeans) {
                    map.put(formFieldBean.getName(), "");
                }
                //String rightId = getFirstEditRightId(affair, formBean);
                WorkflowApiManager wapi = (WorkflowApiManager) AppContext.getBean("wapi");
                // 默认取发起者pc端权限
                String rightId = FormUtil.parseOperationId(wapi.getNodeFormOperationNameFromRunning(colSummary.getProcessId(), null));
                if (Strings.isNotBlank(rightId)) {
                    FormAuthViewBean viewBean = formBean.getAuthViewBeanById(Long.parseLong(rightId));
                    //如果viewBean为null直接break;的话会导致后面的预提无法回滚
                    if (viewBean == null) {
                        LOGGER.error("流程撤销，获取到的权限为null，对应operationId = " + rightId);
                    } else {
                        List<FormAuthViewFieldBean> viewFieldBeans = viewBean.getFormAuthorizationFieldList();
                        FormFieldBean fieldBean;
                        for (FormAuthViewFieldBean avfb : viewFieldBeans) {
                            fieldBean = avfb.getFormFieldBean();
                            if (fieldBean == null) {
                                LOGGER.warn("当前视图权限中的字段：" + avfb.getFieldName() + " 已经被删除，此处出现空属于历史数据，必须进行过滤");
                                continue;
                            }
                            //计算式中含有流水号的，不清空
                            String serid = FormFieldUtil.getSerialNumberIds4Formula(fieldBean.getFormulaData());
                            if (Strings.isNotBlank(serid)) {
                                continue;
                            }
                            //默认值为流水号的，不清空
                            if (Strings.isNotBlank(avfb.getDefaultValueType()) && Strings.isNotBlank(avfb.getDefaultValue()) && avfb.isSerialNumberDefaultValue()) {
                                continue;
                            }
                            FormFieldCtrl ctrl = fieldBean.getFieldCtrl();
                            //重复表行序号等不需要清空值
                            if (null == ctrl || !ctrl.needClearWhenBackToStarter()) {
                                continue;
                            }
                            //非编辑权限全清空
                            FormAuthViewFieldBean auth = viewBean.getFormAuthorizationField(fieldBean.getName());
                            if (!auth.isEditAuth()) {
                                if (fieldBean.isMasterField()) {
                                    //控件自身实现回退清空逻辑
                                    ctrl.clearWhenBackToStarter(formBean, colSummary.getFormRecordid(), fieldBean, map);
                                    // 关联首选字段，则删除其关联记录
                                    if (fieldBean.isRelationSelectedField()) {
                                        cap4FormRelationRecordDAO.deleteFieldRecord(colSummary.getFormRecordid(), fieldBean.getName(), 0L);
                                    }
                                } else {
                                    subMap = tableDataMap.get(fieldBean.getOwnerTableName());
                                    if (subMap == null) {
                                        subMap = new HashMap<String, Object>();
                                    }
                                    //控件自身实现回退清空逻辑
                                    ctrl.clearWhenBackToStarter(formBean, colSummary.getFormRecordid(), fieldBean, subMap);
                                    tableDataMap.put(fieldBean.getOwnerTableName(), subMap);

                                    // 关联首选字段，则删除其关联记录
                                    if (fieldBean.isRelationSelectedField()) {
                                        tempList = subRelationFieldList.get(fieldBean.getOwnerTableName());
                                        if (tempList == null) {
                                            tempList = new ArrayList<String>();
                                        }
                                        tempList.add(fieldBean.getName());
                                        subRelationFieldList.put(fieldBean.getOwnerTableName(), tempList);
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    cap4FormTriggerManager.rollBackWithHolding(colSummary.getFormRecordid());
                } catch (Exception e) {
                    LOGGER.error("撤销保存待发预提数据还原报错", e);
                }
                needFillBackDealOpition = true;
                break;
            case takeBack://取回
                //当前节点是审核，设置状态为未审核
                if (AUDIT.equals(affair.getNodePolicy())) {
                    needCalcAll = true;
                    this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_UNAUDITED.getKey(), 0L, null);
                }
                //当前节点是核定，设置状态为未核定
                else if (vouch.equals(affair.getNodePolicy())) {
                    needCalcAll = true;
                    this.getStateMap(map, UpdateDataType.FORM_VOUCH, FormDataRatifyFlagEnum.FLOW_UNVOUCH.getKey(), 0L, null);
                }
                needFillBackDealOpition = true;
                break;
            case specialback://指定回退
                //指定回退到发起者，即流程重走，需要回滚预提
                boolean isRepeal = AppContext.getThreadContext("isRepeal_4_form_use") == null ? false : (Boolean) AppContext.getThreadContext("isRepeal_4_form_use");
                LOGGER.info("指定回退，是否回退到发起者：isRepeal = " + isRepeal);
                if (isRepeal) {
                    //指定回退到发起者，需要更改状态为草稿
                    this.getStateMap(map, false, colSummary.getStartMemberId(), DateUtil.currentTimestamp(), true);
                    try {
                        LOGGER.info("指定回退，预提回滚");
                        cap4FormTriggerManager.rollBackWithHolding(colSummary.getFormRecordid());
                    } catch (Exception e) {
                        LOGGER.error("撤销保存待发预提数据还原报错", e);
                    }
                }
                //BUG_重要_V5_V6.0sp1_一星卡_北京华夏顺泽投资集团有限公司_流程处理意见框没有勾选回退意见不显示
                //未勾选流程意见回退不显示的时候未回填
                needFillBackDealOpition = true;
                break;
            default:
                break;
        }
        if (!this.isChildrenWorkFlow(colSummary) && colSummary.getState() != null
                && colSummary.getState() == CollaborationEnum.flowState.finish.ordinal()) {
            if (conditionState == FormTriggerBean.TriggerPoint.FlowSend) {
                conditionState = FormTriggerBean.TriggerPoint.FlowState_Send_OR_Finished;
            } else if (conditionState == FormTriggerBean.TriggerPoint.FlowState_Ratify) {
                conditionState = FormTriggerBean.TriggerPoint.FlowState_Ratify_OR_Finished;
            } else {
                conditionState = FormTriggerBean.TriggerPoint.FlowState_Finished;
            }
            map.put(MasterTableField.finishedflag.getKey(), FromDataFinishedFlagEnum.END_YES.getKey());
            //非子流程的流程结束需要执行触发
            needTrigger = true;
            needCalcAll = true;
            LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 操作类型是：" + type.name() + "流程结束");
        }
        //判断是否有字段设置了流程处理意见，然后决定是否更新二维码文件
        boolean hasSetValue = false;
        if (needFillBackDealOpition) {
            hasSetValue = this.getDealOpition(formBean, map, fieldBeans, list, colSummary, affair);
        }

        LOGGER.info(colSummary.getSubject() + "开始更新数据状态…………表单名称：" + formBean.getFormName() + " " + formBean.getId() + " 数据ID：" + colSummary.getFormRecordid() + " 操作类型是：" + type.name() + map);
        //BUG_普通_V5_V5.6SP1_宝鸡航天动力泵业有限公司_节点处理人提交流程之后，流程态度意见控件数据为空。
        FormDataMasterBean masterDataBean = cap4FormManager.getSessioMasterDataBean(colSummary.getFormRecordid());
        if (masterDataBean != null) {
            masterDataBean.addFieldValue(map);
        }
        //批处理完后, 也要修改modify_date
        if (!isInformNode(affair)) {
            //非知会节点才处理
            map.put(MasterTableField.modify_date.getKey(), DateUtil.currentTimestamp());
            map.put(MasterTableField.modify_member_id.getKey(), AppContext.currentUserId());
        } else {
            //知会节点处理时不执行触发，回写动作
            needTrigger = false;
        }
        boolean constantFieldInCalc = formBean.hasConstantFieldInCalc(1);
        //如果有固定字段参与计算，先刷新一次计算，再保存表单
        if ((needCalcAll && constantFieldInCalc) || hasSetValue) {
            if (masterDataBean == null) {
                //批处理提交的时候没有缓存，需要查询一次数据库
                masterDataBean = this.cap4FormDataDAO.selectDataByMasterId(colSummary.getFormRecordid(), formBean, null);
            }
            masterDataBean.addFieldValue(map);
            if (needCalcAll && constantFieldInCalc) {
                this.calcAll(formBean, masterDataBean, null, false, false, false, false);
            }
            cap4FormManager.saveOrUpdateFormData(masterDataBean, formBean.getId(), true);
        } else {//如果固定字段没有参与计算，则只更新固定字段和流程处理意见等字段的值
            cap4FormDataDAO.updateData(colSummary.getFormRecordid(), formBean.getMasterTableBean().getTableName(), map);
        }
        if (!tableDataMap.isEmpty()) {
            List<FormDataSubBean> subBeans;
            masterDataBean = this.cap4FormDataDAO.selectDataByMasterId(colSummary.getFormRecordid(), formBean, null);
            for (Entry<String, Map<String, Object>> et : tableDataMap.entrySet()) {
                subBeans = masterDataBean.getSubData(et.getKey());
                tempList = subRelationFieldList.get(et.getKey());
                if (Strings.isNotEmpty(subBeans)) {
                    for (FormDataSubBean formDataSubBean : subBeans) {
                        cap4FormRelationRecordDAO.deleteFieldRecord(colSummary.getFormRecordid(), tempList, formDataSubBean.getId());
                        this.cap4FormDataDAO.updateData(formDataSubBean.getId(), et.getKey(), et.getValue());
                    }
                }
            }
        }
        if (needTrigger) {
            if (formBean.getFormType() == FormType.processesForm.getKey() && Strings.isNotEmpty(formBean.getFormTriggerIdList())) {
                LOGGER.info("需要执行触发，进入触发执行判断…………");
                AppContext.putThreadContext("TRIGGER_PARAM_SUMMARY_PRE_" + colSummary.getId(), colSummary);
                cap4FormTriggerManager.doPreWrite(ModuleType.collaboration.getKey(), colSummary.getId(), formBean, colSummary.getFormRecordid(), conditionState, colSummary.getSubject());
                AppContext.removeThreadContext("TRIGGER_PARAM_SUMMARY_PRE_" + colSummary.getId());
                cap4FormTriggerManager.doTrigger(affair, ModuleType.collaboration.getKey(), colSummary.getId(), conditionState, triggerEventSourceEnum);
            }
        }
        if (colSummary.getFormRecordid() != null) {//有流程表单处理之后在更新完状态再调用移除session表单数据缓存
            cap4FormManager.removeSessionMasterDataBean(colSummary.getFormRecordid());
        }
    }

    /**
     * 获取第一个有权限的可编辑权限
     */
    private String getFirstEditRightId(CtpAffair affair, FormBean formBean) {
        String rightId = "";
        String rithtStr = affair.getMultiViewStr();//形如：视图id.权限id_视图id.权限id_视图id.权限id
        String[] bindViewOperation = affair.getFirstViewAndOperation();
        if (Strings.isNotBlank(bindViewOperation[1])) {
            rightId = bindViewOperation[1];
        }
        return rightId;
    }

    /* (non-Javadoc)
     * @see com.seeyon.cap4.form.modules.engin.base.formData.CAP4FormDataManager#updateDataState(java.lang.Long, java.lang.Long, java.lang.String, int)
     */
    @Override
    public void updateDataState(Long formId, Long dataId, String type, int state) throws BusinessException {

        FormDataMasterBean masterBean = this.cap4FormManager.getSessioMasterDataBean(dataId);
        if (masterBean != null) {
            UpdateDataType updateDataType = UpdateDataType.getTypeByName(type);
            if (updateDataType != null) {
                Map<String, Object> map = new HashMap<String, Object>();
                if (updateDataType != UpdateDataType.FORM_FLOW) {
                    this.getStateMap(map, updateDataType, state, AppContext.currentUserId(), DateUtil.currentTimestamp());
                } else {
                    this.getStateMap(map, updateDataType, state, masterBean.getStartMemberId(), masterBean.getStartDate());
                }
                masterBean.addFieldValue(map);
            }
        }
    }

    /**
     * 组装流程表单数据状态map，对下面的方法的进一步封装，主要用于发送，保存待发，撤销
     *
     * @param map         数据map 必传
     * @param isSend      是否是发送调用
     * @param senderId    发起人
     * @param date        发起时间
     * @param setFormFlow 是否执行创建人更新
     */
    private void getStateMap(Map<String, Object> map, boolean isSend, Long senderId, Date date, boolean setFormFlow) {
        if (isSend) {
            this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_UNAUDITED.getKey(), 0L, null);
        } else {
            this.getStateMap(map, UpdateDataType.FORM_AUDIT, FormDataStateEnum.FLOW_DRAFT.getKey(), 0L, null);
        }
        this.getStateMap(map, UpdateDataType.FORM_VOUCH, FormDataRatifyFlagEnum.FLOW_UNVOUCH.getKey(), 0L, null);
        if (setFormFlow) {
            this.getStateMap(map, UpdateDataType.FORM_FLOW, FromDataFinishedFlagEnum.END_NO.getKey(), senderId, date);
        }
    }

    /**
     * 组装流程表单数据状态map
     *
     * @param map      数据map 必传
     * @param type     需要更新的状态分类：流程状态，核定状态，审核状态
     * @param state    对应的状态值
     * @param memberId 对应的人员
     * @param date     对应的更新时间
     */
    private void getStateMap(Map<String, Object> map, UpdateDataType type, int state, Long memberId, Date date) {
        if (map == null) {
            return;
        }

        switch (type) {
            case FORM_AUDIT:
                map.put(MasterTableField.state.getKey(), state);
                if (state != FormDataStateEnum.FLOW_UNAUDITED.getKey()) {
                    // CAPF-8052 未审核的情况，不需要去更新审核相关的字段
                    map.put(MasterTableField.approve_date.getKey(), date);
                    map.put(MasterTableField.approve_member_id.getKey(), memberId);
                }
                break;
            case FORM_FLOW:
                map.put(MasterTableField.finishedflag.getKey(), state);
                map.put(MasterTableField.start_member_id.getKey(), memberId);
                map.put(MasterTableField.start_date.getKey(), date);
                map.put(MasterTableField.modify_member_id.getKey(), memberId);
                map.put(MasterTableField.modify_date.getKey(), date);
                break;
            case FORM_VOUCH:
                map.put(MasterTableField.ratifyflag.getKey(), state);
                map.put(MasterTableField.ratify_date.getKey(), date);
                map.put(MasterTableField.ratify_member_id.getKey(), memberId);
                break;
            default:
                break;
        }
    }

    /**
     * 设置流程意见控件值
     * 201：意见为空时均不显示；202：暂存待办意见不显示；203：回退意见不显示
     *
     * @param formBean
     * @param masterBean
     * @param fieldBeans
     * @param comments
     * @throws BusinessException
     */
    private boolean getDealOpition(FormBean formBean, Map<String, Object> masterBean, List<FormFieldBean> fieldBeans,
                                   List<Comment> comments, ColSummary colSummary, CtpAffair affair) throws BusinessException {
        List<Comment> temp;
        //是否有字段已经设置了流程处理意见，添加一个标识，看要不要更新二维码
        boolean hasSetValue = false;
        if (Strings.isEmpty(comments)) {
            return hasSetValue;
        }
        for (FormFieldBean formFieldBean : fieldBeans) {
            if (!needFillBackDealOpition(formBean, formFieldBean, affair, true)) {
                continue;
            }
            boolean needBackComments = false;
            boolean needTemporaryComments = true;
            boolean needNullComments = true;

            String formatType = formFieldBean.getFormatType();
            if (Strings.isNotBlank(formatType)) {
                if (formatType.contains(FlowDealOptionsType.nullToShow.getKey())) {
                    //【意见为空时均不显示】缺省不勾选，如果勾选，那么处理流程表单时如果意见为空，则均不回填到流程处理意见字段。
                    needNullComments = false;
                }
                if (formatType.contains(FlowDealOptionsType.temporaryToShow.getKey())) {
                    //【暂存待办意见不显示】缺省不勾选，如果勾选，那么处理流程表单时如果暂存待办，则意见不回填到流程处理意见字段。
                    needTemporaryComments = false;
                }
                if (!formatType.contains(FlowDealOptionsType.backToShow.getKey())) {
                    //【回退意见不显示】缺省勾选，如果不勾选，那么在做回退操作的时候的处理意见要回填到流程处理意见字段。
                    needBackComments = true;
                }
            }
            temp = this.getComments(formBean, formFieldBean, comments, colSummary, needBackComments, needTemporaryComments, needNullComments);
            if (Strings.isNotEmpty(temp)) {
                String value = this.getDealOpitionValue(formFieldBean, temp);
                if (formFieldBean.getFieldType().equals(FieldType.VARCHAR.getKey())) {
                    int length = formFieldBean.getFieldLength() == null || !Strings.isDigits(formFieldBean.getFieldLength()) ? 0 : Integer.parseInt(formFieldBean.getFieldLength());
                    if (value.length() > length / 3) {
                        LOGGER.info("表单：" + formBean.getFormName() + "的字段" + formFieldBean.getName() + "意见回填超长被截取，原意见：" + value);
                        value = value.substring(0, length / 3);
                    }
                }
                masterBean.put(formFieldBean.getName(), value);
            } else {
                masterBean.put(formFieldBean.getName(), "");
            }
            hasSetValue = true;
        }
        return hasSetValue;
    }

    /**
     * 获取某一个意见控件值
     *
     * @param formFieldBean
     * @param comments
     * @throws BusinessException
     */
    private String getDealOpitionValue(FormFieldBean formFieldBean, List<Comment> comments) throws BusinessException {
        StringBuilder sb = new StringBuilder();
        String value = null;
        for (int i = 0, j = comments.size(); i < j; i++) {
            Comment comment = comments.get(i);
            value = this.getDealOpition(comment, formFieldBean.getFormatType(), formFieldBean.getName());
            sb.append(value);
            if (i != j - 1) {
                sb.append("\r\n\r\n");
            }
        }
        return sb.toString();
    }

    /**
     * 按照事项分组处理意见
     *
     * @param commonts
     * @param colSummary
     * @param flag                  true 按照事项分组，false 按照人员分组
     * @param needBackComments      是否需要回退的意见
     * @param needTemporaryComments 是否需要暂存待办意见
     * @param needNullComments      意见为空是否显示
     * @return
     * @throws BusinessException
     */
    private Map<Long, List<Comment>> groupCommentByAffair(List<Comment> commonts, ColSummary colSummary, boolean flag,
                                                          boolean needBackComments, boolean needTemporaryComments, boolean needNullComments)
            throws BusinessException {
        Map<Long, List<Comment>> map = new LinkedHashMap<Long, List<Comment>>();
        List<Comment> list;
        List<Comment> temp = new ArrayList<Comment>();
        Long id = null;
        for (Comment comment : commonts) {
            if (flag) {
                id = comment.getAffairId();
                list = map.get(comment.getAffairId());
            } else {
                id = comment.getCreateId();
                list = map.get(comment.getCreateId());
            }
            if (list == null) {
                if (id == null) {
                    continue;
                } else {
                    list = new ArrayList<Comment>();
                    map.put(id, list);
                }
            }
            if (Strings.isBlank(comment.getContent()) && !needNullComments) {
                continue;
            }
            if (this.isTemporaryComment(comment) && !needTemporaryComments) {
                continue;
            }
            if ((this.isRollBackComment(comment) && !needBackComments) || !this.isFillBackComment(comment)) {
                continue;
            }
            if (this.isCancelComment(comment)) {
                for (Entry<Long, List<Comment>> et : map.entrySet()) {
                    et.getValue().clear();
                }
                temp.clear();
            }
            list.add(comment);
            temp.add(comment);
        }
        map.put(0L, temp);
        return map;
    }

    /**
     * 组装流程处理意见 6.0 新功能
     *
     * @param comment
     * @param typeStr   101，102，。。。。。
     * @param fieldName
     * @return
     * @throws BusinessException
     */
    private String getDealOpition(Comment comment, String typeStr, String fieldName) throws BusinessException {
        StringBuilder sb = new StringBuilder();
        String transactor = "";
        V3xOrgMember member;
        if (Strings.isNotBlank(comment.getExtAtt2())) {
            transactor = ResourceUtil.getString("form.dealopition.by.other.label", comment.getExtAtt2());
        }
        //回车换行符加4的空格
        String crlfAndTenSpace = "\r\n    ";
        String space = " ";
        String leftMiddle = "[";
        String rightMiddle = "]";

        member = this.orgManager.getMemberById(comment.getCreateId());
        V3xOrgDepartment department = this.orgManager.getDepartmentById(member.getOrgDepartmentId());
        V3xOrgPost orgPost = this.orgManager.getPostById(member.getOrgPostId());

        String att = StringUtils.fixStrWithSpace(this.getAtt(comment), 5);
        String inscribe = comment.getContent();
        if (Strings.isBlank(inscribe)) {
            inscribe = "";
        }
        String inscribeWithCrlfAndTenSpace = inscribe.replace("\n", crlfAndTenSpace);

        Long affairId = comment.getAffairId();

        if (Strings.isBlank(typeStr)) {
            //格式化为空时，采用默认的值:态度 意见 \r\n    姓名   日期  时间
            sb.append(att).append(space).append(inscribe).append(crlfAndTenSpace).append(member.getName()).append(transactor).append(space).append(comment.getCreateDateStr());
        } else {
            /******************************处理态度、意见**********************/
            boolean isIncludeAtt = typeStr.contains(FlowDealOptionsType.att.getKey());
            boolean isIncludeInscribe = typeStr.contains(FlowDealOptionsType.inscribe.getKey());
            boolean hasDept = typeStr.contains(FlowDealOptionsType.dept.getKey());
            boolean hasPost = typeStr.contains(FlowDealOptionsType.post.getKey());
            boolean hasSecondRow = hasSecondRow(typeStr);
            if (isIncludeAtt) {
                sb.append(att).append(space);

            }
            if (isIncludeInscribe) {
                if (isIncludeAtt) {
                    sb.append(inscribeWithCrlfAndTenSpace);
                } else {
                    sb.append(inscribe);
                }
            }
            if ((isIncludeAtt || isIncludeInscribe) && hasSecondRow) {
                sb.append(crlfAndTenSpace);
            }
            /******************************部门、岗位、姓名、签章。日期、时间**********************/
            if (hasSecondRow(typeStr)) {
                String[] types = typeStr.split(",");
                //只要不设置部门、岗位时，签名、姓名都不显示[]
                if (hasDept || hasPost) {
                    sb.append(leftMiddle);
                }
                for (String one : types) {
                    FlowDealOptionsType type = FlowDealOptionsType.getEnumItemByKey(one);
                    if (null == type) {
                        LOGGER.error("处理流程意见找不到枚举类型，类型key:" + one + "，字段" + fieldName);
                        continue;
                    }
                    switch (type) {
                        case dept:
                            sb.append(department.getName()).append(space);
                            break;
                        case post:
                            sb.append((orgPost == null ? "" : orgPost.getName())).append(space);
                            break;
                        case name:
                            sb.append(member.getName()).append(transactor).append(space);
                            break;
                        case signet:
                            sb.append(getSignetAndSaveHtmlSignet(member, fieldName, affairId, transactor));
                            break;
                        case date:
                            sb.append(comment.getCreateDateStr().substring(0, 10)).append(space);
                            break;
                        case time:
                            sb.append(comment.getCreateDateStr().substring(comment.getCreateDateStr().length() - 5)).append(space);
                            break;
                        default:
                            sb.append("");
                    }
                }
                sb.deleteCharAt(sb.toString().length() - 1);//删掉方括号前的空格
                if (hasDept || hasPost) {
                    sb.append(rightMiddle);
                }
            }
        }
        return sb.toString();
    }

    private boolean hasSecondRow(String formatStr) {
        if (Strings.isNotBlank(formatStr)) {
            if (formatStr.contains(FlowDealOptionsType.dept.getKey()) ||
                    formatStr.contains(FlowDealOptionsType.post.getKey()) ||
                    formatStr.contains(FlowDealOptionsType.name.getKey()) ||
                    formatStr.contains(FlowDealOptionsType.signet.getKey()) ||
                    formatStr.contains(FlowDealOptionsType.date.getKey()) ||
                    formatStr.contains(FlowDealOptionsType.time.getKey())) {
                return true;
            }
        }
        return false;
    }

    private String getAtt(Comment comment) {
        if (this.isDealAtt(comment)) {
            if ("collaboration.dealAttitude.disagree".equals(comment.getExtAtt1())) {
                return "【" + ResourceUtil.getString(comment.getExtAtt1()) + "】";
            } else {
                return "【" + ResourceUtil.getString(comment.getExtAtt1()) + "】  ";
            }
        }
        return "          ";
    }

    /**
     * 得到处理人的签名并保存到html签名历史表(保存格式:部门名称 <signet>小王_field0001_-5908625899231690426</signet>
     * 如果存在多个则再找人员与签章名字相同,如仍不存在，则视为签章不存在，返回人员姓名
     *
     * @param member
     * @param fieldName
     * @param affairId
     * @return
     */
    private String getSignetAndSaveHtmlSignet(V3xOrgMember member, String fieldName, Long affairId, String transactor) {
        String space = " ";
        String memberName = member.getName();
        StringBuilder res = new StringBuilder();
        List<V3xSignet> signetList = signetManager.findSignetByMemberId(member.getId());
        V3xSignet selfSignet = null;
        if (signetList != null && !signetList.isEmpty()) {
            for (V3xSignet v3xSignet : signetList) {
                if (v3xSignet.getMarkType() == 0) {
                    if (selfSignet == null ||
                            (selfSignet != null && selfSignet.getMarkDate().before(v3xSignet.getMarkDate()))) {
                        selfSignet = v3xSignet;
                    }
                }
            }
        }
        if (selfSignet != null) {
            long summaryId = UUIDLong.longUUID();
            V3xHtmDocumentSignature htmlDocSignature = new V3xHtmDocumentSignature();
            htmlDocSignature.setId(summaryId);
            htmlDocSignature.setSummaryId(summaryId);
            htmlDocSignature.setFieldName(fieldName + "_" + summaryId);
            htmlDocSignature.setUserName(memberName);
            htmlDocSignature.setSignetType(1);
            htmlDocSignature.setDateTime(DateUtil.currentTimestamp());
            if (affairId != null) {
                htmlDocSignature.setAffairId(affairId);
            }
            htmlDocSignature.setFieldValue(FormUtil.byte2Hex(selfSignet.getMarkBodyByte()));
            htmSignetManager.save(htmlDocSignature);
            res.append("@signet@").append(memberName).append("_").append(fieldName).append("_").append(summaryId).append("@signet@");
            res.append(transactor).append(space);
        } else {
            res.append(memberName).append(transactor).append(space);
        }
        return res.toString();
    }

    /**
     * 获取某个单元格需要填入的意见列表
     *
     * @param formBean
     * @param formFieldBean
     * @param allComments
     * @param needBackComments      是否需要回退的意见
     * @param needTemporaryComments 意见为空是否显示
     * @param needNullComments      意见为空是否显示
     * @return
     * @throws BusinessException
     */
    private List<Comment> getComments(FormBean formBean, FormFieldBean formFieldBean, List<Comment> allComments,
                                      ColSummary colSummary, boolean needBackComments, boolean needTemporaryComments, boolean needNullComments) throws BusinessException {
        List<Comment> list = new ArrayList<Comment>();
        Map<Long, List<Comment>> map = this.groupCommentByAffair(allComments, colSummary, false, needBackComments, needTemporaryComments, needNullComments);
        List<Comment> comments = map.get(0L);
        List<Comment> temp;
        Comment com = null;
        for (Comment comment : comments) {
            //排除空意见
            if (Strings.isBlank(comment.getContent()) && !needNullComments) {
                continue;
            }
            //排除暂存代办意见
            if (isTemporaryComment(comment) && !needTemporaryComments) {
                continue;
            }
            //排除回退和撤销已经
            if (isRollBackComment(comment) && !needBackComments) {
                continue;
            }
            // 排除移交意见 需求上报-紧急-锦州市鸿升科技有限公司-表单CAP4-KFZX2019012210434-陈艾-2019-01-22 11:45
            if (isTransfer(comment)) {
                continue;
            }
            if (isCancelComment(comment)) {
                list.clear();
                continue;
            }
            if (!this.isFillBackComment(comment)) {
                continue;
            }
            if (null == comment.getAffairId()) {
                continue;
            }
            //判断当前意见是否需要填入意见控件
            if (this.needFillBackDealOpition(formBean, formFieldBean, comment.getAffairId(), true)) {
                //编辑和追加分别处理
                if (this.needFillBackDealOpition(formBean, formFieldBean, comment.getAffairId(), false)) {
                    //排除草稿、回复、发起人附言
                    if (comment.getCtype() != Comment.CommentType.comment.getKey()) {
                        continue;
                    }
                    list.add(comment);
                } else {
                    //是编辑权限，取所有意见中最晚的一个，如果是回退或者撤销意见，则不填入
                    temp = map.get(comment.getCreateId());
                    if (Strings.isNotEmpty(temp)) {
                        for (Comment c : temp) {
                            //排除草稿、回复、发起人附言
                            if (c.getCtype() != Comment.CommentType.comment.getKey()) {
                                continue;
                            }
                            if (this.needFillBackDealOpition(formBean, formFieldBean, c.getAffairId(), true)) {
                                com = c;
                            }
                        }
                        if (com != null) {
                            if ((!isRollBackComment(com) || needBackComments) && comment.getCtype() != Comment.CommentType.draft.getKey()) {
                                if (!list.contains(com)) {
                                    list.add(com);
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 判断节点是否是知会节点
     *
     * @param affair
     * @return
     */
    private boolean isInformNode(CtpAffair affair) {
        return "inform".equals(affair.getNodePolicy());
    }

    /**
     * 判断处理意见是否是回退的
     *
     * @param comment
     * @return
     */
    private boolean isRollBackComment(Comment comment) {
        if (Strings.isNotBlank(comment.getExtAtt3())
                && ("collaboration.dealAttitude.rollback".equals(comment.getExtAtt3()))) {
            return true;
        }
        return false;
    }

    /**
     * 判断意见是否是暂存待办的
     *
     * @param comment
     * @return
     */
    private boolean isTemporaryComment(Comment comment) {
        if (Strings.isNotBlank(comment.getExtAtt3())
                && ("collaboration.dealAttitude.temporaryAbeyance".equals(comment.getExtAtt3()))) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是需要回填的意见，回复意见/草稿返回true，发起人附言/意见回复返回false；
     *
     * @param comment
     * @return
     */
    private boolean isFillBackComment(Comment comment) {
        if (comment.getCtype() == null) {
            return false;
        }
        if (Comment.CommentType.comment.getKey() != comment.getCtype() && Comment.CommentType.draft.getKey() != comment.getCtype()) {
            return false;
        }
        return true;
    }

    private boolean isCancelComment(Comment comment) {
        if (Strings.isNotBlank(comment.getExtAtt3())
                && ("collaboration.dealAttitude.cancelProcess".equals(comment.getExtAtt3()))) {
            return true;
        }
        return false;
    }

    private boolean isDealAtt(Comment comment) {
        if (Strings.isNotBlank(comment.getExtAtt1())
                && ("collaboration.dealAttitude.agree".equals(comment.getExtAtt1()) || "collaboration.dealAttitude.disagree".equals(comment.getExtAtt1()) || "collaboration.dealAttitude.haveRead".equals(comment.getExtAtt1()))) {
            return true;
        }
        return false;
    }

    /**
     * 是否移交意见
     */
    private boolean isTransfer(Comment comment) {
        if (Strings.isNotBlank(comment.getExtAtt3()) && ("collaboration.dealAttitude.transfer".equals(comment.getExtAtt3()))) {
            return true;
        }
        return false;
    }

    private boolean needFillBackDealOpition(FormBean formBean, FormFieldBean formFieldBean, Long affairID, boolean isAdd) throws BusinessException {
        CtpAffair affair = affairManager.get(affairID);
        if (affair == null) {
            LOGGER.error("组装流程意见时，事项：" + affairID + "被删除，该事项对应意见不组装……");
            return false;
        }
        return needFillBackDealOpition(formBean, formFieldBean, affair, isAdd);
    }

    /**
     * 判断事项ID对应的意见是否需要写入单元格
     *
     * @param formBean
     * @param formFieldBean
     * @param isAdd         true:第一次判断是否需要写入，false：第二次判断是编辑还是追加
     * @return 第一次判断：需要写入：true,不需要则false；二次判断：是追加：true,编辑：false
     * @throws BusinessException
     */
    private boolean needFillBackDealOpition(FormBean formBean, FormFieldBean formFieldBean, CtpAffair affair, boolean isAdd)
            throws BusinessException {
        boolean needFillBack = false;
        if (affair == null) {
            LOGGER.error("组装流程意见时，字段：" + formFieldBean.getName() + "对应事项被删除，该事项对应意见不组装……");
            return needFillBack;
        }
        if (AffairUtil.isFormReadonly(affair)) {
            LOGGER.info("组装流程意见时，事项：" + affair.getId() + "为只读权限，该事项对应意见不组装……");
            return needFillBack;
        }
        boolean isMbLogin = (Constants.login_sign.pc.value() != AppContext.getCurrentUser().getLoginSign() && Constants.login_sign.ucpc.value() != AppContext.getCurrentUser().getLoginSign());
        if (!isMbLogin) {
            String rightId = getFirstEditRightId(affair, formBean);
            if ("".equals(rightId)) {
                LOGGER.error("组装流程意见时，事项：" + affair.getId() + "对应权限不存在，该事项对应意见不组装……");
                return needFillBack;
            }
            FormAuthViewBean formAuthViewBean = formBean.getAuthViewBeanById(Long.valueOf(rightId));
            if (formAuthViewBean == null) {
                LOGGER.error("组装流程意见时，事项对应权限：" + rightId + "被删除，该事项对应意见不组装……");
                return needFillBack;
            }

            FormAuthViewFieldBean authViewFieldBean;
            FieldAccessType accessType;
            authViewFieldBean = formAuthViewBean.getFormAuthorizationField(formFieldBean.getName());
            needFillBack = isNeedFillBack(isAdd, authViewFieldBean);
        } else {
            String rithtStr = affair.getMultiViewStr();
            if (rithtStr != null && !"".equals(rithtStr)) {
                String[] viewAndOperation = rithtStr.split("_");
                for (String tempRightStr : viewAndOperation) {
                    String[] bindViewAndOperation = tempRightStr.split("[.]");
                    String operationId = null;
                    if (bindViewAndOperation.length > 1) {
                        operationId = bindViewAndOperation[1];
                    } else {
                        operationId = bindViewAndOperation[0];
                    }
                    FormAuthViewBean authViewBean = formBean.getAuthViewBeanById(Long.parseLong(operationId));
                    if (authViewBean == null) {
                        LOGGER.error("组装流程意见时，事项对应权限：" + operationId + "被删除，该事项对应意见不组装……");
                        return needFillBack;
                    }
                    if ((authViewBean.getType().equals(Enums.FormAuthorizationType.add.getKey()) || authViewBean.getType().equals(Enums.FormAuthorizationType.update.getKey()))) {
                        FormAuthViewFieldBean authViewFieldBean = authViewBean.getFormAuthorizationField(formFieldBean.getName());
                        needFillBack = isNeedFillBack(isAdd, authViewFieldBean);
                        if (needFillBack) {
                            break;
                        }
                    }
                }
            }
        }
        return needFillBack;
    }

    private boolean isNeedFillBack(boolean isAdd, FormAuthViewFieldBean authViewFieldBean) {
        boolean result = false;
        if (authViewFieldBean != null) {
            String accessType = authViewFieldBean.getAccess();
            boolean isAddAuth = FieldAccessType.add.getKey().equals(accessType);
            if (isAddAuth || FieldAccessType.edit.getKey().equals(accessType)) {
                if (isAdd) {
                    result = true;
                } else {
                    if (isAddAuth) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return the cap4FormCacheManager
     */
    public CAP4FormCacheManager getCap4FormCacheManager() {
        return cap4FormCacheManager;
    }

    /**
     * @param cap4FormCacheManager the cap4FormCacheManager to set
     */
    public void setCap4FormCacheManager(CAP4FormCacheManager cap4FormCacheManager) {
        this.cap4FormCacheManager = cap4FormCacheManager;
    }

    /**
     * @return the cap4FormDataDAO
     */
    public CAP4FormDataDAO getCap4FormDataDAO() {
        return cap4FormDataDAO;
    }

    /**
     * @param cap4FormDataDAO the cap4FormDataDAO to set
     */
    public void setCap4FormDataDAO(CAP4FormDataDAO cap4FormDataDAO) {
        this.cap4FormDataDAO = cap4FormDataDAO;
    }


    /**
     * @return the formLogManager
     */
    public FormLogManager getFormLogManager() {
        return formLogManager;
    }

    /**
     * @param formLogManager the formLogManager to set
     */
    public void setFormLogManager(FormLogManager formLogManager) {
        this.formLogManager = formLogManager;
    }

    /**
     * @return the fileManager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * @param fileManager the fileManager to set
     */
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * @return the fileToExcelManager
     */
    public FileToExcelManager getFileToExcelManager() {
        return fileToExcelManager;
    }

    /**
     * @param fileToExcelManager the fileToExcelManager to set
     */
    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
        this.fileToExcelManager = fileToExcelManager;
    }

    public OrgManager getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public AffairManager getAffairManager() {
        return affairManager;
    }

    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }

    /**
     * @return the cap4FormManager
     */
    public CAP4FormManager getCap4FormManager() {
        return cap4FormManager;
    }

    /**
     * @param cap4FormManager the cap4FormManager to set
     */
    public void setCap4FormManager(CAP4FormManager cap4FormManager) {
        this.cap4FormManager = cap4FormManager;
    }

    /**
     * @return the CAP4FormAuthDesignManager
     */
    public CAP4FormAuthDesignManager getCAP4FormAuthDesignManager() {
        return CAP4FormAuthDesignManager;
    }

    /**
     * @param CAP4FormAuthDesignManager the CAP4FormAuthDesignManager to set
     */
    public void setCap4FormAuthDesignManager(CAP4FormAuthDesignManager CAP4FormAuthDesignManager) {
        this.CAP4FormAuthDesignManager = CAP4FormAuthDesignManager;
    }

    public BarCodeManager getBarCodeManager() {
        return barCodeManager;
    }

    public void setBarCodeManager(BarCodeManager barCodeManager) {
        this.barCodeManager = barCodeManager;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public SerialCalRecordManager getSerialCalRecordManager() {
        return serialCalRecordManager;
    }

    public void setSerialCalRecordManager(SerialCalRecordManager serialCalRecordManager) {
        this.serialCalRecordManager = serialCalRecordManager;
    }

    /* (non-Javadoc)
     * @see com.seeyon.cap4.form.modules.engin.base.formData.CAP4FormDataManager#insertOrUpdateMasterData(com.seeyon.cap4.form.bean.FormDataMasterBean)
     */
    @Override
    public void insertOrUpdateMasterData(FormDataMasterBean masterData) throws BusinessException, SQLException {
        this.cap4FormDataDAO.insertOrUpdateMasterData(masterData);
        //添加数据跟踪记录
        dataTrace(masterData, "存储表单");

        //[徐矿集团会议通知功能] 作者：zhou 2021-01-19 开始
        FormTableBean formTable = masterData.getFormTable();
        ProptiesUtil proptiesUtil = new ProptiesUtil();
        //获取会议通知主表的表名
        String formmain = formTable.getTableName();
        //判断表单是不是会议通知表单，“是”就调用泛微接口
        if (formmain.equals(proptiesUtil.getOaMeetingFormmain())) {

            ProptiesUtil pUtil = new ProptiesUtil();
            //获取股份公司表中的所有人员的id;
            List<String> gfgsIds = new ArrayList<>();
            List<String> gfgsNames = new ArrayList<>();
            String sql = "select " + pUtil.getValueByKey("oa.gfgs.tablename.field") + "," + pUtil.getValueByKey("oa.gfgs.tablename.field.obj") + " from " + pUtil.getValueByKey("oa.gfgs.tablename");
            List<Map<String, Object>> lists = JDBCUtil.doQuery(sql);
            Map<String, String> idnamemap = new HashMap<>();
            for (int i = 0; i < lists.size(); i++) {
                Map<String, Object> map = lists.get(i);
                gfgsIds.add((String) map.get(pUtil.getValueByKey("oa.gfgs.tablename.field")));
                idnamemap.put((String) map.get(pUtil.getValueByKey("oa.gfgs.tablename.field")), (String) map.get(pUtil.getValueByKey("oa.gfgs.tablename.field.obj")));
            }
            //获取所选的人
            List<String> selectMids = new ArrayList<>();
            Map<String, List<FormDataSubBean>> sub = masterData.getSubTables();

            for (Map.Entry entry : sub.entrySet()) {
                List<FormDataSubBean> subBeans = (List<FormDataSubBean>) entry.getValue();
                for (FormDataSubBean bean : subBeans) {
                    Map<String, Object> rowData = bean.getRowData();
                    String mId = (String) rowData.get("field0050");
                    selectMids.add(mId);
                    //判断如果含有股份公司的人员的id，就开始调用泛微的接口推送数据。
                    if (gfgsIds.contains(mId)) {
                        String name = idnamemap.get(mId);
                        gfgsNames.add(name);
                    }
                }
            }
            if (gfgsNames.size() > 0) {
                Map<String, Object> allDataMap = masterData.getAllDataMap();
                //在这里判断一下是取消会议，还是新发，或者重发
                String cancelid = allDataMap.get("field0042") + "";
                String retryId = allDataMap.get("field0035") + "";
                String enumSql = "select enumvalue from ctp_enum_item t where t.id = " + cancelid;
                List<Map<String, Object>> enumResult = JDBCUtil.doQuery(enumSql);
                if (enumResult.size() > 0) {
                    Map<String, Object> rMap = enumResult.get(0);
                    String enumvalue = rMap.get("enumvalue") + "";
                    Long id = masterData.getId();
                    System.out.println("id:"+id);
                    CapFormInsertDataEvent insertData = new CapFormInsertDataEvent(this);
                    insertData.setId(id);
                    insertData.setDataMap(allDataMap);
                    insertData.setList(gfgsNames);
                    if ("0".equals(enumvalue)) {//0代表取消，1代表未取消
                        insertData.setType("cancel");
                    } else {
                        if (!"".equals(retryId) && !"null".equals(retryId)) {
                            String enumSql2 = "select enumvalue from ctp_enum_item t where t.id = " + retryId;
                            List<Map<String, Object>> enumList = JDBCUtil.doQuery(enumSql2);
                            if (enumList.size() > 0) {
                                Map<String, Object> m = enumList.get(0);
                                if ("0".equals(m.get("enumvalue") + "")) {
                                    insertData.setType("retry");
                                }
                            }
                        } else {
                            insertData.setType("new");
                        }
                    }
                    EventDispatcher.fireEvent(insertData);
                }

            }

        }
        //[徐矿集团会议通知功能] 作者：zhou 2021-01-19 截止
    }

    @Override
    public boolean isFieldHasUnique(String fieldName, String tableName) throws BusinessException {
        JDBCAgent jdbc = null;
        ResultSet result = null;
        try {
            jdbc = new JDBCAgent();
            StringBuilder sql = new StringBuilder();
            sql.append("select count( " + fieldName + ")  from  " + tableName + " group by (  " + fieldName + " )  HAVING COUNT( " + fieldName + " ) > 1 ");
            jdbc.execute(sql.toString());
            result = jdbc.getQueryResult();
            int count = 0;
            if (result.next()) {
                count = result.getInt(1);
            }
            if (count > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        } finally {
            if (null != result) {
                try {
                    result.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
            if (jdbc != null) {
                jdbc.close();
            }

        }
        return false;
    }

    /**
     * 校验数据唯一和唯一标识，<br>
     * 当设置了多个数据唯一时，其中一个数据唯一字段不满足，则返回<br>
     * 批量修改或者批量刷新时调用
     *
     * @param fb              需要校验的表单
     * @param cacheMasterData 需要校验的数据对象
     * @return Map 包含错误信息和不符合的字段信息
     * @throws BusinessException
     */
    @Override
    public Map<String, Object> validateDataUnique(FormBean fb, FormDataMasterBean cacheMasterData) throws BusinessException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String msg = "1";
        if (Strings.isNotEmpty(fb.getUniqueFieldList())) {
            for (List<String> list : fb.getUniqueFieldList()) {
                if (isUniqueMarked(fb, cacheMasterData, list)) {
                    String s = "";
                    for (String fName : list) {
                        FormFieldBean fieldBean = fb.getFieldBeanByName(fName);
                        if (Strings.isBlank(s)) {
                            s = s + fieldBean.getDisplay();
                        } else {
                            s = s + "," + fieldBean.getDisplay();
                        }
                    }
                    resultMap.put("uniqueField", list);
                    msg = ResourceUtil.getString("form.data.validate.uniqueFlag", s);
                    break;
                }
            }
        }
        resultMap.put("msg", msg);
        return resultMap;
    }

    /**
     * 判断唯一标识  保存数据和批量刷新、批量修改时调用
     */
    @Override
    public boolean isUniqueMarked(FormBean fb, FormDataMasterBean formData, List<String> fieldList) throws BusinessException {
        //记录一个最大值max,该值的目的是为了给?号赋值。因为重复表可能多行，必须每一行都要赋值。主一，重一（第一行）。主一，重一（第二行）
        int max = 1;
        //存入每个字段对应的表名字，一组唯一标识，最多两个表，一主一重，单主，单重
        List<String> allTableName = new ArrayList<String>();
        //记录所有表名list
        List<String> tableNameList = new ArrayList<String>();
        //重复表列表
        List<String> slaveList = new ArrayList<String>();
        String subTableName = "";
        //首先判断重复表字段唯一标识是否有重复的。
        tableNameList.add(fb.getMasterTableBean().getTableName());
        Map<String, Object> masterMap = formData.getRowData();
        //先排除当前数据是否有唯一标识字段为空，找出重复表列
        for (int i = 0; i < fieldList.size(); i++) {
            String fieldName = fieldList.get(i);
            FormFieldBean ffb = fb.getFieldBeanByName(fieldName);
            if (ffb.isMasterField()) {
                String masterValue = String.valueOf(masterMap.get(fieldName));
                //当前主表字段有值为空的，这组唯一标识就不校验了,重复表有多行，所以不能直接返回，需要后面继续验证
                if (Strings.isBlank(masterValue) || "null".equals(masterValue)) {
                    return false;
                }
            }
            String tableName = ffb.getOwnerTableName();
            //通过表名获取子表数据列表。以便得到最大的行数
            List<FormDataSubBean> subList = formData.getSubData(tableName);
            if (subList != null && subList.size() > max) {
                max = subList.size();
            }
            allTableName.add(tableName);
            //添加表名字到list,以便后边和数据库对比时使用
            if (!tableNameList.contains(tableName)) {
                tableNameList.add(tableName);
            }
            //表示重复表字段。首先判断重复表字段是否有重复的数据
            if (ffb != null && !ffb.isMasterField()) {
                slaveList.add(fieldName);
                if ("".equals(subTableName)) {
                    subTableName = ffb.getOwnerTableName();
                }
            }
        }
        if (!"".equals(subTableName)) {
            boolean isSlaveExist = isExistSameSlaveValue(formData.getSubData(subTableName), slaveList);
            if (isSlaveExist) {
                return true;
            }
        }
        /*-------------------------------------去数据库查询是否存在唯一标识组合---------------------------------*/
        JDBCAgent jdbc = null;
        try {
            jdbc = new JDBCAgent();
            //组装from头sql
            StringBuilder sql = new StringBuilder();
            sql.append(" select " + tableNameList.get(0) + ".id from ");
            for (int i = 0; i < tableNameList.size(); i++) {
                if (i == 0) {
                    sql.append(tableNameList.get(i));
                } else {
                    sql.append(" left join " + tableNameList.get(i) + " on " + tableNameList.get(0) + ".id=" + tableNameList.get(i) + ".formmain_id ");
                }
            }
            sql.append(" where   ");
            //循环遍历唯一标识字段列表，组装a0.field0001 = ? and a1.field0003 = ?等等
            for (int i = 0; i < fieldList.size(); i++) {
                //i=末行的时候不应该有and
                if (i == (fieldList.size() - 1)) {
                    //这里么通过遍历需要传入表名列表，然后根据该字段的表名进行添加。
                    for (int k = 0; k < tableNameList.size(); k++) {
                        if (tableNameList.get(k).equals(allTableName.get(i))) {
                            sql.append(tableNameList.get(k) + "." + fieldList.get(i) + "= ? ");
                        }
                    }
                } else {
                    for (int k = 0; k < tableNameList.size(); k++) {
                        if (tableNameList.get(k).equals(allTableName.get(i))) {
                            sql.append(tableNameList.get(k) + "." + fieldList.get(i) + "=" + " ?  and ");
                        }
                    }
                }
            }
            boolean isExistDB = false;
            //循环最大值max,即为了组装?的值。
            for (int i = 0; i < max; i++) {
                List<Object> valueList = new ArrayList<Object>();
                Object value = null;
                //如果某个重复表字段为null值那么就不校验。
                boolean flag = false;
                for (int k = 0; k < fieldList.size(); k++) {
                    FormFieldBean ffb = fb.getFieldBeanByName(fieldList.get(k));
                    List<Object> dataList = formData.getDataList(fieldList.get(k));
                    if (ffb.isMasterField()) {
                        value = dataList != null && dataList.size() > 0 ? dataList.get(0) : null;
                    } else {
                        value = i >= dataList.size() ? null : dataList.get(i);
                    }
                    //如果值为空的话，就不校验。
                    if (value == null) {
                        flag = true;
                        break;
                    }
                    if (FieldType.DATETIME.getKey().equals(ffb.getFieldType())) {
                        if (value instanceof Date) {
                            String s = DateUtil.getDate((Date) value, DateUtil.YMDHMS_PATTERN);
                            value = DateUtil.parseTimestamp(s, DateUtil.YMDHMS_PATTERN);
                        } else if (value instanceof String) {
                            value = DateUtil.parseTimestamp(String.valueOf(value), DateUtil.YMDHMS_PATTERN);
                        }
                    } else if (FieldType.TIMESTAMP.getKey().equals(ffb.getFieldType())) {
                        if (value instanceof Date) {
                            String s = DateUtil.getDate((Date) value, DateUtil.YEAR_MONTH_DAY_PATTERN);
                            value = DateUtil.parseTimestamp(s, DateUtil.YEAR_MONTH_DAY_PATTERN);
                        } else if (value instanceof String) {
                            value = DateUtil.parseTimestamp(String.valueOf(value), DateUtil.YEAR_MONTH_DAY_PATTERN);
                        }
                    }
                    valueList.add(value);
                }
                if (flag) {
                    continue;
                }
                sql.append(" and " + tableNameList.get(0) + ".id <> " + formData.getId());
                int c = 0;
                jdbc.execute(sql.toString(), valueList);
                c = jdbc.resultSetToList().size();
                int count = sql.indexOf("?") > -1 ? c : 0;
                //
                if (count > 0) {
                    isExistDB = true;
                    break;
                }
            }
            return isExistDB;
        } catch (Exception e) {
            LOGGER.error(fb.getFormName() + " 判断数据唯一标识发生异常：" + formData.getId() + e.getMessage(), e);
            throw new BusinessException(e);
        } finally {
            if (jdbc != null) {
                jdbc.close();
            }
        }
    }

    /**
     * 是否存在相同的重复表字段组合
     *
     * @return
     */
    private boolean isExistSameSlaveValue(List<FormDataSubBean> subDatas, List<String> fields) {
        boolean isExist = false;
        if (Strings.isNotEmpty(subDatas) && Strings.isNotEmpty(fields)) {
            StringBuilder sb = new StringBuilder();
            List<String> subLineDataList = new ArrayList<String>();
            for (FormDataSubBean fdsb : subDatas) {
                for (int i = 0; i < fields.size(); i++) {
                    Object subValue = fdsb.getFieldValue(fields.get(i));
                    if (subValue == null) {
                        sb.setLength(0);
                        break;
                    } else {
                        sb.append(subValue);
                    }
                }
                if (sb.length() < 1) {
                    continue;
                }
                if (subLineDataList.contains(sb.toString())) {
                    isExist = true;
                    break;
                } else {
                    subLineDataList.add(sb.toString());
                }
                sb.setLength(0);//清空sb
            }
        }
        return isExist;
    }

    /**
     * 唯一标识组成的字符串当做MD5加密摘要
     * 未启用
     *
     * @return
     */
    @Override
    public String generateMD5ForUnique(FormBean formBean, FormDataMasterBean data) {
        //MD5加密摘要,通过下划线链接唯一标识字段
        String message = "";
        String unique = "";
        String uniqueStr = formBean.getUniqueFieldStr();
        if (!Strings.isBlank(uniqueStr)) {
            List<String> uniqueList = formBean.getUniqueFieldList().get(0);

            for (String fieldName : uniqueList) {
                String temp = data.getFieldValue(fieldName).toString();
                message += temp + "_";
            }
            unique = www.seeyon.com.utils.MD5Util.MD5(message);
        }

        data.addFieldValue("unique_MD5", unique);
        return unique;
    }

    /**
     * 通过唯一标识的MD5值来判断是否符合唯一标识
     * 未启用
     *
     * @param fb
     * @param formData
     * @param fieldList
     * @return
     * @throws BusinessException
     */
    @Override
    public boolean isUniqueByMD5(FormBean fb, FormDataMasterBean formData, List<String> fieldList) throws BusinessException {
        Map<String, Object> masterMap = formData.getRowData();
        String result = "";
        for (int i = 0; i < fieldList.size(); i++) {
            String masterValue = String.valueOf(masterMap.get(fieldList.get(i)));
            result += masterValue + "_";
        }
        String uniqueMD5 = www.seeyon.com.utils.MD5Util.MD5(result);

        JDBCAgent jdbc = null;
        String tableName = fb.getMasterTableBean().getTableName();
        try {
            jdbc = new JDBCAgent();
            StringBuilder sql = new StringBuilder();
            sql.append("select " + tableName + ".unique_MD5 from " + tableName);
            sql.append(" where ");
            sql.append(tableName + ".unique_MD5 = " + "'" + uniqueMD5 + "'");
            sql.append(" and " + tableName + ".id <> " + formData.getId());

            boolean isExistDB = false;
            int count = 0;
            jdbc.execute(sql.toString());
            count = jdbc.resultSetToList().size();
            if (count > 0) {
                isExistDB = true;
            }
            return isExistDB;

        } catch (Exception e) {

            LOGGER.error(fb.getFormName() + " 判断数据唯一标识发生异常：" + formData.getId(), e);
            throw new BusinessException(e);
        } finally {

            if (jdbc != null) {
                jdbc.close();
            }
        }
    }

    /**
     * 唯一标识字段修改后,清空数据库中原来的MD5,然后重新生成
     * 未启用
     *
     * @throws BusinessException
     */
    @Override
    public void refreshMD5UniqueFiled() throws BusinessException {
        // TODO: 2017/10/19 杨泽 唯一标识字段修改后,清空数据库中原来的MD5,然后重新生成
    }

    /**
     * 流程标题和消息模板中的字段和系统变量替换
     * 思路：
     * 将模板转换为字符数组，然后遍历找 '[ '和 '{'，找到后分别将其index放入其Stack中，
     * 只要遇到 ']' 和 '}'，则根据当前的index和pop()出的index进行字符串截取，放入需要替换的list中，
     * 最后遍历list，进行值替换
     */
    @Override
    public String getReplaceMsg(String msg, FormBean fb, Map<String, Object> data, boolean needSubData) throws BusinessException {
        LOGGER.info("开始执行字符串表单数据替换：对象->" + msg + " 当前登录语言 " + AppContext.getLocale());
        String subject = msg;
        char[] msgCharArr = msg.toCharArray();
        Stack<Integer> bigBracketStack = new Stack<Integer>(); // { 所在的索引位置
        Stack<Integer> squareBracketStack = new Stack<Integer>(); // [ 所在的索引位置
        Set<String> needReplaceStrList = new HashSet<String>();
        String temp;
        for (int i = 0; i < msgCharArr.length; i++) {
            char ch = msgCharArr[i];
            switch (ch) {
                case '[':
                    bigBracketStack.push(i);
                    break;
                case ']':
                    temp = msg.substring(bigBracketStack.pop(), i + 1);
                    needReplaceStrList.add(temp);
                    break;
                case '{':
                    squareBracketStack.push(i);
                    break;
                case '}':
                    temp = msg.substring(squareBracketStack.pop(), i + 1);
                    needReplaceStrList.add(temp);
                    break;
            }
        }

        String value = null;
        boolean needReplace = false;
        for (String key : needReplaceStrList) {
            String tempKey = key.substring(1, key.length() - 1);
            //先找字段，再找固定字段，再找系统变量
            FormFieldBean tempFieldBean = fb.getFieldBeanByDisplay(tempKey);
            if (tempFieldBean != null) {
                if (needSubData) {
                    //取重复行第一行的此字段的值
                    value = tempFieldBean.getDisplayValue(data.get(tempFieldBean.getName()), false)[1].toString();
                    needReplace = true;
                } else {
                    if (tempFieldBean.isMasterField()) {
                        Object dbValue = data.get(tempFieldBean.getName());
                        value = tempFieldBean.getDisplayValue(dbValue, false)[1].toString();
                        needReplace = true;
                    }
                }
            } else {
                SystemDataField dataField = SystemDataField.getEnumByText(tempKey);
                if (dataField != null) {
                    needReplace = true;
                    value = dataField.getKey();
                    switch (dataField) {
                        case approvalState:
                            value = FormDataStateEnum.getFlowFormDataStateEnumByKey((Integer) data.get(MasterTableField.state.getKey())).getText();
                            break;
                        case createDate:
                            value = DateUtil.format((Date) data.get(value), DateUtil.YMDHMS_PATTERN);
                            break;
                        case creator:
                            value = orgManager.getMemberById((Long) data.get(value)).getName();
                            break;
                        case flowState:
                            value = FromDataFinishedFlagEnum.getFlowStateByKey((Integer) data.get(MasterTableField.finishedflag.getKey())).getText();
                            break;
                        case modify_date:
                            value = DateUtil.format((Date) data.get(value), DateUtil.YMDHMS_PATTERN);
                            break;
                        case ratifyState:
                            value = FormDataRatifyFlagEnum.getFromLogOperateTypeByKey((Integer) data.get(MasterTableField.ratifyflag.getKey())).getText();
                            break;
                        default:
                            break;
                    }
                } else if (tempKey.contains("System")) { //兼容老数据，不知道是啥时候的版本，系统变量格式为{System.当前登录人员姓名} 现行格式为[当前登录人员姓名]
                    String realKey = key.substring(key.indexOf(".") + 1);
                    FormulaVar var = FormulaVar.getEnumByText(realKey);
                    if (var != null) {
                        needReplace = true;
                        value = var.getValue();
                    }
                } else {
                    FormulaVar var = FormulaVar.getEnumByText(tempKey);
                    if (var != null) {
                        needReplace = true;
                        Object obj = var.getValue();
                        if (obj == null) {
                            value = "";
                        } else {
                            value = obj.toString();
                        }
                    }
                }
            }
            if (needReplace) {
                if (StringUtil.checkNull(value)) {
                    value = "";
                }
                subject = subject.replace(key, value);
            }
        }
        LOGGER.info("结束执行字符串表单数据替换：结果-> " + subject);
        return subject;
    }

    /**
     * 查询动态表的基础方法
     *
     * @param returnFields
     * @param table
     * @param whereId
     * @return
     * @throws BusinessException
     * @throws SQLException
     */
    @Override
    public List<Map<String, Object>> getFormSlaveDataListById(String[] returnFields, String table, String whereId, List<Long> ids) throws BusinessException {
        if (ids != null) {
            String fieldNames = (returnFields == null || returnFields.length == 0) ? "*" : StringUtils.arrayToString(returnFields);
            List<Long>[] subIds = Strings.splitList(ids, 1000);
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (List<Long> subId : subIds) {
                StringBuilder sql = new StringBuilder("select " + fieldNames + " from " + table + " where " + whereId + " in (");
                for (int i = 0; i < subId.size(); i++) {
                    if (i == subId.size() - 1) {
                        sql.append("?");
                    } else {
                        sql.append("?,");
                    }
                }
                sql.append(" ) order by sort asc");
                JDBCAgent jdbc = new JDBCAgent();
                try {
                    jdbc.execute(sql.toString(), subId);
                    List<Map<String, Object>> resList = jdbc.resultSetToList();
                    if (resList != null) {
                        resultList.addAll(resList);
                    }
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new BusinessException(e);
                } finally {
                    if (jdbc != null) {
                        jdbc.close();
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.seeyon.cap4.form.modules.engin.base.formData.CAP4FormDataManager#delFormData(java.lang.Long, java.lang.String, java.lang.Long, boolean)
     */
    @Override
    public boolean delFormData(Long formId, String ids, Long templateId, boolean needLog) throws BusinessException,
            SQLException {
        StringBuffer logs = new StringBuffer();
        FormBean fb = cap4FormCacheManager.getForm(formId);

        List<FormFieldBean> list = fb.getAllFieldBeans();
        String[] masterFields = new String[list.size()];
        ModuleType moduleType = null;
        switch (FormType.getEnumByKey(fb.getFormType())) {
            case unFlowForm:
                moduleType = ModuleType.cap4UnflowForm;
                break;
            default:
                break;
        }
        int master = 0;
        //记录所有需要保存日志的列
        List<FormFieldBean> logfields = new ArrayList<FormFieldBean>();
        for (FormFieldBean ffb : list) {
            //日志记录
            //判断是否需要记录详细日志,日志设置里列+数据唯一的列都要记录
            if (fb.getBind().getLogFieldList().contains(ffb.getOwnerTableName() + "." + ffb.getName())) {
                //查询时只用主表数据进行查询
                if (ffb.isMasterField()) {
                    masterFields[master++] = ffb.getName();
                }
                logfields.add(ffb);
            }
        }
        //如果没有需要记录的列,则置为空,以免formDataDAO.selectDataByMasterId方法报错
        masterFields = masterFields.length == 0 ? null : masterFields;
        FormDataMasterBean fdmb;
        String id[] = ids.split(",");
        LOGGER.info("delFormData ids length : " + id.length + ", formId:" + formId + ", formTemplateId:" + templateId + ", ids:" + ids);
        for (int j = 0; j < id.length; j++) {
            if (!this.cap4FormDataDAO.isExist(id[j], fb.getMasterTableBean().getTableName())) {
                continue;
            }
            //取得日志记录列的数据
            fdmb = cap4FormDataDAO.selectDataByMasterId(Long.parseLong(id[j]), fb, masterFields);
            if (fdmb != null) {
                for (FormFieldBean ffb : logfields) {
                    if (ffb == null) {
                        continue;
                    }
                    Object tempValue = fdmb.getFieldValue(ffb.getName());
                    if (ffb.isSubField()) {
                        List<FormDataSubBean> subBeans = fdmb.getSubData(ffb.getOwnerTableName());
                        if (subBeans != null && subBeans.size() > 0) {
                            tempValue = subBeans.get(0).getFieldValue(ffb.getName());
                        }
                    }
                    //logs.append(FormLogUtil.getLogForDelete(ffb, tempValue));
                }
            }
            //删除全文检索信息
            if (AppContext.hasPlugin("index")) {
                indexManager.delete(Long.parseLong(id[j]), ApplicationCategoryEnum.form.getKey());
            }
            //删除正文数据
            MainbodyService.getInstance().deleteContentAllByModuleId(moduleType, Long.parseLong(id[j]), true);
            //删除表单中lbs相关数据
            lbsManager.deleteAttendanceInfoByMasterDataId(Long.parseLong(id[j]));
            //删除此数据生成的任务F28 --cap无流程表单 and 有任务插件 and 有任务设置
            if (moduleType == ModuleType.cap4UnflowForm && AppContext.hasPlugin("taskmanage")) {
                boolean hasTaskTrigger = false;
                FormTriggerBean triggerBean;
                for (Long triggerId : fb.getFormTriggerIdList()) {
                    triggerBean = cap4FormCacheManager.getFormTriggerBean(triggerId);
                    if (triggerBean != null && TriggerBusinessType.Task.getKey().equals(triggerBean.getType())) {
                        hasTaskTrigger = true;
                        break;
                    }
                }
                if (hasTaskTrigger) {//当前表单设置了任务联动
                    TaskmanageApi taskManageApi = (TaskmanageApi) AppContext.getBean("taskmanageApi");
                    taskManageApi.deleteTaskInfoBySourceRecordId(Long.parseLong(id[j]));
                }
            }

            //删除数据时删除对应的时间调度，不在此处删除，时间调度执行的时候如果数据不存在则不执行，如果执行时间是重复表字段删除时，数据多会有性能问题
            //delTriggerQuartzJod(fb, fdmb);
            if (needLog) {
                //记录日志
                formLogManager.saveOrUpdateLog(formId, cap4FormCacheManager.getForm(formId).getFormType(), templateId, AppContext.currentUserId(), FormLogOperateType.DELETE.getKey(), logs.toString(), fdmb == null ? null : fdmb.getStartMemberId(), fdmb == null ? null : fdmb.getStartDate());
            }
            logs.setLength(0);
        }
        return true;
    }

    public LbsManager getLbsManager() {
        return lbsManager;
    }

    public void setLbsManager(LbsManager lbsManager) {
        this.lbsManager = lbsManager;
    }

    public IndexManager getIndexManager() {
        return indexManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public EnumManager getEnumManagerNew() {
        return enumManagerNew;
    }

    public void setEnumManagerNew(EnumManager enumManagerNew) {
        this.enumManagerNew = enumManagerNew;
    }

    @Override
    public void deleteNotInFormDataLbs(FormDataMasterBean cacheMasterData, String type) {
        try {
            Long formId = cacheMasterData.getFormTable().getFormId();
            FormBean formBean = cap4FormCacheManager.getForm(formId);
            List<FormFieldBean> fields = formBean.getAllFieldBeans();
            List<Long> notInIds = new ArrayList<Long>();
            for (FormFieldBean field : fields) {
                if ((!StringUtil.checkNull(field.getInputType()) && field.getInputType().equalsIgnoreCase(type)) || (!StringUtil.checkNull(String.valueOf(field.getFormatType())) && field.getFormatType().equalsIgnoreCase(type))) {
                    if (field.isMasterField()) {
                        Object val = cacheMasterData.getFieldValue(field.getName());
                        if (!StringUtil.checkNull(String.valueOf(val))) {
                            //地图标注字段回写时给的非数字值，会发生异常 bug OA-98524
                            try {
                                notInIds.add(Long.parseLong(String.valueOf(val)));
                            } catch (Exception e) {
                                LOGGER.info("地图标注字段删除时ID转换异常，字段：" + field.getName() + field.getDisplay() + " 值：" + val);
                            }
                        }
                    } else {
                        List<Object> subFieldVals = cacheMasterData.getDataList(field.getName());
                        for (Object subFieldVal : subFieldVals) {
                            if (!StringUtil.checkNull(String.valueOf(subFieldVal))) {
                                //地图标注字段回写时给的非数字值，会发生异常 bug OA-98524
                                try {
                                    notInIds.add(Long.parseLong(String.valueOf(subFieldVal)));
                                } catch (Exception e) {
                                    LOGGER.info("地图标注字段删除时ID转换异常，字段：" + field.getName() + field.getDisplay() + " 值：" + subFieldVal);
                                }
                            }
                        }
                    }
                }
            }
            if (notInIds.size() > 0) {
                lbsManager.deleteNotInFormData(cacheMasterData.getId(), notInIds);
            }
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void setCap4FormBindDesignManager(CAP4FormBindDesignManager cap4FormBindDesignManager) {
        this.cap4FormBindDesignManager = cap4FormBindDesignManager;
    }

    /**
     * @return the formRelationRecordDAO
     */
    public FormRelationRecordDAO getFormRelationRecordDAO() {
        return formRelationRecordDAO;
    }

    /**
     * @param formRelationRecordDAO the formRelationRecordDAO to set
     */
    public void setFormRelationRecordDAO(FormRelationRecordDAO formRelationRecordDAO) {
        this.formRelationRecordDAO = formRelationRecordDAO;
    }

    /**
     * 批量刷新，返回错误详情列表时，对特殊数据进行加工显示(比如组织模型数据)
     *
     * @param fieldName
     * @param form
     * @return
     */
    private String getDisplayVal(String fieldName, FormDataMasterBean masterBean, FormBean form) {
        String display = "";
        try {
            FormFieldBean field = form.getFieldBeanByName(fieldName);
            if (field == null) {
                MasterTableField masterTableField = MasterTableField.getEnumByKey(fieldName);
                field = masterTableField.getFormFieldBean();
            }
            FormFieldBean realFfb = field;
            Object fieldVal = masterBean.getFieldValue(fieldName);
            Object[] displayVal = realFfb.getDisplayValue(fieldVal);
            if (displayVal != null && displayVal.length > 1) {
                display = (String) displayVal[1];
            }
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage(), e);
        }
        //获得实际的显示值
        return display;
    }


    /**
     * 获取需要刷新的数据bean对象，并放入缓存和数据map中
     *
     * @param formBean 表单对象
     * @param dataId   数据id
     * @param dataMap  数据map
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    private FormDataMasterBean getMasterDataBean(FormBean formBean, Long dataId, Map<String, FormDataMasterBean> dataMap) throws SQLException, BusinessException {
        FormDataMasterBean formDataBean = cap4FormDataDAO.selectDataByMasterId(dataId, formBean, null);
        dataMap.put(dataId.toString(), formDataBean);
        //可编辑的情况下，才产生数据缓存
        cap4FormManager.putSessioMasterDataBean(formBean, formDataBean, true, false);
        return formDataBean;
    }

    @Override
    public List<FormFieldBean> getBatchUpdateFieldBeans(FormBean formBean, String templateId) throws BusinessException {
        List<FormFieldBean> fieldBeans = null;
        if (formBean.getFormType() == Enums.FormType.unFlowForm.getKey()) {
            FormBindBean bindBean = formBean.getBind();
            FormBindAuthBean bindAuthBean = bindBean.getFormBindAuthBean(templateId);
            String auth = bindAuthBean.getAuthByName(FormBindAuthBean.AuthName.BATHUPDATE.getKey());
            if (Strings.isNotBlank(auth)) {
                List<FormFieldBean> authBeans = FormUtil.convertFieldName2BeanList(auth, ",", formBean);
                fieldBeans = cap4FormBindDesignManager.getBathUpdateFields(formBean, authBeans);
            }
        }
        return fieldBeans;
    }

    public SignetManager getSignetManager() {
        return signetManager;
    }

    public void setSignetManager(SignetManager signetManager) {
        this.signetManager = signetManager;
    }

    public V3xHtmDocumentSignatManager getHtmSignetManager() {
        return htmSignetManager;
    }

    public void setHtmSignetManager(V3xHtmDocumentSignatManager htmSignetManager) {
        this.htmSignetManager = htmSignetManager;
    }

    /**
     * 记录无流程的查询日志
     *
     * @param queryType
     */
    private void infoQueryTypeLog(FormQueryTypeEnum queryType) {
        if (LOGGER.isInfoEnabled()) {
            if (queryType == FormQueryTypeEnum.baseManageQuery) {//业务生成器数据维护列表查询【基础数据】
                LOGGER.info("查询类型：业务生成器数据维护列表查询【基础数据】");
            } else if (queryType == FormQueryTypeEnum.infoManageQuery) {//业务生成器数据维护列表查询【信息管理】
                LOGGER.info("查询类型：业务生成器数据维护列表查询【信息管理】");
            } else if (queryType == FormQueryTypeEnum.flowRelationQuery) {//流程相关表单查询
                LOGGER.info("查询类型：流程相关表单查询");
            } else if (queryType == FormQueryTypeEnum.relationFormQuery) {//关联表单查询
                LOGGER.info("查询类型：关联表单查询");
            } else if (queryType == FormQueryTypeEnum.formQuery) {//表单查询
                LOGGER.info("查询类型：表单查询");
            } else if (queryType == FormQueryTypeEnum.unFlowCheckRight) {
                LOGGER.info("查询类型：无流程权限校验");
            } else if (queryType == FormQueryTypeEnum.unFlowExport) {
                LOGGER.info("查询类型：无流程导出");
            } else {
                LOGGER.info("查询类型：未知查询");
            }
        }
    }

    /**
     * 获取无流程导出的sql，仅包括id 用于子表联合查询
     *
     * @param formBean
     * @param params
     * @return
     * @throws BusinessException
     */
    @Override
    public FormQueryWhereClause getFormQueryResultClauseForExport(FormBean formBean, Map<String, Object> params) throws BusinessException {
        Long currentUserId = AppContext.currentUserId();
        FormQueryWhereClause customCondition = getCustomConditionFormQueryWhereClause(formBean, params);
        //M3无流程支持自定义排序20170214
        List<Map<String, Object>> customOrderBy = (List<Map<String, Object>>) params.get("userOrderBy");
        Set<String> customShowFields = (Set<String>) params.get("customShowFields");
        FormQueryWhereClause relationSqlWhereClause = getRelationConditionFormQueryWhereClause(formBean, params);
        Long templeteId = Long.parseLong(Strings.isBlank(String.valueOf(params.get("formTemplateId") == null ? "" : params.get("formTemplateId"))) ? "0" : params.get("formTemplateId") + "");
        FormQueryWhereClause formQueryWhereClause = null;
        try {
            formQueryWhereClause = getFormQuerySql(
                    currentUserId,
                    formBean,
                    templeteId,
                    FormQueryTypeEnum.unFlowExport,
                    customCondition,
                    customOrderBy,
                    customShowFields,
                    relationSqlWhereClause,
                    false);
        } catch (Exception e) {
            LOGGER.error("getFormQueryResultClauseForExport 表单获取sql接口异常！", e);
            throw new BusinessException("getFormQueryResultClauseForExport 表单获取sql接口异常！", e);
        }
        return formQueryWhereClause;
    }

    @Override
    public int insertData(FormDataBean dataBean) throws BusinessException, SQLException {
        int count = cap4FormDataDAO.insertData(dataBean);
        dataTrace(dataBean, "存储表单");
        return count;
    }

    @Override
    public int insertData(List<? extends FormDataBean> dataList) throws BusinessException, SQLException {
        int count = cap4FormDataDAO.insertData(dataList);
        for (FormDataBean formDataBean : dataList) {
            dataTrace(formDataBean, "存储表单");
        }
        return count;
    }

    @Override
    public int insertData(List<? extends FormDataBean> formDataBeanList, boolean updateSort) throws BusinessException, SQLException {
        int count = cap4FormDataDAO.insertData(formDataBeanList, updateSort);
        for (FormDataBean formDataBean : formDataBeanList) {
            dataTrace(formDataBean, "存储表单");
        }
        return count;
    }

    @Override
    public boolean deleteForm(Long masterId, FormBean formBean) throws BusinessException, SQLException {
        boolean deleteForm = cap4FormDataDAO.deleteForm(masterId, formBean);
        dataTrace(masterId, formBean, "删除表单");
        return deleteForm;
    }

    @Override
    public FormQueryResult getFormQueryResult(
            Long currentUserId, FlipInfo flipInfo, boolean isNeedPage,
            FormBean formBean, Long templeteId, FormQueryTypeEnum queryType, FormQueryWhereClause customCondition,
            List<Map<String, Object>> customOrderBy, Set<String> customShowFields, FormQueryWhereClause relationSqlWhereClause, boolean reverse)
            throws BusinessException {
        this.infoQueryTypeLog(queryType);
        try {
            FormQueryWhereClause querySql = getFormQuerySql(
                    currentUserId,
                    formBean,
                    templeteId,
                    queryType,
                    customCondition,
                    customOrderBy,
                    customShowFields,
                    relationSqlWhereClause,
                    reverse);
            LOGGER.info("无流程列表生成的查询sql语句：" + querySql.getAllSqlClause());
            LOGGER.info("无流程列表生成的查询sql参数：" + querySql.getQueryParams());
            FormTableBean masterTableBean = formBean.getMasterTableBean();
            cap4FormDataDAO.selectMasterDataList(flipInfo, masterTableBean, querySql.getAllSqlClause(), querySql.getQueryParams());
            FormQueryResult queryResult = new FormQueryResult();
            queryResult.setFlipInfo(flipInfo);
            queryResult.setQuerySql(querySql.getAllSqlClause());
            queryResult.setQueryParams(querySql.getQueryParams());
            return queryResult;
        } catch (Exception e) {
            LOGGER.error("表单公共查询接口异常！", e);
            throw new BusinessException("表单公共查询接口异常！", e);
        }
    }

    /**
     * @param currentUserId
     * @param formBean
     * @param templeteId
     * @param queryType
     * @param customOrderBy
     * @param customShowFields
     * @return
     * @throws BusinessException
     */
    private FormQueryWhereClause getFormQuerySql(
            Long currentUserId, FormBean formBean, Long templeteId,
            FormQueryTypeEnum queryType, FormQueryWhereClause customSqlWhereClause,
            List<Map<String, Object>> customOrderBy, Set<String> customShowFields,
            FormQueryWhereClause relationSqlWhereClause, boolean reverse) throws Exception {
        StringBuffer sql = new StringBuffer("");
        FormTableBean masterTableBean = formBean.getMasterTableBean();
        FormBindBean bindBean = formBean.getBind();
        List<FormBindAuthBean> bindAuthList = new ArrayList<FormBindAuthBean>();
        if (templeteId == null || templeteId.longValue() == 0) {
            bindAuthList = bindBean.getUnflowFormBindAuthByUserId(currentUserId);
        } else {
            FormBindAuthBean firstFormBindAuthBean = bindBean.getFormBindAuthBean(String.valueOf(templeteId));
            bindAuthList.add(firstFormBindAuthBean);
        }
        int size = bindAuthList.size();
        boolean multiBinds = (size == 1) ? false : true;
        String[] bindSqls = new String[size];
        FormBindAuthBean firstFormBindAuthBean = null;
        List<FormQueryWhereClause> whereClauseList = new ArrayList<FormQueryWhereClause>();
        //OA-79645公司协同：4.13 反馈上来性能问题 和一个严重的bug 详细内容见描述
        //亿化方案为：1、排除where条件相同的一个情况。2、限制5个，前端给提示
        /**
         * 性能优化专项：
         * 2016-11-07 CAP性能优化专项，解除5个应用绑定的限制。
         * 以前用的union关联查询，现在改为or条件查询。
         * 黄奎修改
         */
        for (int i = 0; i < size; i++) {
            //union改为or性能提升, 解除5个应用绑定的限制。
            FormBindAuthBean bindAuth = bindAuthList.get(i);
            if (i == 0) {
                firstFormBindAuthBean = bindAuth;
            }
            //一个bind生成一个where查询条件对象
            FormQueryWhereClause oneWhereClause = getFormQueryWhereClauseForOneBind(formBean, firstFormBindAuthBean,
                    bindAuth, customSqlWhereClause, relationSqlWhereClause,
                    multiBinds, reverse, customOrderBy, customShowFields, queryType);
            if (whereClauseList.isEmpty()) {
                whereClauseList.add(oneWhereClause);
            } else {
                boolean isAdd = false;
                Iterator<FormQueryWhereClause> it = whereClauseList.iterator();
                while (it.hasNext()) {
                    FormQueryWhereClause formQueryWhereClause = it.next();
                    String whereSql = formQueryWhereClause.getWhereClause();
                    if (whereSql.equals(oneWhereClause.getWhereClause())) {
                        List<Object> queryParams = formQueryWhereClause.getQueryParams();
                        List<Object> oneQueryParams = oneWhereClause.getQueryParams();
                        if (Strings.isNotEmpty(queryParams) && queryParams.size() == oneQueryParams.size()) {
                            for (int k = 0; k < queryParams.size(); k++) {
                                Object o1 = queryParams.get(k);
                                Object o2 = oneQueryParams.get(k);
                                if (!Strings.equals(o1, o2)) {
                                    isAdd = true;
                                    break;
                                }
                            }
                        } else {
                            LOGGER.error("--sql param is error-->" + queryParams + "<----->" + oneQueryParams);
                        }
                    } else {
                        isAdd = true;
                    }
                }
                if (isAdd) {
                    whereClauseList.add(oneWhereClause);
                }
            }
        }
        //用union合并结果集
        /**
         * 性能优化专项：
         * 2016-11-07 CAP性能优化专项，应用绑定关联底表数据列表选择加载很慢的性能问题。
         * 以前用的union关联查询，现在改为or条件查询。
         * 注意：union在没有建立索引时性能是差于or的，特别是应用绑定很多，union关联的很多时候慢的一塌糊涂。
         * 黄奎修改
         */
        if (multiBinds && whereClauseList.size() > 1) {
            List<Object> allParams = new ArrayList<Object>();
            for (int i = 0; i < whereClauseList.size(); i++) {
                FormQueryWhereClause abindWhereClause = whereClauseList.get(i);
                if (i == 0) {//首个需要select语句部分
                    sql.append(" ").append(abindWhereClause.getAllSqlClause()).append(" ");
                    if (Strings.isBlank(abindWhereClause.getWhereClause())) {//应用绑定中没有设置操作范围，那就查询所有数据，其他应用绑定不再拼sql
                        break;
                    }
                } else {
                    //后面的只需要条件部分用 or 连接
                    if (Strings.isNotBlank(abindWhereClause.getWhereClause())) {
                        //第二个应用绑定需要判断下是否有子表字段参与条件，但是在第一个应用的from里面没有，如果没有，这里给加上
                        for (String formson : abindWhereClause.getFormsons()) {
                            if (sql.indexOf(formson) == -1) {
                                sql = sql.replace(sql.indexOf("where"), sql.indexOf("where") + 5, "," + formson + " where");
                            }
                        }
                        sql.append(" or ").append(abindWhereClause.getWhereClause());
                    } else {
                        sql.append(" or 1=1 ");
                    }
                }
                if (null != abindWhereClause.getQueryParams() && !abindWhereClause.getQueryParams().isEmpty()) {
                    allParams.addAll(abindWhereClause.getQueryParams());
                }
            }
            String sortFields = firstFormBindAuthBean.getSortStr(masterTableBean.getTableName(), reverse);
            //sqlserver下面order by字段不能重复，要判断是否有创建时间
            sortFields = " order by " + (Strings.isBlank(sortFields) ? "" : sortFields + ",");
            String collation = " asc ";
            if (reverse) {
                collation = "desc";
            }
            if (!sortFields.contains("start_date")) {
                sortFields += masterTableBean.getTableName() + ".start_date " + collation + " ";
            } else {
                sortFields = sortFields.substring(0, sortFields.lastIndexOf(","));
            }
            sql.append(sortFields).append(",").append(masterTableBean.getTableName()).append(".id ").append(collation).append(" ");
            FormQueryWhereClause allQueryClause = new FormQueryWhereClause();
            allQueryClause.setAllSqlClause(sql.toString().replaceAll("<> null", "is not null"));
            allQueryClause.setQueryParams(allParams);
            return allQueryClause;
        } else if (multiBinds && whereClauseList.size() == 1) {
            FormQueryWhereClause queryClause = whereClauseList.get(0);
            String s = queryClause.getAllSqlClause();
            if (Strings.isNotBlank(s) && s.indexOf("order by") < 0) {
                String sortFields = firstFormBindAuthBean.getSortStr(masterTableBean.getTableName(), reverse);
                sortFields = " order by " + (Strings.isBlank(sortFields) ? "" : sortFields + ",");
                String collation = " asc ";
                if (reverse) {
                    collation = "desc";
                }
                if (!sortFields.contains("start_date")) {
                    sortFields += masterTableBean.getTableName() + ".start_date " + collation + " ";
                } else {
                    sortFields = sortFields.substring(0, sortFields.lastIndexOf(","));
                }
                StringBuilder sortFieldsSb = new StringBuilder("");
                sortFieldsSb.append(sortFields).append(",").append(masterTableBean.getTableName()).append(".id ").append(collation).append(" ");
                queryClause.setAllSqlClause(queryClause.getAllSqlClause().replaceAll("<> null", "is not null") + sortFieldsSb.toString());
            } else {
                if (Strings.isNotBlank(s)) {
                    queryClause.setAllSqlClause(queryClause.getAllSqlClause().replaceAll("<> null", "is not null"));
                }
                return queryClause;
            }
            return queryClause;
        } else {
            FormQueryWhereClause queryClause = whereClauseList.get(0);
            if (Strings.isNotBlank(queryClause.getAllSqlClause())) {
                String allSql = queryClause.getAllSqlClause();
                allSql = allSql.replaceAll("<> null", "is not null");
                //加上自定义的排序语句
                if (Strings.isNotBlank(queryClause.getOrderByClause())) {
                    allSql = allSql + "order by " + queryClause.getOrderByClause();
                }
                queryClause.setAllSqlClause(allSql);
            }
            return queryClause;
        }
    }

    /**
     * 一个bind生成一个where查询条件对象
     * 这里面如果没有自定义排序，就只处理了无流程列表的默认排序，关联表单的排序放到外面的方法处理的。
     *
     * @param customOrderBy M3无流程自定义排序
     * @return
     */
    private FormQueryWhereClause getFormQueryWhereClauseForOneBind(
            FormBean formBean,
            FormBindAuthBean firstFormBindAuthBean,
            FormBindAuthBean bindAuth,
            FormQueryWhereClause customSqlWhereClause,
            FormQueryWhereClause relationSqlWhereClause,
            boolean multiBinds, boolean reverse,
            List<Map<String, Object>> customOrderBy, Set<String> customShowFields, FormQueryTypeEnum queryType) {
        FormFormulaBean formulaBean = bindAuth.getFormFormulaBean();
        FormQueryWhereClause allWhereClause = new FormQueryWhereClause();
        List<Object> queryParams = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer("");
        if (formulaBean != null) {
            //获得该绑定授权的过滤条件sql语句和查询参数对象, 操作范围
            FormQueryWhereClause formulaWhereClause = formulaBean.getExecuteFormulaForWhereClauseSQL(null, true, true);
            if (null != formulaWhereClause) {
                if (Strings.isNotBlank(formulaWhereClause.getAllSqlClause())) {
                    sb.append(" (").append(formulaWhereClause.getAllSqlClause()).append(") ");
                }
                if (null != formulaWhereClause.getQueryParams() && !formulaWhereClause.getQueryParams().isEmpty()) {
                    queryParams.addAll(formulaWhereClause.getQueryParams());
                }
            }
            //增加查询条件：拼接自定义增加的条件sql
            if (null != customSqlWhereClause) {
                if (Strings.isNotBlank(customSqlWhereClause.getAllSqlClause())) {
                    sb.append(" and (").append(customSqlWhereClause.getAllSqlClause()).append(") ");
                }
                if (null != customSqlWhereClause.getQueryParams() && !customSqlWhereClause.getQueryParams().isEmpty()) {
                    queryParams.addAll(customSqlWhereClause.getQueryParams());
                }
            }
            //拼接关联表单条件sql语句
            if (null != relationSqlWhereClause) {
                if (Strings.isNotBlank(relationSqlWhereClause.getAllSqlClause())) {
                    sb.append(" and (").append(relationSqlWhereClause.getAllSqlClause()).append(") ");
                }
                if (null != relationSqlWhereClause.getQueryParams() && !relationSqlWhereClause.getQueryParams().isEmpty()) {
                    queryParams.addAll(relationSqlWhereClause.getQueryParams());
                }
            }
        } else {
            boolean hasSql = false;
            //增加查询条件：拼接自定义增加的条件sql
            if (null != customSqlWhereClause) {
                if (Strings.isNotBlank(customSqlWhereClause.getAllSqlClause())) {
                    sb.append(" (").append(customSqlWhereClause.getAllSqlClause()).append(") ");
                    hasSql = true;
                }
                if (null != customSqlWhereClause.getQueryParams() && !customSqlWhereClause.getQueryParams().isEmpty()) {
                    queryParams.addAll(customSqlWhereClause.getQueryParams());
                }
            }
            //拼接关联表单条件sql语句
            if (null != relationSqlWhereClause) {
                if (Strings.isNotBlank(relationSqlWhereClause.getAllSqlClause())) {
                    if (hasSql) {
                        sb.append(" and (").append(relationSqlWhereClause.getAllSqlClause()).append(") ");
                    } else {
                        sb.append(" (").append(relationSqlWhereClause.getAllSqlClause()).append(") ");
                    }
                }
                if (null != relationSqlWhereClause.getQueryParams() && !relationSqlWhereClause.getQueryParams().isEmpty()) {
                    queryParams.addAll(relationSqlWhereClause.getQueryParams());
                }
            }
        }
        /*******
         * 20170421
         * 无流程打开的时候或者全文检索穿透的时候需要校验是否有权限打开单据，防止越权，这里采用统一的方式，保持一致，就不再单独用groovy来执行了
         * 查询字段就只查询id，然后在where语句最前面加id = contentDataId
         *******/
        boolean fromCheckRight = false;
        String contentDataId = AppContext.getThreadContext(FormConstant.CHECK_RIGHT_DATA_ID) == null ? "" : AppContext.getThreadContext(FormConstant.CHECK_RIGHT_DATA_ID).toString();
        if (Strings.isNotBlank(contentDataId)) {
            fromCheckRight = true;
        }
        /*********************/
        String whereClause = sb.toString();

        FormTableBean masterTableBean = formBean.getMasterTableBean();
        Set<String> fieldSet = new LinkedHashSet<String>();
        List<SimpleObjectBean> showFieldObjs = firstFormBindAuthBean.getShowFieldList();
        for (int i = 0; i < showFieldObjs.size(); i++) {
            SimpleObjectBean showField = showFieldObjs.get(i);
            String fieldName = showField.getName();
            if (!fieldName.contains(".")) {
                fieldName = masterTableBean.getTableName() + "." + fieldName;
            }
            fieldSet.add(fieldName);
        }
        //追加一些自定义的列
        if (customShowFields != null && !customShowFields.isEmpty()) {
            //fieldSet.clear();
            for (String field : customShowFields) {
                if (field.indexOf(".") == -1) {
                    field = masterTableBean.getTableName() + "." + field;
                }
                fieldSet.add(field);
            }
        }
        String[] constFieldStrs = MasterTableField.getKeys();
        for (int i = 0; i < constFieldStrs.length; i++) {
            String constFieldStr = masterTableBean.getTableName() + "." + constFieldStrs[i];
            if (!fieldSet.contains(constFieldStr)) {
                fieldSet.add(constFieldStr);
            }
        }
        String[] fields = new String[fieldSet.size()];
        int j = 0;
        for (String s : fieldSet) {
            fields[j++] = s;
        }
        String fieldNames = StringUtils.arrayToString(fields);
        StringBuffer sql = new StringBuffer();
        if (fromCheckRight || queryType == FormQueryTypeEnum.unFlowExport) {
            sql.append(" select ").append(masterTableBean.getTableName()).append(".id");
        } else {
            sql.append(" select DISTINCT ").append(fieldNames);
        }
        sql.append(" from ").append(masterTableBean.getTableName()).append(" ").append(masterTableBean.getTableName()).append(" ");

        List<FormFieldBean> allFields = formBean.getAllFieldBeans();
        StringBuffer from = new StringBuffer(" ");
        for (FormFieldBean fieldBean : allFields) {
            if (fieldBean.isConstantField() || fieldBean.isMasterField()) {
                continue;
            }
            //判断select后边的字符串中是否包含子表字段，如果有，则from后边需要跟子表名字
            if (from.indexOf(fieldBean.getOwnerTableName()) == -1 && sql.indexOf(fieldBean.getName()) != -1) {
                from.append(",").append(fieldBean.getOwnerTableName());
                allWhereClause.putFormson(fieldBean.getOwnerTableName());
            }
            //判断where后边的字符串中是否包含子表字段，如果有，则from后边需要跟子表名字
            if (from.indexOf(fieldBean.getOwnerTableName()) == -1 && whereClause.indexOf(fieldBean.getName()) != -1) {
                from.append(",").append(fieldBean.getOwnerTableName());
                allWhereClause.putFormson(fieldBean.getOwnerTableName());
            }
            //判断sortFields后边的字符串中是否包含子表字段，如果有，则from后边需要跟子表名字
            if (from.indexOf(fieldBean.getOwnerTableName()) == -1 && whereClause.indexOf(fieldBean.getName()) != -1) {
                from.append(",").append(fieldBean.getOwnerTableName());
                allWhereClause.putFormson(fieldBean.getOwnerTableName());
            }
        }
        sql.append(from);

        List<FormTableBean> subTables = formBean.getSubTableBean();
        StringBuffer subTablewhereSb = new StringBuffer("");
        for (int i = 0; i < subTables.size(); i++) {
            if (subTables.get(i).isMainTable()) {
                continue;
            }
            if (sql.indexOf(subTables.get(i).getTableName()) != -1
                    && subTablewhereSb.indexOf(subTables.get(i).getTableName()) == -1) {
                subTablewhereSb.append(" (").append(subTables.get(i).getTableName()).append(".")
                        .append(SubTableField.formmain_id.getKey()).append("=")
                        .append(formBean.getMasterTableBean().getTableName()).append(".")
                        .append(MasterTableField.id.getKey()).append(") ");
                subTablewhereSb.append(" and ");
            }
        }
        int andIndex = subTablewhereSb.lastIndexOf("and ");
        if (andIndex != -1) {
            subTablewhereSb = subTablewhereSb.replace(andIndex, andIndex + "and ".length(), "");
        }
        if (!StringUtil.checkNull(subTablewhereSb.toString().trim())) {
            whereClause = subTablewhereSb.append(" and ").append(whereClause).toString();
        }
        if (fromCheckRight) {
            if (!StringUtil.checkNull(whereClause.trim())) {
                whereClause = " " + masterTableBean.getTableName() + ".id = " + contentDataId + " and " + whereClause;
            } else {
                whereClause = " " + masterTableBean.getTableName() + ".id = " + contentDataId;
            }
        }
        if (!StringUtil.checkNull(whereClause.trim())) {
            sql.append(" where ");
            whereClause = FormUtil.changeAndAddNullWhereSql(whereClause);
            sql.append(whereClause);
        }
        allWhereClause.setWhereClause(whereClause);
        andIndex = sql.lastIndexOf("and ");
        if (andIndex != -1 && andIndex == (sql.length() - "and ".length())) {
            sql = sql.replace(andIndex, andIndex + "and ".length(), "");
        }
        boolean customSort = false;
        if (!Strings.isEmpty(customOrderBy)) {
            getCustomOrderByString(customOrderBy, allWhereClause, reverse, masterTableBean.getTableName());
            customSort = true;
        }
        if (!multiBinds && !customSort && !fromCheckRight && queryType != FormQueryTypeEnum.unFlowExport) {
            String sortFields = firstFormBindAuthBean.getSortStr(masterTableBean.getTableName(), reverse);
            //sqlserver下面order by字段不能重复，要判断是否有创建时间
            sortFields = " order by " + (Strings.isBlank(sortFields) ? "" : sortFields + ",");
            String collation = " asc ";
            if (reverse) {
                collation = "desc";
            }
            String orderRule = "";
            if ("dm dbms".equals(SystemEnvironment.getDatabaseType())) {
                orderRule = " nulls first ";
                if ("desc".equals(collation)) {
                    orderRule = " nulls last ";
                }
                collation += orderRule;
            }
            if (!sortFields.contains("start_date")) {
                sortFields += masterTableBean.getTableName() + ".start_date " + collation + " ";
            } else {
                sortFields = sortFields.substring(0, sortFields.lastIndexOf(","));
            }
            sql.append(" ").append(sortFields).append(",").append(masterTableBean.getTableName()).append(".id ").append(collation).append(" ");
        }
        allWhereClause.setAllSqlClause(sql.toString());
        allWhereClause.setQueryParams(queryParams);
        return allWhereClause;
    }

    /**
     * @param allWhereClause sql对象
     * @param sortList       自定义排序字段
     * @param reverse        预留，是否反转
     *                       获取用户自定义的排序sql
     * @return
     */
    private void getCustomOrderByString(List<Map<String, Object>> sortList, FormQueryWhereClause allWhereClause, boolean reverse, String tableName) {
        StringBuffer sb = new StringBuffer();
        String sortStr = "";
        if (!Strings.isEmpty(sortList)) {
            for (Map<String, Object> map : sortList) {
                String orderType = map.get("orderType").toString();
                sb.append(tableName).append(".").append(map.get("fieldName")).append(" ").append(orderType);
                String orderRule = "";
                if ("dm dbms".equals(SystemEnvironment.getDatabaseType())) {
                    orderRule = " nulls first ";
                    if ("desc".equals(orderType)) {
                        orderRule = " nulls last ";
                    }
                    sb.append(" ").append(orderRule);
                }
                sb.append(" ,");
            }
            sb.append(tableName).append(".id asc ");
        }
        sortStr = sb.toString();
        if (sortStr.endsWith(",")) {
            sortStr = sortStr.substring(0, sortStr.length() - 1);
        }
        allWhereClause.setOrderByClause(sortStr);
    }

    @Override
    public String checkBindsNum(Map<String, Object> params) {
        String msg = "";
        String formIdStr = (String) params.get("formId");
        if (Strings.isNotBlank(formIdStr)) {
            Long formId = Long.parseLong(formIdStr);
            FormBean formBean = cap4FormCacheManager.getForm(formId.longValue());
            if (formBean.getFormType() == FormType.unFlowForm.getKey()) {
                FormBindBean bindBean = formBean.getBind();
                long currentUserId = AppContext.getCurrentUser().getId();
                List<FormBindAuthBean> bindAuthList = new ArrayList<FormBindAuthBean>();
                try {
                    bindAuthList = bindBean.getUnflowFormBindAuthByUserId(currentUserId);
                } catch (BusinessException e) {
                    LOGGER.error(e.getMessage(), e);
                }

                if (bindAuthList.size() > 5) {
                    msg = ResourceUtil.getString("form.check.bindnum");
                }
            }
        }
        return msg;
    }

    private boolean isChildrenWorkFlow(ColSummary colSummary) {
        return Integer.valueOf(ColConstant.NewflowType.child.ordinal()).equals(colSummary.getNewflowType());
    }


    @Override
    public void insertMasterData(List<FormDataMasterBean> masterBeanList) throws BusinessException, SQLException {
        insertOrUpdateMasterData(masterBeanList, false);
        //添加数据追踪
        for (FormDataMasterBean masterBean : masterBeanList) {
            dataTrace(masterBean, "存储表单");
        }
    }

    /**
     * 批量更新数据 针对回写批量更新动态表使用 采用先删除后新增的方式 add by chenxb 2016-11-30
     */
    @Override
    public void insertOrUpdateMasterData(List<FormDataMasterBean> masterBeanList, boolean needDelete) throws BusinessException, SQLException {
        cap4FormDataDAO.insertOrUpdateMasterData(masterBeanList, needDelete);
        //添加数据追踪
        for (FormDataMasterBean masterBean : masterBeanList) {
            dataTrace(masterBean, "存储表单");
        }
    }

    /**
     * 批量删除数据
     */
    @Override
    public void deleteMasterData(List<FormDataMasterBean> masterBeanList) throws BusinessException, SQLException {
        cap4FormDataDAO.deleteMasterData(masterBeanList);
        //添加数据追踪
        for (FormDataMasterBean masterBean : masterBeanList) {
            dataTrace(masterBean, "删除表单");
        }
    }

    /**
     * 删除指定表单的指定数据
     */
    @Override
    public void deleteData(Long masterId, FormBean formBean) throws BusinessException, SQLException {
        cap4FormDataDAO.deleteForm(masterId, formBean);
        dataTrace(masterId, formBean, "删除表单");
    }

    /**
     * 判断数据唯一标识 新逻辑进行校验
     *
     * @param fb          表单
     * @param fieldList   唯一标识组合
     * @param indexList   大数据量索引字段List
     * @param masterBeans 需要校验的数据
     * @return boolean true--不存在或者只有一条  false--存在多余1条
     */
    @SuppressWarnings("unchecked")
    public boolean isUniqueMarked(FormBean fb, List<String> fieldList, List<String> indexList, List<FormDataMasterBean> masterBeans) throws BusinessException {
        String mainTableName = fb.getMasterTableBean().getTableName();
        String subTableName = "";
        boolean hasMainTable = false;
        boolean hasSubTable = false;
        List<String> subFieldList = new ArrayList<String>();
        String subIndexField = "";
        for (String fieldName : fieldList) {
            FormFieldBean ffb = fb.getFieldBeanByName(fieldName);
            if (ffb.isMasterField()) {
                hasMainTable = true;
            } else {
                hasSubTable = true;
                subTableName = ffb.getOwnerTableName();
                subFieldList.add(fieldName);
                if ("".equals(subIndexField) && indexList.contains(fieldName)) {
                    subIndexField = fieldName;
                }
            }
        }

        boolean executeNewLogic = false;
        // 唯一标识组合是主从表结构，且索引字段有在此组合中的重复表字段，或者当前需要校验的数据多余50，则采用新逻辑
        if (hasMainTable && hasSubTable && Strings.isNotEmpty(subIndexField)) {
            executeNewLogic = true;
        }
        Map<String, List<Object>> subIndexValueListMap = new HashMap<String, List<Object>>();
        if (!executeNewLogic) {
            return isUniqueMarked(fb, fieldList);
        } else {
            for (FormDataMasterBean masterBean : masterBeans) {
                for (String subField : subFieldList) {
                    List<Object> dataList = masterBean.getDataList(subField);
                    if (dataList.size() > 50) {
                        executeNewLogic = false;
                        break;
                    }
                    subIndexValueListMap.put(subField, dataList);
                }
            }
        }
        if (!executeNewLogic) {
            return isUniqueMarked(fb, fieldList);
        }

        // 采用新逻辑进行校验
        String selectFields = Strings.join(fieldList, ",");
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("select sum(num) as count from (select count(*) as num, ").append(selectFields).append(" from (");
        List<Object> valueList = new ArrayList<Object>();
        JDBCAgent jdbc = new JDBCAgent();
        try {
            StringBuilder paramAll = new StringBuilder();
            StringBuilder param = new StringBuilder();
            int index = 0;
            int dataCount = 0;
            for (String subField : subFieldList) {
                index++;
                paramAll.append(subField).append(" in(");
                FormFieldBean ffb = fb.getFieldBeanByName(subField);
                int tempCount = 0;
                for (Object obj : subIndexValueListMap.get(subField)) {
                    if (obj == null) {
                        continue;
                    }
                    param.append(" ?,");
                    Object value = obj;
                    if (FieldType.DATETIME.getKey().equals(ffb.getFieldType())) {
                        if (value instanceof Date) {
                            String s = DateUtil.getDate((Date) value, DateUtil.YMDHMS_PATTERN);
                            value = DateUtil.parseTimestamp(s, DateUtil.YMDHMS_PATTERN);
                        } else if (value instanceof String) {
                            value = DateUtil.parseTimestamp(String.valueOf(value), DateUtil.YMDHMS_PATTERN);
                        }
                    } else if (FieldType.TIMESTAMP.getKey().equals(ffb.getFieldType())) {
                        if (value instanceof Date) {
                            String s = DateUtil.getDate((Date) value, DateUtil.YEAR_MONTH_DAY_PATTERN);
                            value = DateUtil.parseTimestamp(s, DateUtil.YEAR_MONTH_DAY_PATTERN);
                        } else if (value instanceof String) {
                            value = DateUtil.parseTimestamp(String.valueOf(value), DateUtil.YEAR_MONTH_DAY_PATTERN);
                        }
                    }
                    valueList.add(value);
                    tempCount++;
                }
                if (dataCount < tempCount) {
                    dataCount = tempCount;
                }

                String paramSql = param.toString();
                if (Strings.isNotEmpty(paramSql)) {
                    paramSql = paramSql.substring(0, paramSql.lastIndexOf(","));
                }
                paramAll.append(paramSql).append(")");
                if (index < subFieldList.size()) {
                    paramAll.append(" and ");
                }
            }

            // 仅重复表索引
            sqlSb.append("select ").append(selectFields).append(" from ").append(mainTableName).append(" fm,");
            sqlSb.append(" (").append("select formmain_id, ").append(Strings.join(subFieldList, ",")).append(" from ").append(subTableName);
            sqlSb.append(" where ").append(paramAll);
            sqlSb.append(" group by formmain_id, ").append(Strings.join(subFieldList, ",")).append(" ) fs ");
            sqlSb.append(" where fm.id = fs.formmain_id ");
            sqlSb.append(") a group by ").append(selectFields).append(") b");

            /*SELECT sum(num) AS count
            FROM
              ( SELECT count(*) AS num, field0001, field0009
                FROM
                    (
                            SELECT field0001, field0009
                            FROM formmain_1117 fm,
                            ( SELECT formmain_id, field0009
                            FROM formson_1118
                            WHERE field0009 IN ('1', '2', '6', '7', '3')
                            GROUP BY formmain_id, field0009
                    ) fs
                WHERE fm.id = fs.formmain_id
			  ) a
                GROUP BY field0001, field0009
              ) b*/

            jdbc.execute(sqlSb.toString(), valueList);
            Map m = jdbc.resultSetToMap();
            int count = m.get("count") == null ? 0 : Integer.parseInt(String.valueOf(m.get("count")));
            if (count != dataCount) {
                LOGGER.info("唯一标示：" + sqlSb.toString() + " param:" + valueList + " count:" + count + " dataCount:" + dataCount);
                return false;
            }
        } catch (Exception e) {
            LOGGER.info("唯一标示：" + sqlSb.toString() + " param:" + valueList);
            throw new BusinessException(e);
        } finally {
            jdbc.close();
        }
        return true;
    }

    /**
     * 判断数据唯一标识 第一次查询所有的，第二次查询过滤掉重复的，然后比较两个size的大小，如果第一次比第二次多，那就是存在重复的，则表示违反了唯一标识规则
     * 触发类数据保存时调用
     *
     * @param fb        表单
     * @param fieldList 唯一标识组合
     * @return boolean true--不存在或者只有一条  false--存在多余1条
     */
    @Override
    public boolean isUniqueMarked(FormBean fb, List<String> fieldList) throws BusinessException {
        String mainTableName = fb.getMasterTableBean().getTableName();
        String subTableName = "";
        boolean hasMainTable = false;
        boolean hasSubTable = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder whereSb = new StringBuilder();
        String str = "";
        for (String fieldName : fieldList) {
            FormFieldBean ffb = fb.getFieldBeanByName(fieldName);
            if (!hasSubTable && ffb.isSubField()) {
                subTableName = ffb.getOwnerTableName();
                hasSubTable = true;
            }
            if (!hasMainTable && ffb.isMasterField()) {
                hasMainTable = true;
            }
            sb.append(fieldName).append(", ");

            boolean isTime = false;
            if (FieldType.DATETIME.getKey().equals(ffb.getFieldType()) || FieldType.TIMESTAMP.getKey().equals(ffb.getFieldType())) {
                isTime = true;
            }

            if (isTime) {
                str = " " + fieldName + " is not null and";
            } else {
                if (JDBCAgent.getDBType().contains("server")) {
                    str = " isnull(" + fieldName + ", '0') != '0' and";
                } else if ("oracle".equals(JDBCAgent.getDBType()) || "dm dbms".equals(JDBCAgent.getDBType())) {
                    str = " NVL(" + fieldName + ", '0') != '0' and";
                } else if ("mysql".equals(JDBCAgent.getDBType())) {
                    str = " ifnull(" + fieldName + ", '0') != '0' and";
                } else if ("postgre".equals(JDBCAgent.getDBType())) {
                    str = " coalesce(" + fieldName + ", '0') != '0' and";
                } else {
                    str = " " + fieldName + " is not null and";
                }
            }
            whereSb.append(str);
        }
        String selectSql = sb.toString();
        selectSql = selectSql.substring(0, selectSql.length() - 2);
        String where = whereSb.toString();
        where = StringUtils.replaceLast(where, "and", "");

        String tableName = "";
        if (hasMainTable) {
            tableName = mainTableName;
        }
        String unionSQL = "";
        if (hasSubTable) {
            if (hasMainTable) {
                tableName += " fm, " + subTableName + " fs";
                unionSQL = "fm.id = fs.formmain_id and ";
            } else {
                tableName = subTableName;
            }
        }

        //第一次查询所有的，第二次查询过滤掉重复的，然后比较两个size的大小，如果第一次比第二次多，那就是存在重复的
        StringBuilder sqlSB = new StringBuilder();
        sqlSB.append("select ");
        sqlSB.append(selectSql);
        sqlSB.append(" from ").append(tableName);
        sqlSB.append(" where ");
        sqlSB.append(unionSQL);
        sqlSB.append(where);
        String sql = "select count(*) as nnum from (" + sqlSB.toString() + ") t";
        List<Map> result = cap4FormDataDAO.selectDataBySql(sql);
        int v_size1 = result == null ? 0 : Integer.valueOf(result.get(0).get("nnum").toString());
        LOGGER.info("唯一标识1:" + v_size1);

        sqlSB = new StringBuilder();
        sqlSB.append("select distinct ");
        sqlSB.append(selectSql);
        sqlSB.append(" from ").append(tableName);
        sqlSB.append(" where ");
        sqlSB.append(unionSQL);
        sqlSB.append(where);
        sql = "select count(*) as nnum from (" + sqlSB.toString() + ") t";
        result = cap4FormDataDAO.selectDataBySql(sql);
        int v_size2 = result == null ? 0 : Integer.valueOf(result.get(0).get("nnum").toString());
        LOGGER.info("唯一标识2:" + v_size2);

        return !(v_size1 > v_size2);
    }

    /**
     * 校验单个字段的唯一标识 触发类数据保存时调用
     *
     * @param fb        表单
     * @param fieldName 唯一字段
     * @param valueList 对应唯一字段的值集
     * @return boolean true--不存在或者只有一条  false--存在多余1条
     */
    @Override
    public boolean isFieldUnique(FormBean fb, String fieldName, List<Object> valueList) throws BusinessException {
        FormFieldBean ffb = fb.getFieldBeanByName(fieldName);
        String tableName = ffb.getOwnerTableName();

        //首先,如果是重复表字段，先判断传进来的list本身是否有重复的
        if ((ffb.isMasterField() && valueList.size() > 1) || ffb.isSubField()) {
            //该变量目的是用来判断是否有重复的
            List<Object> list = new ArrayList<Object>();
            boolean isExist = false;
            for (Object value : valueList) {
                if (value == null) {
                    continue;
                }
                if (list.contains(value)) {
                    isExist = true;
                    break;
                } else {
                    list.add(value);
                }
            }
            if (isExist) {
                return false;
            }
        }

        JDBCAgent jdbc = new JDBCAgent();
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<Object>();
        try {
            sql.append(" select count(id) as num from " + tableName + " where ");
            int tempNum = 0;
            //遍历数据组装
            for (int j = 0; j < valueList.size(); j++) {
                Object value = valueList.get(j);
                if (value == null) {
                    continue;
                }
                boolean isTime = false;
                if (FieldType.DATETIME.getKey().equals(ffb.getFieldType())) {
                    if (value instanceof Date) {
                        String s = DateUtil.getDate((Date) value, DateUtil.YMDHMS_PATTERN);
                        value = DateUtil.parseTimestamp(s, DateUtil.YMDHMS_PATTERN);
                    } else if (value instanceof String) {
                        value = DateUtil.parseTimestamp(String.valueOf(value), DateUtil.YMDHMS_PATTERN);
                    }
                    isTime = true;
                } else if (FieldType.TIMESTAMP.getKey().equals(ffb.getFieldType())) {
                    if (value instanceof Date) {
                        String s = DateUtil.getDate((Date) value, DateUtil.YEAR_MONTH_DAY_PATTERN);
                        value = DateUtil.parseTimestamp(s, DateUtil.YEAR_MONTH_DAY_PATTERN);
                    } else if (value instanceof String) {
                        value = DateUtil.parseTimestamp(String.valueOf(value), DateUtil.YEAR_MONTH_DAY_PATTERN);
                    }
                    isTime = true;
                }

                String str = "";
                if (isTime) {
                    str = " " + fieldName + " is not null";
                } else {
                    if (JDBCAgent.getDBType().contains("server")) {
                        str = " isnull(" + fieldName + ", '0') != '0'";
                    } else if ("oracle".equals(JDBCAgent.getDBType()) || "dm dbms".equals(JDBCAgent.getDBType())) {
                        str = " NVL(" + fieldName + ", '0') != '0'";
                    } else if ("mysql".equals(JDBCAgent.getDBType())) {
                        str = " ifnull(" + fieldName + ", '0') != '0'";
                    } else if ("postgre".equals(JDBCAgent.getDBType())) {
                        str = " coalesce(" + fieldName + ", '0') != '0'";
                    } else {
                        str = " " + fieldName + " is not null";
                    }
                }

                //第一条特殊处理，同时这边也可能就只有一条的情况。
                if (j == 0) {
                    sql.append(" (" + ffb.getName() + " =  ? and " + str + ") ");
                } else {
                    if (sql.indexOf("?") > -1) {
                        sql.append(" or (" + ffb.getName() + " =  ? and " + str + ") ");
                    } else {
                        sql.append(" (" + ffb.getName() + " =  ? and " + str + ") ");
                    }
                }
                values.add(value);
                tempNum++;
            }
            int count = 0;
            if (sql.indexOf("?") > -1) {
                jdbc.execute(sql.toString(), values);
                Map m = jdbc.resultSetToMap();
                count = m.get("num") == null ? 0 : Integer.parseInt(String.valueOf(m.get("num")));
            }
            LOGGER.info("数据唯一：" + sql.toString() + " param:" + values + " count:" + count + " tempNum:" + tempNum);
            //返回count大于tempNum表示有重复数据
            if (count > tempNum) {
                return false;
            }
        } catch (Exception e) {
            LOGGER.info("数据唯一：" + sql.toString() + " param:" + values);
            LOGGER.error(ffb.getName() + " 做数据唯一判断时异常，" + e.getMessage(), e);
            throw new BusinessException(e);
        } finally {
            jdbc.close();
        }
        return true;
    }

    /**
     * 原无流程cap3基本查询
     *
     * @param flipInfo
     * @param params    查询参数
     * @param forExport
     * @return
     * @throws BusinessException
     */
    @Override
    public FlipInfo getFormMasterDataListBySearch(FlipInfo flipInfo, Map<String, Object> params, boolean forExport) throws BusinessException {
        Long formId = Long.parseLong(params.get("formId") + "");
        FormBean formBean = cap4FormCacheManager.getForm(formId.longValue());
        String templateIdStr = String.valueOf(params.get("formTemplateId"));
        String auth = FormUtil.getUnflowFormAuth(formBean, templateIdStr);
        Long currentUserId = AppContext.currentUserId();
        boolean isNeedPage = true;
        FormQueryTypeEnum queryType = FormQueryTypeEnum.infoManageQuery;
        FormQueryWhereClause customCondition = FormSearchUtil.getCustomConditionFormQueryWhereClause(formBean, params);
        //M3无流程支持自定义排序20170214
        List<Map<String, Object>> customOrderBy = (List<Map<String, Object>>) params.get("userOrderBy");
        Set<String> customShowFields = new HashSet<String>();
        FormQueryWhereClause relationSqlWhereClause = getRelationConditionFormQueryWhereClause(formBean, params);
        Long templeteId = Long.parseLong(Strings.isBlank(String.valueOf(params.get("formTemplateId") == null ? "" : params.get("formTemplateId"))) ? "0" : params.get("formTemplateId") + "");
        FormQueryResult queryResult = getFormQueryResult(currentUserId, flipInfo, isNeedPage, formBean, templeteId, queryType,
                customCondition, customOrderBy, customShowFields, relationSqlWhereClause, false);
        //处理组织机构等id类型数据，这样列表里面显示出来的是显示值，如人员id对应应该显示人员姓名
        //OA-62881  表单信息管理和基础数据列表中隐藏字段显示了  调用查询已经实现的接口完成
        flipInfo.setData(FormUtil.setShowValueList(formBean, auth, flipInfo.getData(), false, forExport));
        return queryResult.getFlipInfo();
    }

    /**
     * 后台刷新表单中自定义控件的内容
     *
     * @param params 传入参数
     *               key:  formBean
     *               formDataMasterBean
     *               moduleId: 正文moduleId
     */
    @Override
    public void refreshCustomControls(Map<String, Object> params) {
        try {
            FormBean formBean = (FormBean) params.get("formBean");
            List<FormFieldBean> allCustomFields = formBean.getCustomFields();
            for (FormFieldBean fieldBean : allCustomFields) {
                params.put("formFieldBean", fieldBean);
                ((FormFieldCustomCtrl) fieldBean.getFieldCtrl()).refresh(params);
            }
        } catch (BusinessException e) {
            LOGGER.error("failed to refresh custom controls! cause:" + e.getMessage(), e);
        }
    }


    /**
     * 数据追踪方法
     *
     * @param formDataBean
     */
    private void dataTrace(FormDataBean formDataBean, String operateType) {
        if (CAP4MonitorUtil.needDataTrace(formDataBean.getFormTable().getFormId())) {
            if (formDataBean instanceof FormDataMasterBean) {
                FormDataMasterBean formDataMasterBean = (FormDataMasterBean) formDataBean;
                HistoryObj masterHistoryObj = HistoryObjManager.newObj(formDataMasterBean.getId(), formDataMasterBean.getFormTable().getTableName(), operateType, "表单存储001", new FormDataTraceBean(formDataMasterBean.getFormTable()));
                HistoryObjManager.getInstance().put(masterHistoryObj);
                if (formDataMasterBean.getSubTables() != null && formDataMasterBean.getSubTables().size() > 0) {
                    Map<String, List<FormDataSubBean>> subTables = formDataMasterBean.getSubTables();
                    List<String> tableNames = new ArrayList<String>(subTables.keySet());
                    Collections.sort(tableNames);//排序依次
                    for (String tableName : tableNames) {
                        List<FormDataSubBean> subDatas = subTables.get(tableName);
                        for (FormDataSubBean subData : subDatas) {
                            HistoryObj subHistoryObj = HistoryObjManager.newObj(subData.getId(), tableName, operateType, "表单存储001", new FormDataTraceBean(subData.getFormTable()));
                            HistoryObjManager.getInstance().put(subHistoryObj);
                        }
                    }
                }
            } else if (formDataBean instanceof FormDataSubBean) {
                FormDataSubBean formDataSubBean = (FormDataSubBean) formDataBean;
                HistoryObj subHistoryObj = HistoryObjManager.newObj(formDataSubBean.getId(), formDataSubBean.getFormTable().getTableName(), "存储表单", "表单存储001", new FormDataTraceBean(formDataSubBean.getFormTable()));
                HistoryObjManager.getInstance().put(subHistoryObj);
            }
        }
    }

    /**
     * 数据追踪方法
     *
     * @param masterId
     * @param formBean
     * @param operateType
     */
    private void dataTrace(Long masterId, FormBean formBean, String operateType) {
        List<FormTableBean> tableList = formBean.getTableList();
        for (int i = 0; i < tableList.size(); i++) {
            FormTableBean tableBean = tableList.get(i);
            if (CAP4MonitorUtil.needDataTrace(tableBean.getFormId())) {
                HistoryObj masterHistoryObj = HistoryObjManager.newObj(masterId, tableBean.getTableName(), operateType, "表单存储001", new FormDataTraceBean(tableBean));
                HistoryObjManager.getInstance().put(masterHistoryObj);
            }
        }
    }

    public void setCap4FormRelationActionManager(CAP4FormRelationActionManager cap4FormRelationActionManager) {
        this.cap4FormRelationActionManager = cap4FormRelationActionManager;
    }

    public void setCap4FormTriggerManager(CAP4FormTriggerManager cap4FormTriggerManager) {
        this.cap4FormTriggerManager = cap4FormTriggerManager;
    }

    public void setCap4FormRelationRecordDAO(CAP4FormRelationRecordDAO cap4FormRelationRecordDAO) {
        this.cap4FormRelationRecordDAO = cap4FormRelationRecordDAO;
    }

    /**
     * 内部类，数据追踪
     */
    class FormDataTraceBean implements ISaveData {

        private FormTableBean formTableBean;

        public FormDataTraceBean(FormTableBean formTableBean) {
            this.formTableBean = formTableBean;
        }

        @Override
        public String toSaveText() {
            return formTableBean.toJSON();
        }

        public FormTableBean getFormTableBean() {
            return formTableBean;
        }

        public void setFormTableBean(FormTableBean formTableBean) {
            this.formTableBean = formTableBean;
        }
    }
}
