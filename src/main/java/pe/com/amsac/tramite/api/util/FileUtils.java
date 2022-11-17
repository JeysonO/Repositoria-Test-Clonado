package pe.com.amsac.tramite.api.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtils {
    private static final int BUFFER_SIZE = 1024;

    protected FileUtils() {
        throw new UnsupportedOperationException();
    }

    public static String filenameFromDate(boolean includeMillisecond, boolean includeSeparator, String separator) {
        String filename = "";
        String year = String.valueOf(DateUtils.getCurrentYear().intValue());
        year = StringUtils.lpad(year, 4, '0');
        String month = String.valueOf(DateUtils.getCurrentMonth().intValue());
        month = StringUtils.lpad(month, 2, '0');
        String day = String.valueOf(DateUtils.getCurrentDayOfMonth().intValue());
        day = StringUtils.lpad(day, 2, '0');
        String hour = String.valueOf(DateUtils.getCurrentHourOfDay().intValue());
        hour = StringUtils.lpad(hour, 2, '0');
        String minute = String.valueOf(DateUtils.getCurrentMinuteOfHourOfDay().intValue());
        minute = StringUtils.lpad(minute, 2, '0');
        String second = String.valueOf(DateUtils.getCurrentSecondOfHourOfDay().intValue());
        second = StringUtils.lpad(second, 2, '0');
        if (includeSeparator) {
            filename = year + month + day + separator + hour + minute + second;
        } else {
            filename = year + month + day + hour + minute + second;
        }

        if (includeMillisecond) {
            String millisecond = String.valueOf(DateUtils.getCurrentMillisecondOfHourOfDay().intValue());
            millisecond = StringUtils.lpad(millisecond, 3, '0');
            filename = filename + millisecond;
        }

        return filename;
    }

    public static List<String> listFiles(String filetype, String directory) {
        List<String> filenames = new ArrayList();
        File file = new File(directory);
        String[] files = file.list(new FileTypeFilter(filetype));

        for(int i = 0; i < files.length; ++i) {
            String filename = files[i];
            filenames.add(filename);
        }

        return filenames;
    }

    public static FileWriter createFile(String filepath) throws IOException {
        FileWriter file = new FileWriter(filepath);
        return file;
    }

    public static void createFileWithContent(String filepath, String content) throws IOException {
        FileWriter file = createFile(filepath);
        file.write(content);
        file.close();
    }

    public static void createFileWithContent(String destinationFilepath, InputStream sourceInputStream) throws IOException {
        File destinationFile = new File(destinationFilepath);
        OutputStream outputStream = new FileOutputStream(destinationFile);
        byte[] buf = new byte[0];

        int len;
        while((len = sourceInputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }

        sourceInputStream.close();
        outputStream.close();
    }

    public static void copyFile(String sourceFilepath, String destinationFilepath) {
        File sourceFile = new File(sourceFilepath);
        File destinationFile = new File(destinationFilepath);

        try {
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destinationFile);
            byte[] buf = new byte[0];

            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    public static boolean deleteFile(String filepath) {
        File file = new File(filepath);
        return file.delete();
    }

    public static boolean existFile(String filepath) {
        boolean result = true;

        try {
            FileReader file = new FileReader(filepath);
            file.close();
        } catch (FileNotFoundException var3) {
            result = false;
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return result;
    }

    public static Date getLastModified(String filepath) {
        File file = new File(filepath);
        long lastModified = file.lastModified();
        return new Date(lastModified);
    }

    public static void createDirectory(String directoryName) {
        File file = new File(directoryName);
        if (!file.exists() || file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }

    }

    public static String getFileContent(String filepath) {
        String result = "";

        try {
            FileReader file = new FileReader(filepath);
            StringBuffer content = new StringBuffer();

            int car;
            while((car = file.read()) != -1) {
                content.append((char)car);
            }

            result = content.toString();
            file.close();
        } catch (FileNotFoundException var5) {
            result = null;
        } catch (IOException var6) {
            result = null;
        }

        return result;
    }

    public static String getFileSize(long size) {
        long n = 1000L;
        String s = "";
        double kb = (double)(size / n);
        double mb = kb / (double)n;
        double gb = mb / (double)n;
        double tb = gb / (double)n;
        if (size < n) {
            s = size + " Bytes";
        } else if (size >= n && size < n * n) {
            s = String.format("%.0f", kb) + " KB";
        } else if (size >= n * n && size < n * n * n) {
            s = String.format("%.0f", mb) + " MB";
        } else if (size >= n * n * n && size < n * n * n * n) {
            s = String.format("%.0f", gb) + " GB";
        } else if (size >= n * n * n * n) {
            s = String.format("%.0f", tb) + " TB";
        }

        return s;
    }
}
