package ru.javarush;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceFileCreator {
    private static final Pattern PATTERN_PACKAGE = Pattern.compile("^package\\s+(.+);$");
    private static final Pattern PATTERN_FILENAME = Pattern.compile(".*(class|interface|enum)\\s+(\\w+)\\s*.*\\{");
    private static final Pattern PATTERN_COURSE_PART = Pattern.compile("^com\\.javarush\\.task(\\.pro)?\\.task(\\d{2})\\.task\\d{4}.*$");
    private static final Properties props = new Properties();

    public static void main(String[] args) throws IOException, UnsupportedFlavorException, InterruptedException {
        props.load(new FileReader("app.properties"));

        String oldContent = null;
        while (true) {
            Thread.sleep(500);
            String currentContent = readClipboard();
            if (!currentContent.equals(oldContent)) {
                try {
                    String packageName = getPackageName(currentContent);
                    String fileName = getFileName(currentContent);
                    String dirPath = getProjectDirectoryForPackage(packageName) + "\\" + packageName.replace('.', '\\');
                    String filePath = dirPath + "\\" + fileName + ".java";

                    Files.createDirectories(Paths.get(dirPath));

                    Files.write(Paths.get(filePath),
                            currentContent.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE);

                    System.out.println(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                oldContent = currentContent;
            }
        }
    }

    public static String getProjectDirectoryForPackage(String packageName) {

        Matcher matcher = PATTERN_COURSE_PART.matcher(packageName);
        if (matcher.find()) {
            int part = Integer.parseInt(matcher.group(2));
            if (part > 0 && part <= 10) {
                return props.getProperty("syntax.dir");
            } else if (part >= 11 && part <= 20) {
                return props.getProperty("core.dir");
            } else if (part >= 21 && part <= 30) {
                return props.getProperty("multithreading.dir");
            } else if (part >= 31 && part <= 40) {
                return props.getProperty("collections.dir");
            } else {
                throw new IllegalArgumentException("Unknown part: " + part + " in package: " + packageName);
            }
        }

        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    public static String readClipboard() throws IOException, UnsupportedFlavorException {
        return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
    }

    public static String getPackageName(String content) {
        String[] lines = content.split("\\n");

        for (String line : lines) {
            line = line.trim();
            Matcher matcher = PATTERN_PACKAGE.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }

        }

        throw new IllegalArgumentException("Not found package line");
    }

    public static String getFileName(String content) {
        String[] lines = content.split("\\n");

        for (String line : lines) {
            line = line.trim();
            Matcher matcher = PATTERN_FILENAME.matcher(line);
            if (matcher.find()) {
                return matcher.group(2);
            }
        }

        throw new IllegalArgumentException("Not found class name line");
    }
}
