package kd.lzp.servicetools.util;

import cn.hutool.core.util.ZipUtil;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.TempFileCache;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.cache.AppCache;
import kd.bos.entity.cache.IAppCache;
import kd.bos.fileservice.BatchDownloadRequest;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.id.ID;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.AttachmentServiceHelper;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.MetadataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.url.UrlService;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 附件工具
 *
 * @author lzpeng
 */
public class AttachmentUtils {


    /**
     * 附件详情单据标识
     */
    private static final String BOS_ATTACHMENT_ENTITY = "bos_attachment";

    /**
     * 临时附件URL
     */
    private static final String TEMP_ATTACHMENT_URL = "TEMP_ATTACHMENT_URL";

    /**
     * 应用缓存
     */
    private static final IAppCache appCache = AppCache.get(AttachmentUtils.class.getName());

    /**
     * 复制附件
     *
     * @param srcBill             源单据
     * @param srcAttachmentPanel  源单据附件面板
     * @param destBill            目标单据
     * @param destAttachmentPanel 目标单据附件面板
     */
    public static void copy(DynamicObject srcBill, String srcAttachmentPanel, DynamicObject destBill,
                            String destAttachmentPanel) {
        String srcEntityName = srcBill.getDynamicObjectType().getName();
        String destEntityName = destBill.getDynamicObjectType().getName();
        Object srcBillId = srcBill.getPkValue();
        Object destBillId = destBill.getPkValue();
        List<Map<String, Object>> srcAttachments = AttachmentServiceHelper.getAttachments(srcEntityName, srcBillId,
                srcAttachmentPanel);
        List<Map<String, Object>> destAttachments = AttachmentServiceHelper.getAttachments(destEntityName, destBillId,
                destAttachmentPanel);
        List<Map<String, Object>> srcAttachmentDatas = decodeAttachmentUrls(srcAttachments);
        List<Map<String, Object>> destAttachmentDatas = decodeAttachmentUrls(destAttachments);
        if (!destAttachmentDatas.isEmpty()) {
            srcAttachmentDatas.removeIf(srcAttachmentData -> {
                for (Map<String, Object> destAttachmentData : destAttachmentDatas) {
                    if (destAttachmentData.get("url").equals(srcAttachmentData.get("url"))) {
                        return true;
                    }
                }
                return false;
            });
        }
        for (Map<String, Object> attachmentData : srcAttachmentDatas) {
            String fileName = String.valueOf(attachmentData.get("name"));
            String attachmentUrl = String.valueOf(attachmentData.get("url"));
            Integer byteSize = Integer.parseInt(String.valueOf(attachmentData.get("size")));
            DynamicObject destAttachmentData = createAttachmentData(fileName, attachmentUrl, byteSize, destBill, destAttachmentPanel);
            SaveServiceHelper.save(new DynamicObject[]{destAttachmentData});
        }

    }

    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param is       文件流
     * @return 文件url
     */
    public static String upload(String fileName, InputStream is) {
        return upload(fileName, is, null);
    }

    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param path     文件路径
     * @param is       文件流
     * @return 文件url
     */
    public static String upload(String fileName, InputStream is, String path) {
        if (path == null) {
            path = getAttachmentFilePath() + "/" + fileName;
        } else if (!path.endsWith(fileName)) {
            path = path + "/" + fileName;
        }
        FileItem fileItem = new FileItem(fileName, path, is);
        fileItem.setCreateNewFileWhenExists(true);
        FileService attachmentFileService = FileServiceFactory.getAttachmentFileService();
        String attachmentUrl = attachmentFileService.upload(fileItem);
        return attachmentUrl;
    }

    /**
     * 上传附件 通过字节数组
     *
     * @param fileName        附件名称
     * @param bytes           文件字节数组
     * @param bill            单据
     * @param attachmentPanel 附件面板
     */
    public static void upload(String fileName, byte[] bytes, DynamicObject bill, String attachmentPanel) {
        DynamicObject attachmentData = createAttachmentData(fileName, bytes, bill, attachmentPanel);
        SaveServiceHelper.save(new DynamicObject[]{attachmentData});
    }

    /**
     * 上传附件 通过字文件流
     *
     * @param fileName        附件名称
     * @param is              文件流
     * @param bill            单据
     * @param attachmentPanel 附件面板
     */
    public static void upload(String fileName, InputStream is, DynamicObject bill, String attachmentPanel) {
        DynamicObject attachmentData = createAttachmentData(fileName, is, bill, attachmentPanel);
        SaveServiceHelper.save(new DynamicObject[]{attachmentData});
    }

    /**
     * 构建待上传的附件 通过字节数组
     *
     * @param fileName        附件名称
     * @param bytes           文件字节数组
     * @param bill            单据
     * @param attachmentPanel 附件面板
     * @return 附件对象
     */
    public static DynamicObject createAttachmentData(String fileName, byte[] bytes, DynamicObject bill,
                                                     String attachmentPanel) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        return createAttachmentData(fileName, is, bill, attachmentPanel);
    }

    /**
     * 构建待上传的附件 通过字文件流
     *
     * @param fileName        附件名称
     * @param is              文件流
     * @param bill            单据
     * @param attachmentPanel 附件面板
     * @return 附件对象
     */
    public static DynamicObject createAttachmentData(String fileName, InputStream is, DynamicObject bill,
                                                     String attachmentPanel) {
        int count = 0;
        try {
            count = is.available();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String path = getAttachmentFilePath(bill);
        String attachmentUrl = upload(fileName, is, path);
        return createAttachmentData(fileName, attachmentUrl, count, bill, attachmentPanel);
    }

    /**
     * 构建待上传的附件 通过文件url
     *
     * @param fileName        文件名
     * @param attachmentUrl   文件url
     * @param sizeInByte      文件字节数
     * @param bill            单据
     * @param attachmentPanel 附件面板
     * @return
     */
    private static DynamicObject createAttachmentData(String fileName, String attachmentUrl, int sizeInByte, DynamicObject bill,
                                                      String attachmentPanel) {
        DynamicObject attachment = BusinessDataServiceHelper.newDynamicObject(BOS_ATTACHMENT_ENTITY);
        // attachment.set("id", Long.valueOf(orm.genLongId(bos_attachment)));
        attachment.set("fnumber", ID.genStringId());// 编码
        attachment.set("fbilltype", bill.getDataEntityType().getName());// 单据类型
        attachment.set("fbillno", bill.getString("number"));// 单据编号
        attachment.set("finterid", String.valueOf(bill.getPkValue()));// 单据内码
        Date today = new Date();
        attachment.set("fcreatetime", today);// 创建时间
        attachment.set("fmodifytime", today);// 修改时间
        attachment.set("faudittime", today);// 审核时间
        attachment.set("faliasfilename", fileName);// 别名
        attachment.set("fattachmentname", fileName);// 文件名
        attachment.set("fextname", fileName.substring(fileName.lastIndexOf(".") + 1));// 文件类型
        attachment.set("fattachmentsize", sizeInByte);// 大小 kb
        attachment.set("ffileid", attachmentUrl);// 文件 url
        Object creator = bill.get("creator");
        attachment.set("fcreatemen", creator);// 创建人
        attachment.set("fmodifymen", creator);// 修改人
        attachment.set("fauditmen", creator);// 审核人
        attachment.set("fattachmentpanel", attachmentPanel);// 附件面板key
        return attachment;
    }


    /**
     * 打包下载附件
     *
     * @param fileName        文件名
     * @param pkValues        单据内码数组
     * @param entityName      单据标识
     * @param attachmentPanel 附件面板
     * @return
     */
    public static String download(String fileName, Object[] pkValues, String entityName, String attachmentPanel) {
        List<QFilter> qFilterList = new ArrayList<>();
        if (pkValues != null) {
            qFilterList.add(new QFilter("finterid", QCP.in, pkValues));
        }
        if (entityName != null) {
            qFilterList.add(new QFilter("fbilltype", QCP.equals, entityName));
        }
        if (pkValues != null) {
            qFilterList.add(new QFilter("fattachmentpanel", QCP.equals, attachmentPanel));
        }
        DynamicObjectCollection fileUrlCollection = QueryServiceHelper.query(BOS_ATTACHMENT_ENTITY, "ffileid", qFilterList.toArray(new QFilter[0]));
        String[] fileUrls = fileUrlCollection.stream().map(o -> o.getString("ffileid")).toArray(String[]::new);
        return downloadByAttachmentUrls(fileName, fileUrls);
    }

    /**
     * 打包下载附件
     *
     * @param fileName      文件名
     * @param attachmentIds 附件id数组
     * @return 打包下载的URL地址
     */
    public static String downloadByAttachmentIds(String fileName, String... attachmentIds) {
        QFilter qFilter = new QFilter("id", QCP.in, attachmentIds);
        DynamicObject[] attachments = BusinessDataServiceHelper.load("bd_attachment", "id,url",
                new QFilter[]{qFilter});
        String[] filePaths = Arrays.stream(attachments).map(attachment -> attachment.getString("url"))
                .toArray(String[]::new);
        return downloadByAttachmentUrls(fileName, filePaths);
    }

    /**
     * 打包下载附件 linux 创建中文文件名会报错 Malformed input or input contains unmappable
     * characters, 所以用字节流 大文件如何处理呢,字节数组放不下
     *
     * @param fileName       文件名
     * @param attachmentUrls 附件路径数组
     * @return 打包下载的URL地址
     */
    public static String downloadByAttachmentUrls(String fileName, String... attachmentUrls) {
        if (attachmentUrls == null || attachmentUrls.length == 0) {
            return null;
        }
        if (attachmentUrls.length > 1) {
            fileName = fileName == null ? "attachments.zip"
                    : !fileName.toLowerCase().endsWith(".zip") ? fileName + ".zip" : fileName;
        } else if (fileName == null) {
            String attachmentUrl = attachmentUrls[0];
            fileName = attachmentUrl.substring(attachmentUrl.lastIndexOf("/") + 1);
        }
        FileService fs = FileServiceFactory.getAttachmentFileService();
        // 模拟浏览器userAgent访问
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (attachmentUrls.length > 1) {
                // 构造BatchDownloadRequest对象
                BatchDownloadRequest bdr = getBatchDownloadRequest(attachmentUrls);
                fs.batchDownload(bdr, out, userAgent);
            } else {
                String attachmentUrl = attachmentUrls[0];
                fs.download(attachmentUrl, out, userAgent);
            }
            out.flush();
            return download(fileName, out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ExceptionUtils.getDetailMessage(e);
        }
    }

    /**
     * 下载文件
     *
     * @param fileName 要下载的文件名
     * @param in       输入流
     * @return 文件下载的URL地址
     */
    public static String download(String fileName, InputStream in) {
        try {
            int available = in.available();
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            if (available * 2L < maxMemory - totalMemory + freeMemory) {
                // 如果流的大小小于剩余内存的一半
                TempFileCache cache = CacheFactory.getCommonCacheFactory().getTempFileCache();
                return cache.saveAsUrl(fileName, in, 2 * 60);
            } else {
                String attachmentUrl = appCache.get(TEMP_ATTACHMENT_URL, String.class);
                if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
                    FileService attachmentFileService = FileServiceFactory.getAttachmentFileService();
                    attachmentFileService.delete(attachmentUrl);
                }
                attachmentUrl = upload(fileName, in);
                appCache.put(TEMP_ATTACHMENT_URL, attachmentUrl);
                attachmentUrl = UrlService.getAttachmentFullUrl(attachmentUrl);
                return attachmentUrl;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载文件
     *
     * @param fileName 要下载的文件名
     * @param file     文件
     * @return 文件下载的URL地址
     */
    public static String download(String fileName, File file) {
        if (!file.exists()) {
            return null;
        }
        if (fileName == null) {
            fileName = file.getName();
        }
        if (file.isDirectory()) {
            file = ZipUtil.zip(file);
            fileName = fileName.endsWith(".zip") ? fileName : fileName + ".zip";
        }
        try {
            InputStream in = new FileInputStream(file);
            return download(fileName, in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载文件
     *
     * @param fileName 要下载的文件名
     * @param filePath 文件路径
     * @return 文件下载的URL地址
     */
    public static String downloadByFilePath(String fileName, String filePath) {
        return download(fileName, new File(filePath));
    }

    /**
     * 通过url下载文件
     *
     * @param fileName 要下载的文件流
     * @param url      url 源文件url
     * @return 文件下载的URL地址
     */
    public static String download(String fileName, URL url) {
        try {
            if ("file".equals(url.getProtocol())) {
                String filePath = URLDecoder.decode(url.getPath(), Charset.defaultCharset().name());
                if (System.getProperty("os.name").startsWith("Windows") && filePath.startsWith("/")) {
                    filePath = filePath.substring(1);
                }
                if (new File(filePath).isDirectory()) {
                    return downloadByFilePath(fileName, filePath);
                }
            }
            if (fileName == null) {
                fileName = url.getPath();
                while (fileName.endsWith("/")) {
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }
            return download(fileName, url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据字节数组下载文件
     *
     * @param fileName 要下载的文件名
     * @param bytes    字节数组
     * @return 文件下载的URL地址
     */
    public static String download(String fileName, byte[] bytes) {
        InputStream in = new ByteArrayInputStream(bytes);
        return download(fileName, in);
    }

    /**
     * 构建批量下载请求
     *
     * @param attachmentUrls 附件url
     * @return 批量下载请求
     */
    @NotNull
    private static BatchDownloadRequest getBatchDownloadRequest(String[] attachmentUrls) {
        BatchDownloadRequest.Dir srcDir = new BatchDownloadRequest.Dir("attachments");
        List<BatchDownloadRequest.File> srcFiles = new ArrayList<>();
        for (String attachmentUrl : attachmentUrls) {
            String attachmentName = attachmentUrl.substring(attachmentUrl.lastIndexOf("/") + 1);
            srcFiles.add(new BatchDownloadRequest.File(attachmentName, attachmentUrl));
        }
        srcDir.setFiles(srcFiles.toArray(new BatchDownloadRequest.File[0]));
        BatchDownloadRequest bdr = new BatchDownloadRequest("test-batch-download");
        bdr.setDirs(new BatchDownloadRequest.Dir[]{srcDir});
        return bdr;
    }

    /**
     * 获取附件路径
     *
     * @param bill
     * @return
     */
    public static String getAttachmentFilePath(DynamicObject bill) {
        RequestContext ctx = RequestContext.get();
        String tenantId = ctx.getTenantId();
        String accountId = ctx.getAccountId();
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String appId = "unknowappid";
        DynamicObjectType type = bill.getDynamicObjectType();
        if (type instanceof MainEntityType) {
            MainEntityType mainEntityType = (MainEntityType) type;
            appId = mainEntityType.getAppId();
        }
        String entityName = type.getName();
        return String.join("/", tenantId, accountId, date, appId, entityName, String.valueOf(bill.getPkValue()),
                "attachments");
    }

    /**
     * 获取附件路径
     *
     * @param entityNumber
     * @return
     */
    public static String getAttachmentFilePath(String entityNumber) {
        MainEntityType type = MetadataServiceHelper.getDataEntityType(entityNumber);
        return getAttachmentFilePath(type);
    }

    /**
     * 获取附件路径
     *
     * @param type
     * @return
     */
    public static String getAttachmentFilePath(MainEntityType type) {
        RequestContext ctx = RequestContext.get();
        String tenantId = ctx.getTenantId();
        String accountId = ctx.getAccountId();
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String appId = type.getAppId();
        String entityName = type.getName();
        return String.join("/", tenantId, accountId, date, appId, entityName, "attachments");
    }

    /**
     * 获取附件路径
     *
     * @param type
     * @return
     */
    @SuppressWarnings("unused")
    public static String getAttachmentFilePath(DynamicObjectType type) {
        RequestContext ctx = RequestContext.get();
        String tenantId = ctx.getTenantId();
        String accountId = ctx.getAccountId();
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String appId = "unknowappid";
        if (type instanceof MainEntityType) {
            MainEntityType mainEntityType = (MainEntityType) type;
            appId = mainEntityType.getAppId();
        }
        String entityName = type.getName();
        return String.join("/", tenantId, accountId, date, appId, entityName, "0", "attachments");
    }

    /**
     * 获取附件路径
     *
     * @return
     */
    public static String getAttachmentFilePath() {
        RequestContext ctx = RequestContext.get();
        String tenantId = ctx.getTenantId();
        String accountId = ctx.getAccountId();
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return String.join("/", tenantId, accountId, date, "attachments");
    }

    /**
     * @param attachmentDatas
     * @return
     */
    private static List<Map<String, Object>> decodeAttachmentUrls(List<Map<String, Object>> attachmentDatas) {
        if (!attachmentDatas.isEmpty()) {
            for (Map<String, Object> attachmentData : attachmentDatas) {
                //AttachmentServiceHelper.remove(destEntityName, destBillId, destAttachment.get("uid"));
                String attachmentUrl = String.valueOf(attachmentData.get("url"));
                attachmentUrl = decodeAttachmentUrl(attachmentUrl);
                attachmentData.put("url", attachmentUrl);
            }
        }
        return attachmentDatas;
    }

    @NotNull
    public static String decodeAttachmentUrl(String attachmentUrl) {
        try {
            attachmentUrl = URLDecoder.decode(attachmentUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int beginIndex = 0, endIndex = attachmentUrl.length();
        if (attachmentUrl.contains("download.do?path=")) {
            beginIndex = attachmentUrl.indexOf("download.do?path=") + "download.do?path=".length() + 1;
        }
        if (attachmentUrl.contains("&kdedcba=")) {
            endIndex = attachmentUrl.indexOf("&kdedcba=");
        }
        attachmentUrl = attachmentUrl.substring(beginIndex, endIndex);
        return attachmentUrl;
    }
}
