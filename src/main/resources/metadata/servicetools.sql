-- ----------------------------
-- Records of tk_lzp_decompiler_r3
-- ----------------------------
DELETE FROM `tk_lzp_decompiler_r3` WHERE FID = 1149973707154784256;
DELETE FROM `tk_lzp_decompiler_r3` WHERE FID = 1149974310329253888;
INSERT INTO `tk_lzp_decompiler_r3` VALUES (1149973707154784256, '1');
INSERT INTO `tk_lzp_decompiler_r3` VALUES (1149974310329253888, '1');

-- ----------------------------
-- Records of tk_lzp_decompiler_l
-- ----------------------------
DELETE FROM `tk_lzp_decompiler_l` WHERE FID = 1149973707154784256;
DELETE FROM `tk_lzp_decompiler_l` WHERE FID = 1149974310329253888;
INSERT INTO `tk_lzp_decompiler_l` VALUES ('1L=UQ9TDOHCR', 1149973707154784256, 'zh_CN', 'Jd-core反编译器 (jd-gui内置使用)');
INSERT INTO `tk_lzp_decompiler_l` VALUES ('1L=UQ9TDOHCS', 1149973707154784256, 'zh_TW', 'Jd-core反編譯器 (jd-gui內置使用)');
INSERT INTO `tk_lzp_decompiler_l` VALUES ('1L=UORV7J5N3', 1149974310329253888, 'zh_CN', 'FernFlower反编译器 (Intellij IDEA 内置使用)');
INSERT INTO `tk_lzp_decompiler_l` VALUES ('1L=UORV7J5N4', 1149974310329253888, 'zh_TW', 'FernFlower反編譯器 (Intellij IDEA 內置使用)');

-- ----------------------------
-- Records of tk_lzp_decompiler
-- ----------------------------
DELETE FROM `tk_lzp_decompiler` WHERE FID = 1149973707154784256;
DELETE FROM `tk_lzp_decompiler` WHERE FID = 1149974310329253888;
INSERT INTO `tk_lzp_decompiler` VALUES (1149973707154784256, 'jd-core', 'C', 1147700688659153920, 886895861458535424, '1', '2021-05-06 15:51:38', '2021-05-07 08:43:40', 1149973707154784256, 'kd.lzp.servicetools.util.decompiler.jd.JdDecompiler');
INSERT INTO `tk_lzp_decompiler` VALUES (1149974310329253888, 'FernFlower', 'C', 1147700688659153920, 886895861458535424, '1', '2021-05-06 15:52:50', '2021-05-07 08:43:15', 1149974310329253888, 'kd.lzp.servicetools.util.decompiler.fernflower.FernflowerDecompiler');

-- ----------------------------
-- Records of tk_lzp_code_type_r3
-- ----------------------------
DELETE FROM `tk_lzp_code_type_r3` WHERE FID = 1147731884105531392;
DELETE FROM `tk_lzp_code_type_r3` WHERE FID = 1147739040921944064;
DELETE FROM `tk_lzp_code_type_r3` WHERE FID = 1147739441872241664;
INSERT INTO `tk_lzp_code_type_r3` VALUES (1147731884105531392, '1');
INSERT INTO `tk_lzp_code_type_r3` VALUES (1147739040921944064, '1');
INSERT INTO `tk_lzp_code_type_r3` VALUES (1147739441872241664, '1');

-- ----------------------------
-- Records of tk_lzp_code_type_l
-- ----------------------------
DELETE FROM `tk_lzp_code_type_l` WHERE FID = 1147731884105531392;
DELETE FROM `tk_lzp_code_type_l` WHERE FID = 1147739040921944064;
DELETE FROM `tk_lzp_code_type_l` WHERE FID = 1147739441872241664;
INSERT INTO `tk_lzp_code_type_l` VALUES ('1KZNRLRZX7E0', 1147731884105531392, 'zh_CN', 'Java源码');
INSERT INTO `tk_lzp_code_type_l` VALUES ('1KZNRLRZX7E1', 1147731884105531392, 'zh_TW', 'Java源碼');
INSERT INTO `tk_lzp_code_type_l` VALUES ('1KZP2R4+BY7H', 1147739040921944064, 'zh_CN', 'Rhino脚本');
INSERT INTO `tk_lzp_code_type_l` VALUES ('1KZP2R4+BY7I', 1147739040921944064, 'zh_TW', 'Rhino腳本');
INSERT INTO `tk_lzp_code_type_l` VALUES ('1KZP5O1U3LT/', 1147739441872241664, 'zh_CN', 'Nashorn脚本');
INSERT INTO `tk_lzp_code_type_l` VALUES ('1KZP5O1U3LT0', 1147739441872241664, 'zh_TW', 'Nashorn腳本');

-- ----------------------------
-- Records of tk_lzp_code_type
-- ----------------------------
DELETE FROM `tk_lzp_code_type` WHERE FID = 1147731884105531392;
DELETE FROM `tk_lzp_code_type` WHERE FID = 1147739040921944064;
DELETE FROM `tk_lzp_code_type` WHERE FID = 1147739441872241664;
INSERT INTO `tk_lzp_code_type` VALUES (1147731884105531392, 'Java', 'C', 1147700688659153920, 1147700688659153920, '1', '2021-05-03 13:34:33', '2021-05-03 13:38:30', 1147731884105531392, ' ', 'import kd.bos.context.RequestContext;\nimport kd.bos.exception.KDException;\nimport kd.bos.instance.Instance;\nimport kd.lzp.servicetools.task.AbstractCodeExecuteTask;\n\nimport java.util.Map;\nimport java.util.concurrent.TimeUnit;\n\n/**\n * 请先选择执行应用和代码类型，然后输入需要运行的代码,然后点击执行\n * 如选择人力资源云->假勤管理应用,将会在mservice-hr上执行代码\n * 通过returnDataToView将数据返回页面,会通过当前表单的showMessage展示出来\n * 通过returnDataToView将异常抛到页面,会通过当前表单的showErrMessage展示出来\n */\npublic final class DynamicTask extends AbstractCodeExecuteTask {\n\n    @Override\n    public void execute(RequestContext ctx, Map<String, Object> map) throws KDException {\n        try {\n            for (int i = 0; i < 10; i++) {\n                feedbackProgress(i * 10, \"已执行 \" + (i * 10) + \"% 。\");\n                TimeUnit.SECONDS.sleep(1);\n            }\n            // 将数据返回页面\n            returnDataToView(ctx.getUserName() + \"@\" + Instance.getAppName());\n        } catch (Exception e) {\n            // 将异常抛到页面\n            returnExceptionToView(e);\n        }\n    }\n\n    // TODO 添加新的方法\n\n}\n', 'kd.lzp.servicetools.task.JavaCodeExecuteTask');
INSERT INTO `tk_lzp_code_type` VALUES (1147739040921944064, 'Rhino', 'C', 1147700688659153920, 1147700688659153920, '1', '2021-05-03 13:51:29', '2021-05-03 13:52:40', 1147739040921944064, ' ', '/**\n * 请先选择执行应用和代码类型，然后输入需要运行的代码,然后点击执行\n * 如选择人力资源云->假勤管理应用,将会在mservice-hr上执行代码\n * 通过returnDataToView将数据返回页面,会通过当前表单的showMessage展示出来\n * 通过returnDataToView将异常抛到页面,会通过当前表单的showErrMessage展示出来\n */\nvar imp = JavaImporter(\n    Packages.kd.bos.context,\n    Packages.kd.bos.instance,\n    Packages.java.util.concurrent\n);\nwith (imp) {\n    try {\n        for (var i = 0; i < 10; i++) {\n            task.feedbackProgress(i * 10, \"已执行 \" + (i * 10) + \"% 。\");\n            TimeUnit.SECONDS.sleep(1);\n        }\n        // 将数据返回页面\n        task.returnDataToView(ctx.getUserName() + \"@\" + Instance.getAppName());\n    } catch (e) {\n        // 将异常抛到页面\n        task.returnExceptionToView(e);\n    }\n}', 'kd.lzp.servicetools.task.RhinoCodeExecuteTask');
INSERT INTO `tk_lzp_code_type` VALUES (1147739441872241664, 'Nashorn', 'C', 1147700688659153920, 1147700688659153920, '1', '2021-05-03 13:52:51', '2021-05-04 18:50:18', 1147739441872241664, ' ', '/**\n * 请先选择执行应用和代码类型，然后输入需要运行的代码,然后点击执行\n * 如选择人力资源云->假勤管理应用,将会在mservice-hr上执行代码\n * 通过returnDataToView将数据返回页面,会通过当前表单的showMessage展示出来\n * 通过returnDataToView将异常抛到页面,会通过当前表单的showErrMessage展示出来\n */\nvar imp = JavaImporter(\n    Packages.kd.bos.context,\n    Packages.kd.bos.instance,\n    Packages.java.util.concurrent\n);\nwith (imp) {\n    try {\n        for (var i = 0; i < 10; i++) {\n            task.feedbackProgress(i * 10, \"已执行 \" + (i * 10) + \"% 。\");\n            TimeUnit.SECONDS.sleep(1);\n        }\n        // 将数据返回页面\n        task.returnDataToView(ctx.getUserName() + \"@\" + Instance.getAppName());\n    } catch (e) {\n        // 将异常抛到页面\n        task.returnExceptionToView(e);\n    }\n}', 'kd.lzp.servicetools.task.NashornCodeExecuteTask');
