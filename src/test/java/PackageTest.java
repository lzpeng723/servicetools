import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 打包
 *
 * @author lzpeng
 * @version 1.0
 * @date 2021-06-09 11:34
 */
public class PackageTest extends AbstractPackageTest {

    /**
     * 生成服务工具平台 jar 包
     */
    @Test
    public void genServiceToolsJar() {
        genJar("build/libs/servicetools.jar", null, "kd/lzp/servicetools");
    }

    /**
     * 生成服务工具平台 zip 包
     */
    @Test
    public void genServiceToolsZip() {
        File jarFile = genJar("build/libs/servicetools.jar", null, "kd/lzp/servicetools");
        genZip("build/distributions/servicetools.zip", null, jarFile);
        jarFile.deleteOnExit();
    }

    /**
     * 生成第三方引用 zip 包
     */
    @Test
    public void genCustomZip() throws IOException {
        String classPathStr = System.getProperty("java.class.path");
        String[] classPaths = classPathStr.split(";");
        String trdPath = new File("../../../mservice-cosmic/lib/trd").getCanonicalPath();
        String bosPath = new File("../../../mservice-cosmic/lib/bos").getCanonicalPath();
        String bizPath = new File("../../../mservice-cosmic/lib/biz").getCanonicalPath();
        List<String> needPackageJarPath = new ArrayList<>();
        for (String classPath : classPaths) {
            if (new File(classPath).isFile() && !classPath.startsWith(trdPath) && !classPath.startsWith(bosPath) && !classPath.startsWith(bizPath)) {
                needPackageJarPath.add(classPath);
                System.out.println(classPath);
            }
        }
        genZip("build/distributions/custom.zip", null, needPackageJarPath.toArray(new String[0]));
    }
}
