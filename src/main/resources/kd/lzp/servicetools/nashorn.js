/**
 * 请先选择执行应用和代码类型，然后输入需要运行的代码,然后点击执行
 * 如选择人力资源云->假勤管理应用,将会在mservice-hr上执行代码
 * 通过returnDataToView将数据返回页面,会通过当前表单的showMessage展示出来
 * 通过returnDataToView将异常抛到页面,会通过当前表单的showErrMessage展示出来
 */
var imp = JavaImporter(
    Packages.kd.bos.context,
    Packages.kd.bos.instance,
    Packages.java.util.concurrent
);
with (imp) {
    try {
        for (var i = 0; i < 10; i++) {
            task.feedbackProgress(i * 10, "已执行 " + (i * 10) + "% 。");
            TimeUnit.SECONDS.sleep(1);
        }
        // 将数据返回页面
        task.returnDataToView(ctx.getUserName() + "@" + Instance.getAppName());
    } catch (e) {
        // 将异常抛到页面
        task.returnExceptionToView(e);
    }
}