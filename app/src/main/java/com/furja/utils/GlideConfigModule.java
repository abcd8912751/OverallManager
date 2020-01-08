package com.furja.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.zhy.http.okhttp.log.LoggerInterceptor;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;

import static com.furja.utils.Constants.LOG_TAG;

@GlideModule
public class GlideConfigModule extends AppGlideModule {
    private static volatile Call.Factory internalClient;
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        HttpsUtils.SSLParams sslParams
                = HttpsUtils.getSslSocketFactory(null, null, null);
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        if (internalClient == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoggerInterceptor(LOG_TAG))
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier(hostnameVerifier)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
            synchronized (GlideConfigModule.class) {
                if (internalClient == null) {
                    internalClient = okHttpClient;
                }
            }
        }
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(internalClient));
    }

    /**
     * java.lang.RuntimeException: Expected instanceof GlideModule
     * 此方法不重写会报以上错误,@return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
