package com.furja.verify.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.furja.overall.R;
import com.furja.verify.utils.LocalQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * 自动适配器
 */

public class MyAutoAdapter extends ArrayAdapter {

    private ArrayList<String> autoPrompts;
    private int layoutResourceID;
    public MyAutoAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        autoPrompts=new ArrayList<String>();
        this.layoutResourceID=resource;
    }

    public MyAutoAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getCount() {
        return autoPrompts.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return autoPrompts.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AutoViewHolder autoViewHolder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(parent.getContext()).inflate(layoutResourceID,null);
            autoViewHolder=new AutoViewHolder();
            autoViewHolder.textView=(TextView)convertView.findViewById(R.id.simple_text_item);
            convertView.setTag(autoViewHolder);
        }
        else
            autoViewHolder=(AutoViewHolder)convertView.getTag();
        autoViewHolder.textView.setText(getItem(position));
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
//        return super.getFilter();
        return new MyFilter();
    }

    /**
     * 自行定制的过滤器,根据输入去本地数据库寻找答案
     */
    private class MyFilter extends Filter
    {
        LocalQuery localQuery;
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            localQuery =new LocalQuery();
            if(TextUtils.isEmpty(charSequence))
                charSequence="";
            List<String> results=localQuery.query(charSequence);
            FilterResults filterResults= new FilterResults();
            filterResults.count=results.size();
            filterResults.values=results;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            autoPrompts= (ArrayList<String>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    class AutoViewHolder
    {

        TextView textView;
        public AutoViewHolder() {
        }
    }
}
