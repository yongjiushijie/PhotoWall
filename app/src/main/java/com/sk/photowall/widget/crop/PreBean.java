package com.sk.photowall.widget.crop;

import android.graphics.Rect;

/**
 * Created by Administrator on 2016/7/25.
 */
public class PreBean {

    private int id;
    private Rect rect;
    private int preWidth;
    private int preHeight;

    PreBean(Rect rect, int preWidth, int preHeight) {
        this.rect = rect;
        this.preWidth = preWidth;
        this.preHeight = preHeight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getPreWidth() {
        return preWidth;
    }

    public void setPreWidth(int preWidth) {
        this.preWidth = preWidth;
    }

    public int getPreHeight() {
        return preHeight;
    }

    public void setPreHeight(int preHeight) {
        this.preHeight = preHeight;
    }
}
