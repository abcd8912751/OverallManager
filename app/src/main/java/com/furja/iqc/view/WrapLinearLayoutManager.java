package com.furja.iqc.view;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 捕捉布局异常的 LayoutManager
 */
public class WrapLinearLayoutManager extends LinearLayoutManager {
    private boolean isCanScroll=false;
    public WrapLinearLayoutManager(Context context) {
        super(context,LinearLayoutManager.VERTICAL,false);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically();
    }
}
