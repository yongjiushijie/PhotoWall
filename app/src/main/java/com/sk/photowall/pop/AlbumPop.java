package com.sk.photowall.pop;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.sk.photowall.R;
import com.sk.photowall.adapter.AlbumsAdapter;
import com.sk.photowall.utils.camera.FileTraversal;
import com.sk.photowall.utils.camera.LocalFileUtils;


/**
 * Created by sk on 2016/7/1.
 * 相册列表popwindow
 */
public class AlbumPop extends PopupWindow {

    ListView lvAlbum;

    private Context context;
    private View view;
    private AlbumsAdapter albumsAdapter;
    private LocalFileUtils util;

    public AlbumPop(Context context, final SelectAlbumItemListener listener) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.pop_albums, null);
        this.setContentView(view);
        lvAlbum = (ListView) view.findViewById(R.id.lv_album);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xddffffff);
        this.setBackgroundDrawable(dw);
        lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener == null)
                    return;
                FileTraversal fileTraversal = util.LocalImgFileList().get(position);
                listener.getPhotos(fileTraversal);
            }
        });

        getData(context);
    }


    private void getData(Context context) {
        albumsAdapter = new AlbumsAdapter(context);
        lvAlbum.setAdapter(albumsAdapter);
        util = new LocalFileUtils(context);
        albumsAdapter.update(util.LocalImgFileList());
    }

    /**
     * 相册列表监听
     */
    public interface SelectAlbumItemListener {
        void getPhotos(FileTraversal fileTraversal);
    }


}
