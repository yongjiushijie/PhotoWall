package com.sk.photowall.utils.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.text.TextUtils;


import com.sk.photowall.utils.camera.ImageUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * save image file to local
 * Created by sk on 2016/8/2.
 */
public class SaveImageFiles {


    /**
     * 压缩图片大小存储文件到本地
     *
     * @param iniUrl  原始文件Url
     * @param destUrl 目的文件Url
     * @param width   宽
     * @param height  高
     */
    public static void saveFile(String iniUrl, String destUrl, int width, int height) {
        if (TextUtils.isEmpty(iniUrl) || TextUtils.isEmpty(destUrl))
            return;
        Bitmap mBitmap = ImageUtils.getSmallBitmap(iniUrl, width, height);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destUrl);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            mBitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
