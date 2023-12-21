package com.enovira.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 文件管理工具类
 * @date 2023/09/22
 */
public class FileUtils {

    private final static String TAG = "FileUtils";

    private final String SDCardRoot;

    public FileUtils() {
        // 得到当前外部存储设备的目录
        SDCardRoot = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator;
    }

    /**
     * 在SD卡上创建文件
     */
    public File createFileInSDCard(String path, String fileName)
            throws IOException {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists())
            file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir
     */
    public File createDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }

    /**
     * 在app缓存目录中创建新目录"/data/data/" + activity.getPackageName()
     * + "/files/" + dir
     *
     * @param dir
     */
    public File createLocalDir(Activity activity, String dir) {
        File dirFile = new File("/data/data/" + activity.getPackageName()
                + "/files/" + dir + File.separator);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName, String path) {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public File write2SDFromInput(String path, String fileName,
                                  InputStream input) {

        File file = null;
        OutputStream output = null;
        try {
            createDir(path);
            file = createFileInSDCard(path, fileName);
            output = Files.newOutputStream(file.toPath());
            byte[] buffer = new byte[4 * 1024];
            int temp;
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;

    }

    public static InputStream getInputStreamFromPath(String path) {

        File file = new File(path);
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException" + e.getMessage());

        }

        return inputStream;
    }

    //存储数据
    public void writeData(File file, String content) {
        FileOutputStream fileOutputStream = null;
        try {
            //获取文件输出流对象
            fileOutputStream = new FileOutputStream(file);
            //保存填写信息
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();//清除缓存

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    //读取数据
    public String readData(File file) {
        FileInputStream fileInputStream = null;
        try {
            //获取读取文件
            fileInputStream = new FileInputStream(file);
            //设置一次读取字节数
            byte[] buff = new byte[1024];
            //获取stringBuilder
            StringBuilder stringBuilder = new StringBuilder("");
            int len = 0;
            //循环读取
            while ((len = fileInputStream.read(buff)) > 0) {
                stringBuilder.append(new String(buff, 0, len));
            }
            //返回读取数据
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeBytes(String path, String content) {

        if (!TextUtils.isEmpty(content)) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(path);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException" + e.getMessage());
            }
        }

    }

    public void copyFile(File f1, File f2) throws Exception {
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2);
        byte[] buffer = new byte[length];
        while (true) {
            int ins = in.read(buffer);
            if (ins == -1) {
                in.close();
                out.flush();
                out.close();
                return;
            } else
                out.write(buffer, 0, ins);
        }
    }

    /**
     * 复制某个目录下的文件到另一个目录下
     *
     * @param fromPath 原文件路径
     * @param toPath   目标文件路径
     * @throws Exception
     */
    public void copyFolder(String fromPath, String toPath) throws Exception {
        File fileFromPath = new File(fromPath);
        File fileToPath = new File(toPath);
        if (!fileToPath.exists()) {
            fileToPath.mkdirs();
        }

        if (fileFromPath.exists() && fileFromPath.isDirectory()) {
            String[] fileFromList = fileFromPath.list();
            if (!fromPath.endsWith("/")) {
                fromPath = fromPath + "/";
            }
            if (!toPath.endsWith("/")) {
                toPath = toPath + "/";
            }

            for (int i = 0; i < fileFromList.length; i++) {
                copyFile(new File(fromPath + fileFromList[i]), new File(toPath
                        + fileFromList[i]));
            }
        }
    }

    /**
     * Method Name：copyAssetFile Description：从项目asset文件夹里复制文件到某个位置
     *
     * @param fromFilePath 原文件路径
     * @param toFilePath   目标文件路径
     *                     Creator：muzhengjun Create DateTime：2013-10-16
     * @throws IOException
     */
    public static void copyAssetFile(String fromFilePath, String toFilePath,
                                     Context context) throws IOException {
        Log.i("carmack", "copyAssetFile from " + fromFilePath + " to "
                + toFilePath);
        // int length = 2097152;
        int length = 1024 * 20;// carmack fix 10x
        InputStream in = context.getAssets().open(fromFilePath);
        FileOutputStream out = new FileOutputStream(new File(toFilePath));
        byte[] buffer = new byte[length];
        boolean a = true;
        while (a) {
            int ins = in.read(buffer);
            if (ins != -1) {
                out.write(buffer, 0, ins);
            } else {
                a = false;
            }
        }

        in.close();
        out.flush();
        out.close();
    }

    public static String getFileString(String filePath) {
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                String allContent = "";
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String content = null;
                while ((content = bufferedReader.readLine()) != null) {
                    allContent += content;
                }
                read.close();
                return allContent;
            } else {
                return "";
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return "";

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 删除单个文件 deviceSecret/deviceName
     *
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String dir, String fileName) {
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 保存压缩图片本地
     *
     * @param activity
     * @param filePath
     * @param fileName
     * @param bitmap
     */
    public static void saveBitmap(Activity activity, String filePath,
                                  String fileName, Bitmap bitmap) {
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        FileOutputStream fOut = null;
        try {
            File f = new File(new File(filePath), fileName);
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片到手机SDCard相关目录 by carmack
     *
     * @param path     图片目录
     * @param fileName 图片名称
     * @param bitmap   图片源
     * @return
     */
    public File saveBitmapToSDCard(String path, String fileName,
                                   Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        File f = null;
        FileOutputStream fOut = null;
        try {
            f = createFileInSDCard(path, fileName);
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    /**
     * Method Name：getFilePath Description：获取工程文件目录
     *
     * @param activity Creator：muzhengjun Create DateTime：2013-10-08
     */
    public static String getFilePath(Activity activity) {
        return activity.getFilesDir().toString();
    }

    /**
     * Method Name：createActivityMKdirs Description：在工程文件目录下创建文件
     *
     * @param filePath ：文件完整路径
     * @throws IOException
     */
    public static File createActivityMKdirs(String filePath, Activity activity)
            throws IOException {
        File file = null;
        String publicPath = FileUtils.getFilePath(activity) + "/";
        int directoryIndex = 0;
        if (filePath.length() > (publicPath.length() + 1)) {
            int index = filePath.indexOf("/", publicPath.length());
            while (index != -1) {
                directoryIndex = index;
                index = filePath.indexOf("/", index + 1);
            }

            String directoryPath = filePath.substring(0, directoryIndex);
            File fileDir = new File(directoryPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        }

        file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    /**
     * 获取SDcard的图片
     *
     * @param pathString 相对于SDCardRoot下图片路径
     * @return
     */
    public Bitmap getSDBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(SDCardRoot + pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(SDCardRoot + pathString);
            }
        } catch (Exception e) {
            Log.e("carmack", "getSDBitmap error: " + e.getMessage());
        }
        return bitmap;
    }

    /**
     * SD卡该路径的图片是否存在
     *
     * @param pathString
     * @return
     */
    public boolean isSDBitmapExists(String pathString) {
        try {
            File file = new File(SDCardRoot + pathString);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String[] readStringByLines(InputStream input) {
        String[] split = null;
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(input));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                buffer.append(tempString).append("&");
                Log.i("carmack", "line " + line + ": "
                        + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            split = buffer.toString().split("&");
        }
        return split;
    }

    //删除指定目录下所有文件
    public static void deleteFiles(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (null == files) {
                return;
            }
            for (File f : files) {
                f.delete();
            }
        }
    }

    /**
     * 在SD卡根目录内删除目录
     *
     * @param dir
     * @return
     */
    public void deleteSDRootDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        if (dirFile.exists()) {
            dirFile.delete();
            Log.e("file", "delete dir: " + dir);
        }
    }

    /**
     * 文件转byte
     *
     * @param file 文件
     * @return byte[] 字节数组
     */
    public static byte[] File2byte(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}