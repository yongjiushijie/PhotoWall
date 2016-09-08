package com.sk.photowall.utils.file;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;


/**
 * 文件缓存的文件
 */
public class FileUtil {

    public static final String BASE_CACHE_IMG = Environment.getExternalStorageDirectory().getAbsolutePath();


    /**
     * 获取隐藏文件夹
     *
     * @param context
     * @param directory
     * @return
     */
    public File getHideDirectory(Context context, String directory) {
        File urlPath = null;
        if (isSdcardExist()) {
            String path = BASE_CACHE_IMG + "/" + context.getPackageName() + "/." + directory;
            urlPath = new File(path);
            if (urlPath.exists())
                return urlPath;
        }
        return urlPath;
    }

    /**
     * 获取文件
     *
     * @param context
     * @param directory
     * @param fileName
     * @return
     */
    public File getFile(Context context, String directory, String fileName) {
        File urlPath = null;
        if (isSdcardExist()) {
            String file = BASE_CACHE_IMG + "/" + context.getPackageName() + "/" + directory;
            createDirFile(file);
            urlPath = createNewFile(new File(file, fileName));
        }
        return urlPath;
    }


    /**
     * 删除整个文件夹以及中所用文件
     *
     * @param file
     */
    public static void delFile(File file) {
        if (!file.exists())
            return;
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                if (f.isFile() && f.isDirectory())
                    f.delete();
                delFile(f);
            }
            file.delete();
        }
    }

    /**
     * 判断SD是否可以
     *
     * @return
     */
    public boolean isSdcardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 创建根目录
     *
     * @param path 目录路径
     */
    public void createDirFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return 创建的文件
     */
    public File createNewFile(File path) {
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return path;
    }

    /**
     * 获取指定目录下的文件名称列表
     *
     * @param folderPath
     * @return
     */
    public static String[] getImageNames(String folderPath) {
        File file01 = new File(folderPath);
        String[] files01 = file01.list();
        int imageFileNums = 0;
        if (files01 != null && files01.length > 0) {
            try {
                for (int i = 0; i < files01.length; i++) {
                    File file02 = new File(folderPath + "/" + files01);
                    if (!file02.isDirectory()) {
                        if (isImageFile(file02.getName())) {
                            imageFileNums++;
                        }
                    }
                }
                String[] files02 = new String[imageFileNums];
                int j = 0;
                for (int i = 0; i < files01.length; i++) {
                    File file02 = new File(folderPath + "/" + files01);
                    if (!file02.isDirectory()) {
                        if (isImageFile(file02.getName())) {
                            files02[j] = file02.getName();
                            j++;
                        }
                    }
                }
                return files02;
            } catch (Exception e) {
                e.printStackTrace();
                return new String[]{};
            }
        }
        return new String[]{};

    }

    /**
     * 判断是否是图片文件
     *
     * @param fileName
     * @return
     */
    private static boolean isImageFile(String fileName) {
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (fileEnd.equalsIgnoreCase("jpg")) {
            return true;
        } else if (fileEnd.equalsIgnoreCase("png")) {
            return true;
        } else if (fileEnd.equalsIgnoreCase("bmp")) {
            return true;
        } else if (fileEnd.equalsIgnoreCase("jpeg")) {
            return true;
        } else {
            return false;
        }
    }
}