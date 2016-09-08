package com.sk.photowall.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sk.photowall.R;
import com.sk.photowall.utils.camera.ScreenSizeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sk on 2016/6/30.
 * 照片墙
 */
public class ImgsAdapter extends BaseAbsAdapter<String> {

    private static final int clumn = 4;
    private OnPositionListener listener;

    public ImgsAdapter(Context context) {
        super(context);
    }

    public void setListener(OnPositionListener onPositionListener) {
        this.listener = onPositionListener;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_photo, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final String imgUrl = mDataSource.get(position);
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext).load(imgUrl).into(viewHolder.imgPhoto);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null)
                    return;
                listener.onPosition(imgUrl);
            }
        });
        return view;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    class ViewHolder {
        @Bind(R.id.img_phopo)
        ImageView imgPhoto;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            getImgHeight(imgPhoto);
        }
    }

    /**
     * 设置img高度
     *
     * @param target
     */
    private void getImgHeight(ImageView target) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) target.getLayoutParams();
        params.width = (ScreenSizeUtils.getScreenWidth(mContext) - 5 * (clumn - 1)) / clumn;
        params.height = params.width;
    }

    public interface OnPositionListener {
        void onPosition(String url);
    }
}
