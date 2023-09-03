package clevertec.servlet;

import java.io.File;
import java.io.IOException;

public class PathUtil {
    private PathUtil() {
    }

    /**
     * Test and run set different user.dir. This method helps to workaround it.
     * 
     * @param relativePath main folder => folder above app directory
     * @return
     */
    public static String resolvePath(String relativePath) {
        try {
            
            String root = new File("").getCanonicalPath();
            String[] rootDirPaths = root.split(File.separator);
            String lastRootDir = rootDirPaths[rootDirPaths.length - 1];
            // it's test env
            if ("app".equals(lastRootDir)) {
                rootDirPaths[rootDirPaths.length - 1] = relativePath;
                return String.join(File.separator, rootDirPaths);
            }
            // it's run env
            else {
                return root + File.separator + relativePath;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
