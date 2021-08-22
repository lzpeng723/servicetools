package kd.lzp.servicetools.formplugin;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.CloseCallBack;
import kd.bos.form.control.CodeEdit;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.api.JobInfo;
import kd.bos.schedule.api.JobType;
import kd.bos.schedule.api.TaskInfo;
import kd.bos.schedule.form.JobFormInfo;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.lzp.servicetools.task.AbstractCodeExecuteTask;
import kd.lzp.servicetools.util.JobForm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lzpeng
 * @version 1.0
 * @description 代码执行器插件
 * @since 2021-05-02 08:36
 */
@SuppressWarnings("unused")
public class CodeExecuteFormPlugin extends AbstractFormPlugin {


    public static final String EXECUTE_CODE_JOB = "EXECUTE_CODE_JOB";

    /**
     * 值更新事件
     *
     * @param e 事件
     */
    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        String name = e.getProperty().getName();
        if ("lzp_code_type".equals(name)) {
            ChangeData changeData = e.getChangeSet()[0];
            DynamicObject codeType = (DynamicObject) changeData.getNewValue();
            if (codeType != null) {
                DynamicObjectType type = codeType.getDynamicObjectType();
                codeType = QueryServiceHelper.queryOne(type.getName(), "lzp_code_template_tag", new QFilter[]{
                        new QFilter("id", QCP.equals, codeType.getPkValue())
                });
                String code = codeType.getString("lzp_code_template_tag");
                CodeEdit codeEdit = this.getControl("lzp_code_template_str");
                codeEdit.setText(code);
            }
        }
    }

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs e) {
        super.afterDoOperation(e);
        OperationResult operationResult = e.getOperationResult();
        if (operationResult == null || operationResult.isSuccess()) {
            String operateKey = e.getOperateKey();
            if ("lzp_execute_code".equals(operateKey)) {
                CodeEdit codeEdit = this.getControl("lzp_code_template_str");
                String code = codeEdit.getText();
                DynamicObject codeType = (DynamicObject) this.getModel().getValue("lzp_code_type");
                DynamicObject executeApp = (DynamicObject) this.getModel().getValue("lzp_execute_app");
                String className = codeType.getString("lzp_execute_class");
                String codeName = codeType.getString("name");
                //创建任务目标
                JobInfo jobInfo = new JobInfo();
                //执行类所在的应用名
                String appId = executeApp.getString("number");
                jobInfo.setAppId(appId);
                //即时执行
                jobInfo.setJobType(JobType.REALTIME);
                jobInfo.setName("动态执行脚本|代码任务 (" + codeName + ")");
                //随机产生一个JobId(任务目标的标识)
                jobInfo.setId(UUID.randomUUID().toString());
                jobInfo.setTaskClassname(className);
                //自定义参数
                Map<String, Object> customParams = new HashMap<>(2);
                customParams.put("code", code);
                customParams.put("codeName", codeName);
                customParams.put("codeId", codeType.getPkValue());
                customParams.put("executeTime", System.currentTimeMillis());
                jobInfo.setParams(customParams);
                //回调参数，设置一个回调处理标识(actionId)
                CloseCallBack closeCallBack = new CloseCallBack(this, EXECUTE_CODE_JOB);
                JobFormInfo jobFormInfo = new JobFormInfo(jobInfo);
                jobFormInfo.setCloseCallBack(closeCallBack);
                jobFormInfo.setCanBackground(true);
                // jobFormInfo.setCanStop(true);
                JobForm.dispatch(jobFormInfo, this.getView());
                this.getModel().setValue("lzp_execute_name", null);
                this.getModel().setValue("lzp_execute_ip", null);
            }
        }
    }

    /**
     * 回调事件，在任务处理完毕后继续后续处理
     */
    @Override
    public void closedCallBack(ClosedCallBackEvent e) {
        super.closedCallBack(e);
        String actionId = e.getActionId();
        if (EXECUTE_CODE_JOB.equals(actionId)) {
            Object returnData = e.getReturnData();
            if (returnData == null) {
                return;
            }
            if (returnData instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked") Map<String, Object> result = (Map<String, Object>) returnData;
                if (result.containsKey("taskinfo")) {
                    String taskInfoStr = (String) result.get("taskinfo");
                    if (StringUtils.isNotBlank(taskInfoStr)) {
                        TaskInfo taskInfo = SerializationUtils.fromJsonString(taskInfoStr, TaskInfo.class);
                        if (taskInfo.isTaskEnd()) {
                            // 获取任务执行完毕，生成的内容
                            String data = taskInfo.getData();
                            JSONObject jsonObj = JSONUtil.parseObj(data);
                            String instanceId = jsonObj.getStr(AbstractCodeExecuteTask.APP_NAME);
                            String hostAddress = jsonObj.getStr(AbstractCodeExecuteTask.HOST_ADDRESS);
                            String msgType = jsonObj.getStr(AbstractCodeExecuteTask.MSG_TYPE);
                            String returnValue = jsonObj.getStr(AbstractCodeExecuteTask.RETURN_VALUE);
                            this.getModel().setValue("lzp_execute_name", instanceId);
                            this.getModel().setValue("lzp_execute_ip", hostAddress);
                            if (returnValue != null) {
                                if ("success".equals(msgType)) {
                                    this.getView().showMessage(returnValue);
                                } else if ("error".equals(msgType)) {
                                    this.getView().showErrMessage(returnValue, "执行过程中出现异常" + System.lineSeparator());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
