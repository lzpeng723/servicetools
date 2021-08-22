package kd.lzp.servicetools.util;

import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.form.CloseCallBack;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.ShowType;
import kd.bos.schedule.api.JobInfo;
import kd.bos.schedule.form.JobFormInfo;

/**
 * 重写 kd.bos.schedule.form.JobForm 原生 kd.bos.schedule.form.JobForm 不支持分应用运行
 *
 * @author lzpeng
 * @version 1.0
 * @description 代码执行器插件
 * @see kd.bos.schedule.form.JobForm
 * @since 2021-05-02 08:36
 */
public class JobForm {
    public static void dispatch(JobInfo jobInfo, IFormView view, CloseCallBack closeCallBack) {
        JobFormInfo clientJobInfo = new JobFormInfo(jobInfo);
        clientJobInfo.setCloseCallBack(closeCallBack);
        dispatch(clientJobInfo, view);
    }

    public static void dispatch(JobFormInfo jobFormInfo, IFormView view) {
        JobInfo jobInfo = jobFormInfo.getJobInfo();
        jobFormInfo.setRootPageId(view.getFormShowParameter().getRootPageId());
        jobFormInfo.setParentPageId(view.getPageId());
        FormShowParameter showParameter = new FormShowParameter();
        showParameter.setFormId("sch_taskprogress");
        showParameter.getOpenStyle().setShowType(ShowType.Modal);

        if (jobFormInfo.getCaption() != null) {
            showParameter.setCaption(jobFormInfo.getCaption());
        } else if (jobInfo.getName() != null) {
            showParameter.setCaption(jobInfo.getName());
        }
        // 重新设置在哪个appId下运行
        if (jobInfo.getAppId() != null) {
            showParameter.getCustomParams().put("ServiceAppId", jobInfo.getAppId());
        } else if (view.getFormShowParameter().getServiceAppId() != null) {
            showParameter.getCustomParams().put("ServiceAppId", view.getFormShowParameter().getServiceAppId());
        }
        String jobInfoStr = SerializationUtils.toJsonString(jobFormInfo);
        showParameter.getCustomParams().put("sch_clientjobinfo", jobInfoStr);
        showParameter.setCloseCallBack(jobFormInfo.getCloseCallBack());
        view.showForm(showParameter);
    }
}