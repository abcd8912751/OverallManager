package com.furja.utils;

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.furja.utils.Constants.LOG_TAG;

/**
 * Created by zhangmeng on 2018/8/9.
 */

public class RetrofitBuilder {
    static Builder builder;
    public static Builder getInstance()
    {
        return RetrofitHolder.retrofitBuilder;
    }

    public static <T> T getHelperByUrl(String baseUrl,Class<T> tClass) {
        builder= RetrofitHolder.retrofitBuilder;
        Retrofit retrofit = builder.baseUrl(baseUrl).build();
        return retrofit.create(tClass);
    }

    public static RetrofitHelper getHelperByUrl(String baseUrl) {
        return getHelperByUrl(baseUrl, RetrofitHelper.class);
    }

    private static class  RetrofitHolder {
        static Builder retrofitBuilder = getRetrofitBuilder();
        private static Builder getRetrofitBuilder() {
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
                    .addNetworkInterceptor(new HeaderInterceptor())
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier(hostnameVerifier)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    //其他配置
                    .build();
            return new Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .client(okHttpClient);
        }
    }

    /**
     * 拦截添加Header
     */
    public static class HeaderInterceptor implements Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("Connection","close").build();
            return chain.proceed(request);
        }
    }
}
