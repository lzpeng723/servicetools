package kd.lzp.servicetools.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.form.control.CodeEdit;
import kd.bos.form.events.BeforeDoOperationEventArgs;

import java.util.EventObject;

/**
 * @author lzpeng
 * @version 1.0
 * @description 代码类型插件
 * @since 2021-05-02 10:36
 */
public class CodeTypeBillPlugin extends AbstractBillPlugIn {

    @Override
    public void afterLoadData(EventObject e) {
        super.afterLoadData(e);
        String code = (String) this.getModel().getValue("lzp_code_template_tag");
        CodeEdit codeEdit = this.getControl("lzp_code_template_str");
        codeEdit.setText(code);
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        CodeEdit codeEdit = this.getControl("lzp_code_template_str");
        String code = codeEdit.getText();
        this.getModel().setValue("lzp_code_template_tag", code);
    }

}
