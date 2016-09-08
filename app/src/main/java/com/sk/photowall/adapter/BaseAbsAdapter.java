package com.sk.photowall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * skin
 */
public abstract class BaseAbsAdapter<T> extends BaseAdapter implements
        OnScrollListener {

    protected List<T> mDataSource = new ArrayList<T>();
    protected Context mContext;
    protected LayoutInflater mInflater;

    private boolean isScrolling;

    public BaseAbsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置适配器的数据，添加数据
     *
     * @param dataList
     */
    public void update(List<T> dataList) {
        if (dataList != null) {
            mDataSource.addAll(dataList);
        }

        notifyDataSetChanged();
    }

    /**
     * 设置适配器数据
     *
     * @param dataList data
     *                 是否需要清空list然后在加载数据
     */
    public void update(List<T> dataList, Boolean isClear) {
        if (isClear) {
            clear();
        }
        if (dataList != null) {
            mDataSource.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置适配器的数据,向列表具体位置添加数据
     */
    public void update(T t, int i) {
        if (t != null) {
            mDataSource.add(i, t);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置适配器的数据，向列表底部添加数据
     */
    public void update(T t) {
        if (t != null) {
            mDataSource.add(t);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置适配器的数据,添加或删除某个实体
     */
    public void update(int i, T t, boolean isAdd) {
        if (t != null) {
            if (isAdd) {
                mDataSource.add(i, t);
            } else {
                mDataSource.remove(t);
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mDataSource.clear();
    }

    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    @Override
    public T getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /**
     * 判断当前listview是否滚动状态
     *
     * @return
     */
    public boolean isScrolling() {
        return isScrolling;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 设置是否滚动的状态
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            isScrolling = false;
            this.notifyDataSetChanged();
        } else {
            isScrolling = true;
        }
    }
}
