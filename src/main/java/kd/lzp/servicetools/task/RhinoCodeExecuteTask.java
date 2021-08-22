package kd.lzp.servicetools.task;

import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.engine.rhino.RhinoEngine;
import kd.bos.context.RequestContext;
import kd.bos.exception.ErrorCode;
import kd.bos.exception.KDException;

import java.util.Map;

/**
 * @author lzpeng
 * @version 1.0
 * @description Rhino脚本执行器任务
 * @since 2021-05-02 15:22
 */
public class RhinoCodeExecuteTask extends AbstractCodeExecuteTask {

    @Override
    public void execute(RequestContext reqCtx, Map<String, Object> map) throws KDException {
        String codeName = (String) map.get("codeName");
        Map<String, Object> result = getReturnMap();
        try {
            feedbackCustomdata(result);
            String code = (String) map.get("code");
            map.put("task", this);
            map.put("ctx", reqCtx);
            
            feedbackProgress(5, "开始执行" + codeName, result);
            ExpressionEngine engine = new RhinoEngine();
            Object returnValue = engine.eval(code, map);
            feedbackProgress(99, codeName + "执行完毕，开始输出结果", result);
            // 任务执行完毕，生成执行结果输出
            result.putIfAbsent(MSG_TYPE, "success");
            result.putIfAbsent(RETURN_VALUE, returnValue);
        } catch (KDException e) {
            returnExceptionToView(e);
            throw e;
        } catch (Exception e) {
            returnExceptionToView(e);
            throw new KDException(e, new ErrorCode("500001", "执行" + codeName + "异常"));
        } finally {
            // 输出定制结果
            feedbackCustomdata(result);
            saveCodeExecuteRecord(reqCtx, map);
        }

    }
}
