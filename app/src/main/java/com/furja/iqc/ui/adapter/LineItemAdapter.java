package com.furja.iqc.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.overall.R;
import com.furja.iqc.json.NewQCList;

/**
 * 电源线检验项目的适配器
 */

public class LineItemAdapter extends BaseQuickAdapter<NewQCList.QCDataBean,BaseViewHolder> {

    public LineItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewQCList.QCDataBean item) {
        helper.setText(R.id.text_barcode,"条形码: "+item.getBarcode());
        helper.setText(R.id.text_billNo,"请检单:"+item.getApplyOrderBillNo());
        helper.setText(R.id.text_qty,"数量:"+item.getApplyQcNum());
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }
}
