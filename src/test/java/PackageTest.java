import org.junit.Test;

import java.io.File;
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
     * 生成服务工具平台 zip 包
     */
    @Test
    public void genServiceToolsZip() {
        File jarFile = genJar("build/libs/servicetools.jar", null, "kd/lzp/servicetools");
        genZip("build/distributions/servicetools.zip", null, jarFile);
    }

    /**
     * 生成第三方引用 zip 包
     */
    @Test
    public void genCustomZip() {
        List<String> needPackageJarPath = getClassPathJarPath();
        genZip("build/distributions/custom.zip", null, needPackageJarPath.toArray(new String[0]));
    }

}
