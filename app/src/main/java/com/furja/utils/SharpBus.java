package com.furja.utils;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.furja.utils.Utils.showLog;

/**
 * 基于RxJava的SharpBus
 */

public class SharpBus {
    private  static SharpBus sharpBus;   //静态也是唯一的实例
    private ConcurrentHashMap<SharpTag, Subject> obseorvableMap;

    public static SharpBus getInstance() {
        synchronized (SharpBus.class) {
            if(sharpBus == null)
                sharpBus = new SharpBus();
        }
        return sharpBus;
    }

    /**
     * 私有化构造方法以锁定全局只有一个实例
     */
    private SharpBus() {
        obseorvableMap = new ConcurrentHashMap<>();
    }

    /**
     * 注册观察者并放入相应Map
     *
     */
    public synchronized <T> Observable<T> register(@NonNull SharpTag sharpTag) {
        Subject subject
                = PublishSubject.create().toSerialized();
        if (!obseorvableMap.contains(sharpTag))
            obseorvableMap.put(sharpTag,subject);
        else
            subject=obseorvableMap.get(sharpTag);
        return subject;
    }

    public  <T> Observable<T> register(String tag) {
        return register(new SharpTag(tag, this));
    }

    public  <T> Observable<T> register(String tag, Object source) {
        return register(new SharpTag(tag, source));
    }

    public  <T> Observable<T> register(String tag, Object source,Class<T> tClass) {
        return register(new SharpTag(tag, source),tClass);
    }

    /**
     * 注册指定数据类型的观察者并放入相应Map
     */
    public synchronized <T> Observable<T> register(@NonNull SharpTag sharpTag,Class<T> tClass) {
        Subject subject = PublishSubject.create().toSerialized();
        if (!obseorvableMap.contains(sharpTag))
            obseorvableMap.put(sharpTag,subject);
        else
            subject=obseorvableMap.get(sharpTag);
        return subject;
    }

    /**
     * 解注册
     * @param tag
     */
    public synchronized void unregister(String tag) {
        List<SharpTag> tags = new ArrayList<>();
        for (Map.Entry<SharpTag, Subject> entry : obseorvableMap.entrySet()) {
            if(entry.getKey().getTag().equals(tag))
                tags.add(entry.getKey());
        }
        for (SharpTag sharpTag : tags)
            unregister(sharpTag);
    }

    /**
     * 解注册
     * @param sharpTag
     */
    public  void unregister(SharpTag sharpTag) {
        obseorvableMap.remove(sharpTag);
    }

    /**
     * 发送事件
     * @param sharpTag
     * @param event
     */
    public  void post(@NonNull SharpTag sharpTag,@NonNull Object event) {
        Subject subject=obseorvableMap.get(sharpTag);
        if(subject!=null)
            subject.onNext(event);
    }

    public static void publishEvent(@NonNull SharpTag sharpTag,@NonNull Object event) {
        getInstance().post(sharpTag,event);
    }

    public static void publishEvent(@NonNull String tag,@NonNull Object event) {
        getInstance().post(tag,event);
    }

    public  void post(@NonNull String  tag,@NonNull Object event) {
        for (Map.Entry<SharpTag, Subject> entry : obseorvableMap.entrySet()) {
            SharpTag sharpTag = entry.getKey();
            if(sharpTag.getTag().equals(tag))
                entry.getValue().onNext(event);
        }
    }

    /**
     * hashMap的key 实例
     */
    public static class SharpTag{
        private String tag;
        private Object source;

        public SharpTag(){
        }

        public SharpTag(String tag, Object source) {
            this.tag = tag;
            this.source = source;
        }
        public SharpTag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SharpTag sharpTag = (SharpTag) o;
            Object tagSource=sharpTag.getSource();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return tag.equals(sharpTag.tag)
                        &&(Objects.equals(source, tagSource));
            }
            else
                return tag.equals(sharpTag.tag)
                        &&(source == tagSource) ||
                        (source != null && source.equals(tagSource));
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + (tag == null ? 0 : tag.hashCode());
            result = 31 * result + (source == null ? 0 : source.hashCode());
            return result;
        }
    }
}
