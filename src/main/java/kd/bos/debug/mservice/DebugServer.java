package kd.bos.debug.mservice;

import kd.bos.config.client.util.ConfigUtils;
import kd.bos.service.webserver.JettyServer;

/**
 *
 */
public class DebugServer {

    public static void main(String[] args) throws Exception {
        // 应用名字 可通过kd.bos.instance.Instance.getAppName获取
        System.setProperty(ConfigUtils.APP_NAME_KEY, "mservice-biz1.5-cosmic");
        //设置集群环境名称和配置服务器地址
        System.setProperty(ConfigUtils.CLUSTER_NAME_KEY, "cosmic");
        // zookeeper 注册地址
        System.setProperty(ConfigUtils.CONFIG_URL_KEY, "127.0.0.1:2181");
        System.setProperty("configAppName", "mservice,web");
        System.setProperty("webmserviceinone", "true");
        System.setProperty("file.encoding", "utf-8");
        System.setProperty("xdb.enable", "false");
        System.setProperty("MONITOR_HTTP_PORT", "9998");
        System.setProperty("JMX_HTTP_PORT", "9091");
        System.setProperty("dubbo.protocol.port", "28888");
        System.setProperty("dubbo.consumer.url", "dubbo://localhost:28888");
        System.setProperty("dubbo.consumer.url.qing", "dubbo://localhost:30880");
        //是否注册为dubbo服务
        System.setProperty("dubbo.registry.register", "false");
        //是否消费mq消息,本地开发设为true,连接线上环境设为false
        System.setProperty("mq.consumer.register", "true");
        // 默认情况下，消息会发送给任意一个消费者节点，在开发调试阶段无法保证自己发送的message被自己java进程消费到，若需要调试MQ，需要设置mq.debug.queue.tag为唯一值
        // 消息队列标签, 调试工作流，后台事务等依赖MQ的功能需要
        System.setProperty("mq.debug.queue.tag", "lzpeng723");
        System.setProperty("dubbo.service.lookup.local", "false");
        // 是否分应用 可通过kd.bos.instance.Instance.isAppSplit获取
        System.setProperty("appSplit", "false");
        // 是否轻量级环境
        System.setProperty("lightweightdeploy", "true");
        // 是否输出sql语句
        System.setProperty("db.sql.out", "false");
        //DB.setSqlOut(true);
        //DB.setSqlLogger(System.out::printf);
        // 开放服务端口
        System.setProperty("JETTY_WEB_PORT", "8080");
        // webapp主目录
        System.setProperty("JETTY_WEBAPP_PATH", "../../../mservice-cosmic/webapp");
        // 静态资源目录
        System.setProperty("JETTY_WEBRES_PATH", "../../../static-file-service");
        // contextUrl
        System.setProperty("domain.contextUrl", "http://127.0.0.1:8080/ierp");
        // 租户编码
        System.setProperty("domain.tenantCode", "cosmic-simple");
        System.setProperty("tenant.code.type", "config");
        // 文件服务器地址
        System.setProperty("fileserver", "http://127.0.0.1:8100/fileserver/");
        // 图片服务器地址
        System.setProperty("imageServer.url", "http://127.0.0.1:8100/fileserver/");
        System.setProperty("bos.app.special.deployalone.ids", "");
        //mc地址
        System.setProperty("mc.server.url", "http://127.0.0.1:8090/");
        // https://club.kdcloud.com/article/211414539328462592
        System.setProperty("trace.enable", "false");
        System.setProperty("trace.reporter.type", "");
        JettyServer.main(null);
    }

}