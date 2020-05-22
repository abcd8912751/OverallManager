package com.furja.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.furja.iqc.json.ApplyCheckOrder;
import com.furja.overall.R;
import com.furja.overall.beans.InspectHistoryLog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import static com.furja.utils.Utils.showLog;
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
        int layoutID = R.layout.layout_inspect_item;
        if(recyclerData!=null&&!recyclerData.isEmpty()){
            if(recyclerData.get(0) instanceof InspectHistoryLog) {
                layoutID = R.layout.layout_inspect_progress;
                DividerItemDecoration itemDecoration=new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(itemDecoration);
            }
        }
        RecyclerAdapter adapter=new RecyclerAdapter(layoutID, recyclerData);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if(clickCallBack!=null)
                clickCallBack.onComplete(position+"");
        });
        dialog.setContentView(root);
        mBehavior = BottomSheetBehavior.from((View) root.getParent());
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//全屏展开
    }

    public static class RecyclerAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
        public static final int TYPE_INSPECT_ITEM=1;
        public static final int TYPE_INSPECT_PROGRESS=2;
        public RecyclerAdapter(int layoutResId, @Nullable List<T> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, T item) {
            switch (mLayoutResId){
                case R.layout.layout_inspect_progress:
                    convertWith(helper,(InspectHistoryLog)item);
                    break;
                    default:
                        convertWith(helper,(ApplyCheckOrder)item);
                        break;
            }
        }

        /**
         * 呈现InspectHistoryLog
         * @param helper
         * @param historyLog
         */
        private void convertWith(BaseViewHolder helper, InspectHistoryLog historyLog){
            String lineSeparator = System.getProperty("line.separator");
            String receiveStr = "收料员:"+historyLog.getFReceiveBiller()
                    +lineSeparator+"单据:"+historyLog.getFReceiveBillno();
            receiveStr=receiveStr+lineSeparator;
            receiveStr=receiveStr+"数量:"+historyLog.getFReceiveQty();
            helper.setText(R.id.text_receive,receiveStr);
            TextView textInspect=helper.getView(R.id.text_inspect);
            TextView textStock=helper.getView(R.id.text_stock);
            String usePolicy = textOf(historyLog.getFUsePolicy());
            textInspect.setText(usePolicy);
            if("未审核".equals(historyLog.getFDocumentStatus())){
                textInspect.setTextColor(Color.rgb(51,51,51));
                textStock.setText("");
            }
            else {
                if(usePolicy.equals("判退")) {
                    textInspect.setTextColor(Color.MAGENTA);
                    if(TextUtils.isEmpty(historyLog.getFMrbBillNo())){
                        textStock.setTextColor(Color.rgb(51,51,51));
                        textStock.setText("尚未退料");
                        textInspect.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_right_arrow_dark,0);
                    }
                    else {
                        textStock.setTextColor(Color.BLACK);
                        String mrbStr="退料员:"+historyLog.getFMrbBiller()+lineSeparator;
                        mrbStr=mrbStr+"单据:"+historyLog.getFMrbBillNo()+lineSeparator;
                        mrbStr=mrbStr+historyLog.getFMrbDate();
                        textStock.setText(mrbStr);
                        textInspect.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_right_arrow,0);
                    }
                } else {
                    if(usePolicy.equals("让步接收"))
                        textInspect.setTextColor(Color.rgb(255,98,1));
                    else
                        textInspect.setTextColor(Color.GREEN);
                    if(TextUtils.isEmpty(historyLog.getFInstockBillNo())){
                        textStock.setTextColor(Color.rgb(51,51,51));
                        textStock.setText("尚未入库");
                        textInspect.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_right_arrow_dark,0);
                    }
                    else {
                        textStock.setTextColor(Color.BLACK);
                        String stockStr="入库员:"+historyLog.getFInstockBiller()+lineSeparator;
                        stockStr=stockStr+"单据:"+historyLog.getFInstockBillNo()+lineSeparator;
                        stockStr=stockStr+historyLog.getFInstockDate();
                        textStock.setText(stockStr);
                        textInspect.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_right_arrow,0);
                    }
                }
            }
        }

        /**
         * 呈现ApplyCheckOrder
         * @param helper
         * @param checkOrder
         */
        private void convertWith(BaseViewHolder helper, ApplyCheckOrder checkOrder){
            int index=helper.getAdapterPosition()+1;
            helper.setText(R.id.item_content,index+": "+checkOrder.getProjectName());
            TextView textView=helper.getView(R.id.item_type);
            if(!checkOrder.hasCheck()||!checkOrder.isViewed()){
                textView.setText("未检完");
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
