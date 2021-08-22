package kd.lzp.servicetools.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.form.control.CodeEdit;

import java.util.EventObject;


/**
 * 代码执行记录插件
 *
 * @author lzpeng
 * @version 1.0
 * @description 代码执行列表插件
 * @since 2021-05-02 21:36
 */
public class CodeExecuteRecordBillPlugin extends AbstractBillPlugIn {

    /**
     * 界面加载数据之后
     * 将变量放到富文本框中
     *
     * @param e 事件对象
     */
    @Override
    public void afterLoadData(EventObject e) {
        super.afterLoadData(e);
        String code = (String) getModel().getValue("lzp_code_tag");
        CodeEdit codeEdit = getControl("lzp_code_template_str");
        codeEdit.setText(code);
    }

}

