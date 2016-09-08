package com.sk.photowall;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.sk.library.base.BaseActivity;
import com.sk.photowall.adapter.ImgsAdapter;
import com.sk.photowall.pop.AlbumPop;
import com.sk.photowall.utils.camera.FileTraversal;
import com.sk.photowall.utils.camera.LocalFileUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements AlbumPop.SelectAlbumItemListener, CompoundButton.OnCheckedChangeListener, PopupWindow.OnDismissListener, ImgsAdapter.OnPositionListener {


    @Bind(R.id.gv_photo)
    GridView gvPhoto;
    @Bind(R.id.cb_title)
    CheckBox cbTitle;
    @Bind(R.id.ll_title)
    LinearLayout llTitle;

    ImgsAdapter adapter;
    AlbumPop albumPop;

    @Override
    protected int contentViewID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        adapter = new ImgsAdapter(this);
        adapter.setListener(this);
        final LocalFileUtils util = new LocalFileUtils(this);
        gvPhoto.setAdapter(adapter);
        adapter.update(util.listAlldir());


        albumPop = new AlbumPop(this, this);
        albumPop.setOnDismissListener(this);
        cbTitle.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            showPop(albumPop);
        else
            closePop(albumPop);
    }

    @Override
    public void getPhotos(FileTraversal fileTraversal) {
        closePop(albumPop);
        //获取文件夹图片列表
        adapter.update(fileTraversal.getFilecontent(), true);
    }

    /**
     * 关闭pop
     *
     * @param popupWindow
     */
    private void closePop(PopupWindow popupWindow) {
        cbTitle.setChecked(false);
        if (popupWindow.isShowing())
            popupWindow.dismiss();
    }

    private void showPop(PopupWindow popupWindow) {
        if (popupWindow.isShowing())
            return;
        cbTitle.setChecked(true);
        albumPop.showAsDropDown(llTitle);
    }

    @Override
    public void onDismiss() {
        cbTitle.setChecked(false);
    }

    @Override
    public void onPosition(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        startIntent(CropImgActivity.class, bundle);
    }
}
