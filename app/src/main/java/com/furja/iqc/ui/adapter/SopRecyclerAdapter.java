package com.furja.iqc.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.overall.R;
import com.github.chrisbanes.photoview.PhotoView;

public class SopRecyclerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SopRecyclerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        PhotoView simpleDraweeView
                = helper.getView(R.id.image_item);
        Glide.with(simpleDraweeView.getContext())
                .load(item)
                .override(1700,1200)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        adjustSdv(simpleDraweeView,resource.getMinimumWidth(),resource.getMinimumHeight());
                        return false;
                    }
                })
                .into(simpleDraweeView);
    }

    private void adjustSdv(ImageView image,int width,int height){
        int screenWidth = getScreenWidth();
        ViewGroup.LayoutParams params = image.getLayoutParams();
        int viewWidth = screenWidth;
        int vw =image.getWidth() -image.getPaddingLeft() -image.getPaddingRight();
        float scale = (float) vw / (float) width;
        int vh = Math.round(height * scale);
        params.height= vh +image.getPaddingTop() +image.getPaddingBottom();
        image.setLayoutParams(params);
        params.width = screenWidth;
        image.setLayoutParams(params);
    }

    public  int getScreenWidth() {
        Context context = getRecyclerView().getContext();
        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;  //横屏时使用高度
    }

}
