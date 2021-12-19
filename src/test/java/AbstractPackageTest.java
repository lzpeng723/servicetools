import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import kd.bos.web.actions.MetadataAction;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 抽象的打包工具
 *
 * @author lzpeng
 * @version 1.0
 * @date 2021-06-09 11:34
 */
public abstract class AbstractPackageTest {

    /**
     * 当前 classpath 根路径
     */
    private List<File> rootFiles;

    /**
     * 初始化变量
     */
    @Before
    public void setRootFilePaths() {
        URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
        List<URL> rootResources = ResourceUtil.getResources("");
        this.rootFiles = rootResources.stream()
                .filter(url -> !location.equals(url))
                .filter(url -> "file".equals(url.getProtocol()))
                .map(URL::getFile)
                .map(File::new)
                .map(File::getAbsoluteFile)
                .collect(Collectors.toList());
        String userDir = System.getProperty("user.dir");
        this.rootFiles.add(new File(userDir, "src/main/java"));
        this.rootFiles.add(new File(userDir));
    }

    /**
     * 获取 ClassPath 中去除苍穹框架包之后的第三方包路径集合
     *
     * @return jar 包路径集合
     */
    @NotNull
    protected List<String> getClassPathJarPath() {
        try {
            String classPathStr = System.getProperty("java.class.path");
            String[] classPaths = classPathStr.split(";");
            String trdPath = new File("../../../mservice-cosmic/lib/trd").getCanonicalPath();
            String bosPath = new File("../../../mservice-cosmic/lib/bos").getCanonicalPath();
            String bizPath = new File("../../../mservice-cosmic/lib/biz").getCanonicalPath();
            String cusPath = new File("../../../mservice-cosmic/lib/cus").getCanonicalPath();
            List<String> needPackageJarPath = new ArrayList<>();
            for (String classPath : classPaths) {
                if (new File(classPath).isFile() && !classPath.startsWith(trdPath) && !classPath.startsWith(bosPath) && !classPath.startsWith(bizPath) && !classPath.startsWith(cusPath)) {
                    needPackageJarPath.add(classPath);
                }
            }
            return needPackageJarPath;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 打成zip包
     *
     * @param zipFilePath 生成zip包的路径
     * @param fileFilter  文件过滤器
     * @param srcFiles    源文件路径
     */
    protected File genZip(String zipFilePath, FileFilter fileFilter, String... srcFiles) {
        if (fileFilter == null) {
            fileFilter = file -> true;
        }
        Map<String, InputStream> fileMap = Arrays.stream(srcFiles)
                .flatMap(this::getStreamByPath)
                .filter(File::exists)
                .map(FileUtil::loopFiles)
                .flatMap(Collection::stream)
                .filter(fileFilter::accept)
                .collect(Collectors.toMap(File::getName, this::getInputStream));
        File zipFile = new File(zipFilePath);
        String[] paths = fileMap.keySet().toArray(new String[0]);
        InputStream[] inputStreams = fileMap.values().toArray(new InputStream[0]);
        ZipUtil.zip(zipFile, paths, inputStreams);
        System.out.println("已生成zip包: " + zipFile.getAbsolutePath());
        return zipFile.getAbsoluteFile();
    }

    /**
     * 打成zip包
     *
     * @param zipFilePath 生成zip包的路径
     * @param fileFilter  文件过滤器
     * @param srcFiles    源文件路径
     */
    protected File genZip(String zipFilePath, FileFilter fileFilter, File... srcFiles) {
        if (fileFilter == null) {
            fileFilter = file -> true;
        }
        File zipFile = new File(zipFilePath);
        ZipUtil.zip(zipFile, Charset.defaultCharset(), false, fileFilter, srcFiles);
        System.out.println("已生成zip包: " + zipFile.getAbsolutePath());
        return zipFile.getAbsoluteFile();
    }


    /**
     * 获取 Manifest
     *
     * @return
     */
    protected Manifest genManifest() {
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(Attributes.Name.IMPLEMENTATION_TITLE, "lzpeng723");
        mainAttributes.put(Attributes.Name.IMPLEMENTATION_VENDOR, "https://github.com/lzpeng723");
        mainAttributes.put(Attributes.Name.IMPLEMENTATION_VERSION, "1.0");
        mainAttributes.put(Attributes.Name.SPECIFICATION_TITLE, "Kingdee");
        mainAttributes.put(Attributes.Name.SPECIFICATION_VENDOR, "https://dev.kingdee.com");
        mainAttributes.put(Attributes.Name.SPECIFICATION_VERSION, "4.0");
        mainAttributes.put(new Attributes.Name("Created-By"), "Development Tool");
        mainAttributes.put(new Attributes.Name("Release"), "1.0");
        mainAttributes.put(new Attributes.Name("Build-Jdk"), System.getProperty("java.version"));
        mainAttributes.put(new Attributes.Name("Builddate"), getTimeStr());
        return manifest;
    }

    /**
     * 打成jar包
     * 添加 META-INF/MANIFEST.MF
     *
     * @param jarFilePath 生成jar包的路径
     * @param fileFilter  文件过滤器
     * @param srcFiles    源文件路径
     * @see MetadataAction#getJarInfo(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected File genJar(String jarFilePath, FileFilter fileFilter, String... srcFiles) {
        return genJar(jarFilePath, null, fileFilter, srcFiles);
    }

    /**
     * 打成jar包
     *
     * @param jarFilePath 生成jar包的路径
     * @param manifest    META-INF/MANIFEST.MF 文件信息
     * @param fileFilter  文件过滤器
     * @param srcFiles    源文件路径
     * @see MetadataAction#getJarInfo(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected File genJar(String jarFilePath, Manifest manifest, FileFilter fileFilter, String... srcFiles) {
        if (fileFilter == null) {
            fileFilter = file -> true;
        }
        if (manifest == null) {
            manifest = genManifest();
        }
        Map<String, InputStream> fileMap = Arrays.stream(srcFiles)
                .flatMap(this::getStreamByPath)
                .filter(File::exists)
                .map(FileUtil::loopFiles)
                .flatMap(Collection::stream)
                .filter(fileFilter::accept)
                .collect(Collectors.toMap(this::getFilePathInJar, this::getInputStream));
        InputStream manifestInputStream = genManifestInputStream(manifest);
        if (manifestInputStream != null) {
            fileMap.put(JarFile.MANIFEST_NAME, manifestInputStream);
        }
        File jarFile = new File(jarFilePath);
        String[] paths = fileMap.keySet().toArray(new String[0]);
        InputStream[] inputStreams = fileMap.values().toArray(new InputStream[0]);
        ZipUtil.zip(jarFile, paths, inputStreams);
        System.out.println("已生成jar包: " + jarFile.getAbsolutePath());
        return jarFile.getAbsoluteFile();
    }

    /**
     * 获得 Manifest 输入流
     *
     * @param manifest Manifest 文件
     * @return
     */
    private InputStream genManifestInputStream(Manifest manifest) {
        if (manifest == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            manifest.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = baos.toByteArray();
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 文件以 ... 结束 过滤器
     *
     * @param suffixes 后缀
     * @return 文件过滤器
     */
    protected FileFilter endsWith(String... suffixes) {
        return file -> {
            String fileName = file.getAbsolutePath();
            for (String suffix : suffixes) {
                if (fileName.endsWith(suffix)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 文件不以 ... 结束 过滤器
     *
     * @param suffixes 后缀
     * @return 文件过滤器
     */
    protected FileFilter notEndsWith(String... suffixes) {
        return file -> {
            String fileName = file.getAbsolutePath();
            for (String suffix : suffixes) {
                if (fileName.endsWith(suffix)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * 文件以 ... 开始 过滤器
     *
     * @param prefixes 前缀
     * @return 文件过滤器
     */
    protected FileFilter startsWith(String... prefixes) {
        return file -> {
            String fileName = file.getAbsolutePath();
            for (String prefix : prefixes) {
                if (fileName.startsWith(prefix)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 文件不以 ... 开始 过滤器
     *
     * @param prefixes 前缀
     * @return 文件过滤器
     */
    protected FileFilter notStartsWith(String... prefixes) {
        return file -> {
            String fileName = file.getAbsolutePath();
            for (String prefix : prefixes) {
                if (fileName.startsWith(prefix)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * 文件包含 ... 过滤器
     *
     * @param contents 字符
     * @return 文件过滤器
     */
    protected FileFilter contains(String... contents) {
        return file -> {
            String fileName = file.getAbsolutePath();
            for (String content : contents) {
                if (fileName.contains(content)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 文件不包含 ... 过滤器
     *
     * @param contents 字符
     * @return 文件过滤器
     */
    protected FileFilter notContains(String... contents) {
        return file -> {
            String fileName = file.getAbsolutePath();
            for (String content : contents) {
                if (fileName.contains(content)) {
                    return false;
                }
            }
            return true;
        };
    }


    /**
     * 部署zip包
     *
     * @param host                主机名
     * @param port                sftp 端口号
     * @param username            用户名
     * @param password            密码
     * @param serverCusZipPath    cus 包路径
     * @param serverCusZipBakPath cus 包备份路径
     * @param files               待部署的文件
     */
    private void deployZip(String host, int port, String username, String password, String serverCusZipPath, String serverCusZipBakPath, File... files) {
        String timeStr = getTimeStr();
        Sftp sftp = JschUtil.createSftp(host, port, username, password);
        for (File file : files) {
            String fileName = file.getName();
            String bakFilePath = String.join(File.separator, "bak", host);
            File bakDir = new File(bakFilePath).getAbsoluteFile();
            if (!bakDir.exists()) {
                bakDir.mkdirs();
            }
            String serverFilePath = serverCusZipPath + "/" + fileName;
            if (sftp.exist(serverFilePath)) {
                File localBakFile = new File(bakDir, fileName);
                if (localBakFile.isDirectory()) {
                    FileUtil.del(localBakFile);
                }
                sftp.download(serverFilePath, bakDir);
                File newLocalBakFile = new File(bakDir, String.join(".", FileNameUtil.mainName(fileName), timeStr, FileNameUtil.extName(fileName)));
                localBakFile.renameTo(newLocalBakFile);
                System.out.println("本地备份: " + newLocalBakFile);
                if (!sftp.exist(serverCusZipBakPath)) {
                    sftp.mkDirs(serverCusZipBakPath);
                }
                sftp.upload(serverCusZipBakPath, newLocalBakFile);
                System.out.println("服务器备份: " + host + ":" + serverCusZipBakPath + "/" + newLocalBakFile.getName());
            }
            if (!sftp.exist(serverCusZipBakPath)) {
                sftp.mkDirs(serverCusZipBakPath);
            }
            sftp.upload(serverCusZipPath, file);
            System.out.println("已部署: " + host + ":" + serverCusZipPath + "/" + fileName);
        }
        String message = "zip包已部署, 请前往容器管理平台(http://" + host + ":8090)进行配置和重启";
        System.out.println(message);
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    @NotNull
    private String getTimeStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * 获得文件流
     *
     * @param path 文件路径
     * @return
     */
    private Stream<File> getStreamByPath(String path) {
        File file = new File(path);
        if (file.exists() && file.isAbsolute()) {
            return Stream.of(file);
        }
        return rootFiles.stream().map(rootFile -> new File(rootFile, path));

    }

    /**
     * 获得文件在jar包内的路径
     *
     * @param file 文件
     * @return
     */
    private String getFilePathInJar(File file) {
        String fileAbsolutePath = file.getAbsolutePath();
        for (File rootFile : rootFiles) {
            String rootAbsolutePath = rootFile.getAbsolutePath();
            if (fileAbsolutePath.startsWith(rootAbsolutePath)) {
                return fileAbsolutePath.substring(rootAbsolutePath.length() + 1).replace(File.separator, "/");
            }
        }
        return file.getName();
    }

    /**
     * 获得文件输入流
     *
     * @param file 文件
     * @return
     */
    private InputStream getInputStream(File file) {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return emptyInputStream;
        }
    }

}
