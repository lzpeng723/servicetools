package kd.lzp.servicetools.util;

import kd.bos.entity.operate.result.OperationResult;
import kd.bos.util.StringUtils;

/**
 * 操作异常工具类
 *
 * @author lzpeng
 * @since 2020-12-11 15:33
 */
public final class OperationResultUtils {

    /**
     * 获得详细错误信息
     *
     * @param operationResult 操作结果
     * @return 详细错误信息
     */
    public static String getErrorMsg(OperationResult operationResult) {
        StringBuilder result = new StringBuilder();
        if (operationResult != null && !operationResult.isSuccess()) {
            if (StringUtils.isNotEmpty(operationResult.getMessage())) {
                result.append(operationResult.getMessage()).append(System.lineSeparator());
            }
            operationResult.getAllErrorOrValidateInfo().forEach((vr) -> {
                result.append(vr.getTitle()).append(" ").append(vr.getMessage()).append(System.lineSeparator());
            });
        }
        return result.toString();
    }
}
