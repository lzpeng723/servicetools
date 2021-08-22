package kd.lzp.servicetools.formplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.control.CodeEdit;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.url.UrlService;
import kd.bos.web.actions.MetadataAction;
import kd.lzp.servicetools.servicehelper.ClassService;
import kd.lzp.servicetools.servicehelper.ServiceFactory;

import java.io.InputStream;
import java.util.Map;

/**
 * 类加载查看插件
 *
 * @author lzpeng
 * @version 1.0
 * @description 代码执行器插件
 * @since 2021-05-02 21:36
 */
@SuppressWarnings("unused")
public class ClassLoaderFormPlugin extends AbstractFormPlugin {

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs e) {
        super.afterDoOperation(e);
        OperationResult operationResult = e.getOperationResult();
        if (operationResult == null || operationResult.isSuccess()) {
            String operateKey = e.getOperateKey();
            if ("lzp_search_class".equals(operateKey)) {
                searchClass();
            } else if ("lzp_download_class".equals(operateKey)) {
                downloadClass();
            } else if ("lzp_download_jar".equals(operateKey)) {
                downloadJar();
            } else if ("lzp_view_manifest".equals(operateKey)) {
                viewManifestInfo();
            } else if ("lzp_metadata_help".equals(operateKey)) {
                String domainContextUrl = UrlService.getMobileDomainContextUrl();
                InputStream in = MetadataAction.class.getResourceAsStream("/metadataapihelp.html");
                this.getView().openUrl(domainContextUrl + "/metadata/help.do");
            }
        }
    }

    /**
     * 下载jar清单文件
     */
    private void viewManifestInfo() {
        String className = (String) this.getModel().getValue("lzp_class_name");
        DynamicObject executeApp = (DynamicObject) this.getModel().getValue("lzp_execute_app");
        String appId = executeApp.getString("number");
        Map<String, Object> classInfoMap = DispatchServiceHelper.invokeService(kd.lzp.servicetools.servicehelper.ServiceFactory.class.getPackage().getName(), appId, ClassService.class.getSimpleName(), "getManifestInfo", className);
        if (classInfoMap != null) {
            this.getModel().setValue("lzp_execute_name", classInfoMap.get("APP_NAME"));
            String manifestStr = (String) classInfoMap.get("manifestStr");
            String locationUrl = (String) classInfoMap.get("locationUrl");
            this.getModel().setValue("lzp_class_path", classInfoMap.get("locationUrl"));
            if (manifestStr != null) {
                this.getView().showMessage(manifestStr);
            } else if (locationUrl != null) {
                this.getView().showMessage("没有在【" + executeApp.getString("name") + "】应用下找到 " + className + "所在jar包的清单文件");
            } else {
                this.getView().showMessage("没有在【" + executeApp.getString("name") + "】应用下找到 " + className);
            }
        } else {
            this.getView().showMessage("没有在【" + executeApp.getString("name") + "】应用下找到 " + className);
        }
    }

    /**
     * 下载jar包
     */
    private void downloadJar() {
        String className = (String) getModel().getValue("lzp_class_name");
        DynamicObject executeApp = (DynamicObject) getModel().getValue("lzp_execute_app");
        String appId = executeApp.getString("number");
        String appName = executeApp.getString("name");
        Map<String, Object> classInfoMap = DispatchServiceHelper.invokeService(kd.lzp.servicetools.servicehelper.ServiceFactory.class.getPackage().getName(), appId, ClassService.class.getSimpleName(), "downloadJarFile", new Object[]{className});
        if (classInfoMap != null) {
            Object mServiceName = classInfoMap.get("APP_NAME");
            getModel().setValue("lzp_execute_name", mServiceName);
            String url = (String) classInfoMap.get("url");
            if (url != null) {
                getView().openUrl(url);
            } else {
                getView().showMessage(String.format("没有在【%s (%s) 】应用下找到 %s", appName, mServiceName, className));
            }
        } else {
            getView().showMessage(String.format("没有在【%s】应用下找到 %s", appName, className));
        }
    }

    /**
     * 下载class文件
     */
    private void downloadClass() {
        String className = (String) getModel().getValue("lzp_class_name");
        DynamicObject executeApp = (DynamicObject) getModel().getValue("lzp_execute_app");
        String appId = executeApp.getString("number");
        String appName = executeApp.getString("name");
        Map<String, Object> classInfoMap = DispatchServiceHelper.invokeService(ServiceFactory.class.getPackage().getName(), appId, ClassService.class.getSimpleName(), "downloadClassFile", new Object[]{className});
        if (classInfoMap != null) {
            Object mServiceName = classInfoMap.get("APP_NAME");
            getModel().setValue("lzp_execute_name", mServiceName);
            String url = (String) classInfoMap.get("url");
            if (url != null) {
                getView().openUrl(url);
            } else {
                getView().showMessage(String.format("没有在【%s (%s) 】应用下找到 %s", appName, mServiceName, className));
            }
        } else {
            getView().showMessage(String.format("没有在【%s】应用下找到 %s", appName, className));
        }
    }

    /**
     * 搜索类
     */
    private void searchClass() {
        String className = (String) getModel().getValue("lzp_class_name");
        DynamicObject executeApp = (DynamicObject) getModel().getValue("lzp_execute_app");
        String appId = executeApp.getString("number");
        String appName = executeApp.getString("name");
        DynamicObject decompiler = (DynamicObject) getModel().getValue("lzp_decompiler");
        String decompilerClassName = decompiler.getString("lzp_decompiler_class");
        Map<String, Object> classInfoMap = DispatchServiceHelper.invokeService(ServiceFactory.class.getPackage().getName(), appId, ClassService.class.getSimpleName(), "getClassInfo", new Object[]{className, decompilerClassName});
        if (classInfoMap != null) {
            CodeEdit codeEdit = getControl("lzp_code_template_str");
            String sourceCode = (String) classInfoMap.get("sourceCode");
            Object mServiceName = classInfoMap.get("APP_NAME");
            if (sourceCode != null) {
                codeEdit.setText(sourceCode);
                getModel().setValue("lzp_execute_name", mServiceName);
                getModel().setValue("lzp_class_path", classInfoMap.get("locationUrl"));
                getModel().setValue("lzp_class_loader_tree", classInfoMap.get("classLoaderTree"));
            } else {
                codeEdit.setText("");
                getModel().setValue("lzp_execute_name", null);
                getModel().setValue("lzp_class_path", null);
                getModel().setValue("lzp_class_loader_tree", null);
                getView().showMessage(String.format("没有在【%s (%s) 】应用下找到 %s", appName, mServiceName, className));
            }
        } else {
            getView().showMessage(String.format("没有在【%s】应用下找到 %s", appName, className));
        }
    }
}
