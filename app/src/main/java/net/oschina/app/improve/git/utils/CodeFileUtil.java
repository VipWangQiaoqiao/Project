package net.oschina.app.improve.git.utils;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public final class CodeFileUtil {
    /**
     * 判断是否是代码文件
     */
    public static boolean isCodeTextFile(String fileName) {
        boolean res = false;
        // 文件的后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index);
        }
        String codeFileSuffix[] = new String[]
                {
                        ".java",
                        ".confg",
                        ".ini",
                        ".xml",
                        ".json",
                        ".txt",
                        ".go",
                        ".php",
                        ".php3",
                        ".php4",
                        ".php5",
                        ".js",
                        ".css",
                        ".html",
                        ".properties",
                        ".c",
                        ".hpp",
                        ".h",
                        ".hh",
                        ".cpp",
                        ".cfg",
                        ".rb",
                        ".example",
                        ".gitignore",
                        ".project",
                        ".classpath",
                        ".m",
                        ".md",
                        ".rst",
                        ".vm",
                        ".cl",
                        ".py",
                        ".pl",
                        ".haml",
                        ".erb",
                        ".scss",
                        ".bat",
                        ".coffee",
                        ".as",
                        ".sh",
                        ".m",
                        ".pas",
                        ".cs",
                        ".groovy",
                        ".scala",
                        ".sql",
                        ".bas",
                        ".xml",
                        ".vb",
                        ".xsl",
                        ".swift",
                        ".ftl",
                        ".yml",
                        ".ru",
                        ".jsp",
                        ".markdown",
                        ".cshap",
                        ".apsx",
                        ".sass",
                        ".less",
                        ".ftl",
                        ".haml",
                        ".log",
                        ".tx",
                        ".csproj",
                        ".sln",
                        ".clj",
                        ".scm",
                        ".xhml",
                        ".xaml",
                        ".lua",
                        ".sty",
                        ".cls",
                        ".thm",
                        ".tex",
                        ".bst",
                        ".config",
                        "Podfile",
                        "Podfile.lock",
                        ".plist",
                        ".storyboard",
                        "gradlew",
                        ".gradle",
                        ".pro",
                        ".pbxproj",
                        ".xcscheme",
                        ".proto",
                        ".wxss",
                        ".wxml",
                        ".vi",
                        ".ctl",
                        ".ts"
                };
        for (String string : codeFileSuffix) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }

        // 特殊的文件
        String fileNames[] = new String[]
                {
                        "LICENSE", "TODO", "README", "readme", "makefile", "gemfile", "gemfile.*", "gemfile.lock", "CHANGELOG"
                };

        for (String string : fileNames) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }

        return res;
    }


    /**
     * 判断是否是图片
     */
    public static boolean isImage(String fileName) {
        boolean res = false;
        // 图片后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index);
        }
        String imageSuffix[] = new String[]
                {
                        ".png", ".jpg", ".jpeg",
                        ".jpe", ".bmp", ".exif",
                        ".dxf", ".wbmp", ".ico",
                        ".gif", ".pcx", ".hdri",
                        ".fpx", ".ufo", ".tiff",
                        ".svg", ".eps", ".ai",
                        ".tga", ".pcd",
                };
        for (String string : imageSuffix) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }
        return res;
    }

}
