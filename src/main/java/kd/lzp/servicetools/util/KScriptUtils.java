package kd.lzp.servicetools.util;

import kd.bos.formula.platform.api.BaseFormulaFunctions;
import kd.bos.kscript.ParserException;
import kd.bos.kscript.runtime.Interpreter;
import kd.bos.kscript.runtime.InterpreterException;

import java.util.Map;

/**
 * 动态执行 KScript 脚本
 *
 * @author lzpeng
 */
public class KScriptUtils {


    /**
     * 动态执行 KScript 脚本
     * {@link kd.bos.formula.platform.api.BaseFormulaFunctions#main(java.lang.String[])}
     *
     * @param script 脚本内容
     * @param map    需要向脚本中注入的java对象
     * @return 脚本执行结果
     */
    public static Object execute(String script, Map<String, Object> map) throws ParserException, InterpreterException {
        return Holder.INSTANCE.interpreter.eval(script, map);
    }

    /**
     * 枚举单例模式
     */
    private enum Holder {
        INSTANCE;

        private final Interpreter interpreter;

        Holder() {
            this.interpreter = new Interpreter();
            // 基本函数
            this.interpreter.addFunctionProvider(new BaseFormulaFunctions());
        }
    }

}
