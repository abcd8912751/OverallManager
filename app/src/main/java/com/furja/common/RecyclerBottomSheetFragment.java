package com.furja.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.iqc.json.ApplyCheckOrder;
import com.furja.overall.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import static com.furja.utils.Utils.textOf;

public class RecyclerBottomSheetFragment<T> extends BottomSheetDialogFragment {
    private List<T> recyclerData;
    private JustCallBack clickCallBack;
    private String title;
    private BottomSheetBehavior mBehavior;
    public RecyclerBottomSheetFragment(List<T> recyclerData) {
        this.recyclerData = recyclerData;
    }
    public RecyclerBottomSheetFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(this.getContext(), R.style.BottomSheetDialog);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        Context context = getContext();
        View root=View.inflate(context,R.layout.layout_recycler_bottomsheet,null);
        TextView text_title = root.findViewById(R.id.text_title);
        RecyclerView recyclerView=root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(WrapLinearLayoutManager.wrap(context));
        text_title.setText(textOf(title));
        RecyclerAdapter adapter=new RecyclerAdapter(recyclerData);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if(clickCallBack!=null)
                clickCallBack.onComplete(position+"");
        });
        dialog.setContentView(root);
        mBehavior = BottomSheetBehavior.from((View) root.getParent());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//全屏展开
    }

    public static class RecyclerAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

        public RecyclerAdapter(@Nullable List<T> data) {
            super(R.layout.layout_inspect_item,data);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            if(item instanceof ApplyCheckOrder){
                ApplyCheckOrder checkOrder= (ApplyCheckOrder) item;
                convertWith(helper,checkOrder);
            }
        }

        private void convertWith(BaseViewHolder helper, ApplyCheckOrder checkOrder) {
            int index=helper.getAdapterPosition()+1;
            helper.setText(R.id.item_content,index+": "+checkOrder.getProjectName());
            TextView textView=helper.getView(R.id.item_type);
            if(!checkOrder.hasCheck()){
                textView.setText("未检");
                textView.setBackgroundResource(R.drawable.shape_notinspect_bg);
            }
            else if(checkOrder.isQualified(true)) {
                textView.setText("合格");
                textView.setBackgroundResource(R.drawable.shape_qualified_bg);
            }
            else {
                textView.setText("不合格");
                textView.setBackgroundResource(R.drawable.shape_unqualified_bg);
            }
        }
    }


    public List<T> getRecyclerData() {
        return recyclerData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRecyclerData(List<T> recyclerData) {
        this.recyclerData = recyclerData;
    }

    public JustCallBack getClickCallBack() {
        return clickCallBack;
    }

    public void setClickCallBack(JustCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }
}
