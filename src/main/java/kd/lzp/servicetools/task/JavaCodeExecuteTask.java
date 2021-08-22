package kd.lzp.servicetools.task;

import cn.hutool.core.compiler.CompilerUtil;
import kd.bos.context.RequestContext;
import kd.bos.exception.ErrorCode;
import kd.bos.exception.KDException;

import java.util.Map;

/**
 * @author lzpeng
 * @version 1.0
 * @description Java代码执行器任务
 * @since 2021-05-02 11:28
 */
public class JavaCodeExecuteTask extends AbstractCodeExecuteTask {

    @Override
    public void execute(RequestContext reqCtx, Map<String, Object> map) throws KDException {
        String codeName = (String) map.get("codeName");
        Map<String, Object> result = getReturnMap();
        try {
            feedbackCustomdata(result);
            feedbackProgress(5, "开始编译" + codeName, result);
            String code = (String) map.get("code");
            ClassLoader classLoader = CompilerUtil.getCompiler(null)
                    .addSource("DynamicTask", code)
                    .compile();
            feedbackProgress(5, "开始执行" + codeName, result);
            Class<?> clazz = classLoader.loadClass("DynamicTask");
            AbstractCodeExecuteTask task = (AbstractCodeExecuteTask) clazz.newInstance();
            task.setTaskId(this.taskId);
            task.setMessageHandle(this.getMessageHandler());
            task.setReturnMap(result);
            task.execute(reqCtx, map);
            feedbackProgress(99, codeName + "执行完毕，开始输出结果", result);
            // 任务执行完毕，生成执行结果输出
            result.putIfAbsent(MSG_TYPE, "success");
            result.putIfAbsent(RETURN_VALUE, codeName + "源码执行成功");
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
