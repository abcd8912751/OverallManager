package com.furja.overall.view;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.overall.R;
import com.furja.overall.beans.NaviItem;

/**
 * 导航Activity
 */

public class NaviRecyclerAdapter extends BaseQuickAdapter<NaviItem,BaseViewHolder> {
    public NaviRecyclerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, NaviItem item) {
        helper.setText(R.id.item_title,item.getTitle());
        helper.setImageResource(R.id.item_icon,item.getIconID());
    }

}
