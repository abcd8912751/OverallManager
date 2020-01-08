package com.furja.iqc.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.overall.R;
import com.furja.utils.GlideApp;
import com.furja.utils.SharpBus;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import static com.furja.utils.Constants.TAG_CLOSE_DIALOG;
import static com.furja.utils.Utils.getScreenWidth;
import static com.furja.utils.Utils.showLog;
public class SopRecyclerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    View.OnTouchListener touchListener;
    public SopRecyclerAdapter(int layoutResId) {
        super(layoutResId);
        touchListener=new View.OnTouchListener() {
            float lastX,lastScale;
            boolean needBack=false;
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                PhotoView photo=(PhotoView)v;
                PhotoViewAttacher attacher = photo.getAttacher();
                needBack=false;
                int offsetX = getScreenWidth() / 4;
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = motionEvent.getRawX();
                        lastScale=attacher.getScale();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        float currX = motionEvent.getRawX();
                        float scale=attacher.getScale();
                        if (currX - lastX > offsetX&&scale==lastScale)
                            needBack=true;
                }
                attacher.onTouch(v, motionEvent);
                if(needBack)
                    Observable.timer(100, TimeUnit.MILLISECONDS)
                        .subscribe(event->{
                            SharpBus.getInstance().post(TAG_CLOSE_DIALOG, "close");
                        });
                return true;
            }
        };
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        PhotoView simpleDraweeView
                = helper.getView(R.id.image_item);
        GlideApp.with(simpleDraweeView.getContext())
                .load(item)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(true)
                .override(1600,1100)
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
            simpleDraweeView.setOnTouchListener(touchListener);
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
