package com.furja.devicemanager.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furja.overall.R;
import com.furja.devicemanager.view.SparePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 备件耗材的Fragment
 */
public class SparePartsFragment extends Fragment {
    private Context context;
    private String planID;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_spareparts,container,false);
        ButterKnife.bind(this,view);
        viewPager.setVisibility(View.VISIBLE);
        List<View> viewList=new ArrayList<View>();
        View childView=inflater.inflate(R.layout.spareparts_layout,container,false);
        viewList.add(childView);
        viewPager.setAdapter(new SparePagerAdapter(viewList));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=getContext();
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }
}
