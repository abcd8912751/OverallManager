package com.furja.utils;

import android.accounts.NetworkErrorException;

import com.alibaba.fastjson.JSONException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static com.furja.utils.Constants.INTERNET_ABNORMAL;

public class RetryWhenUtils implements Function<Observable<Throwable>, ObservableSource<?>> {
    RequestLog requestLog=new RequestLog();
    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                requestLog.error();
                if(throwable instanceof JSONException)
                    return Observable.error(throwable);
                if(requestLog.isOverTimes())
                    return Observable.error(throwable);
                return Observable.timer(1, TimeUnit.SECONDS); // 1S后重新请求
            }
        });
    }

    public static RetryWhenUtils create(){
        return new RetryWhenUtils();
    }


    public static class RequestLog {
        private int errorCount;
        private int limit;
        public RequestLog()
        {
            initValue(3);
        }

        public RequestLog(int limitCount)
        {
            initValue(limitCount);
        }

        public boolean isOverTimes()
        {
            if(errorCount <limit)
                return false;
            return true;
        }



        /**
         * 请求失败将请求次数加1
         */
        public void error()
        {
            this.errorCount = errorCount +1;
        }

        /**
         * 初始化数值
         */
        public void initValue(int value)
        {
            this.limit=value;
            this.errorCount =0;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public void setErrorCount(int errorCount) {
            this.errorCount = errorCount;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public boolean isLastTimes() {
            return (limit-errorCount)==1;
        }
    }
}
