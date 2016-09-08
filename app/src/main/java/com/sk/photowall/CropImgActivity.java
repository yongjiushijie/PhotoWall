package com.sk.photowall;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.sk.library.base.BaseActivity;
import com.sk.photowall.widget.crop.CropImage;
import com.sk.photowall.widget.crop.CropListener;

import butterknife.Bind;

public class CropImgActivity extends BaseActivity {

    String url;
    @Bind(R.id.crop)
    CropImage crop;

    @Override
    protected void getExtraEvent(Bundle extras) {
        super.getExtraEvent(extras);
        url = extras.getString("url");
    }

    @Override
    protected int contentViewID() {
        return R.layout.activity_crop_img;
    }

    @Override
    protected void initView() {
        crop.setImageUrl(url);
    }

    @Override
    protected void initialize() {


        crop.setListener(new CropListener() {
            @Override
            public void onLeft() {
                //// TODO: 16/9/8 图片旋转
                crop.setRotate(90);
            }

            @Override
            public void onCenter() {
                crop.CreatNewPhoto(new CropImage.CallBack() {
                    @Override
                    public void goEditImg(String url) {
                        //// TODO: 16/9/8  通知相册检索改图片url
                        systemUpdateImage(url);
                        finish();
                    }
                });
            }

            @Override
            public void onRight() {
                // TODO: 16/9/8 图片旋转
                crop.setRotate(-90);
            }

        });
    }

    /**
     * 发送广播，通知系统检索图片
     *
     * @param url 新图片path
     */
    private void systemUpdateImage(String url) {
        String name = null;
        if (!TextUtils.isEmpty(url)) {
            name = url.substring(url.lastIndexOf("/") + 1);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, name);
            values.put(MediaStore.Images.Media.DESCRIPTION, "");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, url);
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + url)));
        }
    }

}
