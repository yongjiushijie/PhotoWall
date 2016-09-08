package com.sk.photowall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sk.photowall.R;
import com.sk.photowall.utils.camera.FileTraversal;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sk on 2016/7/1.
 * 照片墙，相册列表
 */
public class AlbumsAdapter extends BaseAbsAdapter<FileTraversal> {

    public AlbumsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_album_list, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        FileTraversal fileTraversal = mDataSource.get(position);
        viewHolder.tvAlbumName.setText(fileTraversal.getFilename());
        viewHolder.tvPhotoNum.setText(String.valueOf(fileTraversal.getFilecontent().size() + ""));
        Glide.with(mContext).load(fileTraversal.getFilecontent().get(0)).into(viewHolder.imgAlbum);
        return view;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class ViewHolder {
        @Bind(R.id.img_album)
        ImageView imgAlbum;
        @Bind(R.id.tv_album_name)
        TextView tvAlbumName;
        @Bind(R.id.tv_photo_num)
        TextView tvPhotoNum;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
