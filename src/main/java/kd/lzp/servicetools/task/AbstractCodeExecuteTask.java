package kd.lzp.servicetools.task;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.instance.Instance;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.lzp.servicetools.util.ExceptionUtils;
import kd.lzp.servicetools.util.OperationResultUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lzpeng
 * @version 1.0
 * @description 抽象的代码执行器任务
 * @since 2021-05-02 11:28
 */
public abstract class AbstractCodeExecuteTask extends AbstractTask {

    public static final String RETURN_VALUE = "RETURN_VALUE";

    public static final String APP_NAME = "APP_NAME";

    public static final String HOST_ADDRESS = "HOST_ADDRESS";

    public static final String MSG_TYPE = "MSG_TYPE";


    protected Log log = LogFactory.getLog(getClass());

    private Map<String, Object> returnMap;

    /**
     * 获得需要返回给页面的map
     *
     * @return
     */
    protected Map<String, Object> getReturnMap() {
        if (returnMap != null) {
            return returnMap;
        }
        returnMap = new HashMap<>(4);
        returnMap.put(APP_NAME, Instance.getAppName());
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            returnMap.put(HOST_ADDRESS, hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 设置returnMap
     *
     * @param returnMap
     */
    public void setReturnMap(Map<String, Object> returnMap) {
        this.returnMap = returnMap;
    }

    /**
     * 返回数据到页面
     *
     * @param data 待返回的数据
     */
    public void returnDataToView(Object data) {
        getReturnMap().put(MSG_TYPE, "success");
        getReturnMap().put(RETURN_VALUE, String.valueOf(data));
    }

    /**
     * 返回异常到页面
     *
     * @param e 待返回的异常
     */
    public void returnExceptionToView(Exception e) {
        getReturnMap().put(MSG_TYPE, "error");
        getReturnMap().put(RETURN_VALUE, ExceptionUtils.getDetailMessage(e));
    }

    /**
     * 返回执行进度给前端页面
     *
     * @param progress 进度
     * @param desc     描述
     */
    public void feedbackProgress(int progress, String desc) {
        feedbackProgress(progress, desc, null);
    }


    /**
     * 保存代码执行记录
     *
     * @param ctx 上下文
     * @param map 执行的代码信息
     */
    protected void saveCodeExecuteRecord(RequestContext ctx, Map<String, Object> map) {
        Long codeId = (Long) map.get("codeId");
        Long executeTime = (Long) map.get("executeTime");
        String code = (String) map.get("code");
        String userId = ctx.getUserId();
        String executeStatus = (String) getReturnMap().get(MSG_TYPE);
        String returnValue = (String) getReturnMap().get(RETURN_VALUE);
        String appName = (String) getReturnMap().get(APP_NAME);
        String taskId = this.taskId;
        DynamicObject codeType = BusinessDataServiceHelper.loadSingle(codeId, "lzp_code_type", "name,number");
        DynamicObject codeExecuteRecord = BusinessDataServiceHelper.newDynamicObject("lzp_code_execute_record");
        codeExecuteRecord.set("enable", 1);
        codeExecuteRecord.set("status", "C");
        codeExecuteRecord.set("creator", userId);
        codeExecuteRecord.set("createtime", executeTime);
        codeExecuteRecord.set("modifytime", System.currentTimeMillis());
        codeExecuteRecord.set("name", String.join("-", codeType.getString("name"), appName));
        codeExecuteRecord.set("number", String.join("-", codeType.getString("number"), taskId));
        codeExecuteRecord.set("lzp_code_type", codeId);
        codeExecuteRecord.set("lzp_code_tag", code);
        codeExecuteRecord.set("lzp_execute_status", executeStatus);
        codeExecuteRecord.set("lzp_return_value_tag", returnValue);
        codeExecuteRecord.set("lzp_sch_task", taskId);
        OperationResult operationResult = SaveServiceHelper.saveOperate("lzp_code_execute_record", new DynamicObject[]{codeExecuteRecord}, null);
        if (!operationResult.isSuccess()) {
            if (this.log.isErrorEnabled()) {
                String errorMsg = OperationResultUtils.getErrorMsg(operationResult);
                this.log.error("保存代码执行记录失败: " + SerializationUtils.toJsonString(codeExecuteRecord) + "\r\n 失败原因: " + errorMsg);
            }

        } else if (this.log.isInfoEnabled()) {
            this.log.info("保存代码执行记录成功: " + SerializationUtils.toJsonString(codeExecuteRecord));
        }
    }

}
