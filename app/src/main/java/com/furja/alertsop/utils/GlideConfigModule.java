package com.furja.alertsop.utils;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.furja.utils.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

import static com.furja.utils.Constants.LOG_TAG;


public class GlideConfigModule extends AppGlideModule {
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
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(LOG_TAG))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(hostnameVerifier)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                //其他配置
                .build();
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(okHttpClient));
    }
}
