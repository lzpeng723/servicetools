-- ----------------------------
-- Records of t_perm_permitem
-- ----------------------------
DELETE FROM `t_perm_permitem` WHERE FID IN ('1LIUHR+1NM+W', '1LIUJ69Z8POO', '1LIUL3/1Y5TR', '1LIUFP04Q/YF');
INSERT INTO `t_perm_permitem`(`FID`, `FNUMBER`, `FINHERITMODE`, `FGROUP`, `FPREPERMITEMID`, `FORDER`, `FDESCRIPTION`, `FBIZDOMAINID`, `FBIZAPPID`, `FNAME`) VALUES ('1LIUHR+1NM+W', 'lzp_download_class', '10', '20', '47150e89000000ac', 0, ' ', 0, '1KVQA4U6JCFF', '下载class文件');
INSERT INTO `t_perm_permitem`(`FID`, `FNUMBER`, `FINHERITMODE`, `FGROUP`, `FPREPERMITEMID`, `FORDER`, `FDESCRIPTION`, `FBIZDOMAINID`, `FBIZAPPID`, `FNAME`) VALUES ('1LIUJ69Z8POO', 'lzp_download_jar', '10', '20', '47150e89000000ac', 0, ' ', 0, '1KVQA4U6JCFF', '下载jar包');
INSERT INTO `t_perm_permitem`(`FID`, `FNUMBER`, `FINHERITMODE`, `FGROUP`, `FPREPERMITEMID`, `FORDER`, `FDESCRIPTION`, `FBIZDOMAINID`, `FBIZAPPID`, `FNAME`) VALUES ('1LIUL3/1Y5TR', 'lzp_execute_code', '10', '20', '47150e89000000ac', 0, ' ', 0, '1KVQA4U6JCFF', '执行代码');
INSERT INTO `t_perm_permitem`(`FID`, `FNUMBER`, `FINHERITMODE`, `FGROUP`, `FPREPERMITEMID`, `FORDER`, `FDESCRIPTION`, `FBIZDOMAINID`, `FBIZAPPID`, `FNAME`) VALUES ('1LIUFP04Q/YF', 'lzp_search_class', '10', '20', '47150e89000000ac', 0, ' ', 0, '1KVQA4U6JCFF', '搜索类');


-- ----------------------------
-- Records of t_perm_permitem_l
-- ----------------------------
DELETE FROM `t_perm_permitem_l` WHERE FID IN ('1LIUHR+1NM+W', '1LIUJ69Z8POO', '1LIUL3/1Y5TR', '1LIUFP04Q/YF');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUHR+1NLAM', '1LIUHR+1NM+W', 'zh_CN', '下载class文件');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUHR+1NLAN', '1LIUHR+1NM+W', 'zh_TW', '下載class文件');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUJ69Z8P/E', '1LIUJ69Z8POO', 'zh_CN', '下载jar包');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUJ69Z8P/F', '1LIUJ69Z8POO', 'zh_TW', '下載jar包');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUL3/1Y6H/', '1LIUL3/1Y5TR', 'zh_CN', '执行代码');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUL3/1Y6H0', '1LIUL3/1Y5TR', 'zh_TW', '執行代碼');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUFP04Q/96', '1LIUFP04Q/YF', 'zh_CN', '搜索类');
INSERT INTO `t_perm_permitem_l`(`FPKID`, `FID`, `FLOCALEID`, `FNAME`) VALUES ('1LIUFP04Q/97', '1LIUFP04Q/YF', 'zh_TW', '搜索類');
