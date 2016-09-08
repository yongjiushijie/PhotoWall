package com.sk.photowall.widget.crop;

/**
 * 裁切接口按钮操作接口
 * Created by sk on 2016/7/25.
 */
public interface CropListener {

    /**
     * 左旋转
     */
    void onLeft();

    /**
     * 提交剪切图片
     */
    void onCenter();

    /**
     * 右旋转
     */
    void onRight();

}
