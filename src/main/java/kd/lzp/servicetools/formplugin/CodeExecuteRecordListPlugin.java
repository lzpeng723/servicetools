package kd.lzp.servicetools.formplugin;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.form.ShowType;
import kd.bos.form.events.BillListHyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickArgs;
import kd.bos.list.BillList;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;


/**
 * 代码执行记录列表插件
 *
 * @author lzpeng
 * @version 1.0
 * @description 代码执行记录列表插件
 * @since 2021-05-02 21:36
 */
public class CodeExecuteRecordListPlugin extends AbstractListPlugin {

    /**
     * 超链接点击事件
     *
     * @param args 事件参数
     */
    @Override
    public void billListHyperLinkClick(HyperLinkClickArgs args) {
        super.billListHyperLinkClick(args);
        String fieldName = args.getFieldName();
        if ("lzp_sch_task".equals(fieldName)) {
            String entityId = ((BillList) getControl("billlistap")).getEntityId();
            ListSelectedRow currentRow = ((BillListHyperLinkClickEvent) args.getHyperLinkClickEvent()).getCurrentRow();
            Object primaryKeyValue = currentRow.getPrimaryKeyValue();
            DynamicObject rowData = QueryServiceHelper.queryOne(entityId, "lzp_sch_task", new QFilter[]{new QFilter("id", "=", primaryKeyValue)});
            args.setCancel(true);
            BillShowParameter showParameter = new BillShowParameter();
            showParameter.setFormId("sch_task");
            showParameter.setPkId(rowData.get("lzp_sch_task"));
            showParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
            showParameter.setStatus(OperationStatus.VIEW);
            getView().showForm(showParameter);
        }
    }

}
