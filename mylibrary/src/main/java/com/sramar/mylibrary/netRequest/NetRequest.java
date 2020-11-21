package com.sramar.mylibrary.netRequest;

import android.support.annotation.NonNull;

import com.sramar.mylibrary.appManager.AppManager;
import com.sramar.mylibrary.appManager.BaseApplication;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class NetRequest {

    public static void upload(MultipartBody multipartBody){
//        MultipartBody.Builder builder = new MultipartBody.Builder();
        //文本部分
//        builder.addFormDataPart("fromType", "1");
//        builder.addFormDataPart("content", "意见反馈内容");
//        builder.addFormDataPart("phone", "17700000066");

        //文件部分
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
//        builder.addFormDataPart("image", file.getName(), requestBody); // “image”为文件参数的参数名（由服务器后台提供）

//        builder.setType(MultipartBody.FORM);
//        MultipartBody multipartBody = builder.build();

    }


    public static Retrofit getRetrofit(final String url) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //设置请求超时时长为15秒
        okHttpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        okHttpClientBuilder.addInterceptor(getLoggerIntercepter());
        Retrofit retrofit = new Retrofit.Builder()
                //服务器地址
                .baseUrl(url)
                //配置回调库，采用RxJava
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //设置OKHttpClient为网络客户端
                .client(okHttpClientBuilder.build()).build();

        return retrofit;
    }

    public static Retrofit getRetrofit(final String url,Map<String,String> headers) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //设置请求超时时长为15秒
        okHttpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        okHttpClientBuilder.addInterceptor(getHeaderInterceptor(headers));
        okHttpClientBuilder.addInterceptor(getLoggerIntercepter());

        Retrofit retrofit = new Retrofit.Builder()
                //服务器地址
                .baseUrl(url)
                //配置回调库，采用RxJava
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //设置OKHttpClient为网络客户端
                .client(okHttpClientBuilder.build()).build();

        return retrofit;
    }
    private static Cache getCache(){
        File cacheFile = new File(BaseApplication.getContext().getExternalCacheDir(), "HttpCache");//缓存地址
        return new Cache(cacheFile, 1024 * 1024 * 50);
    }
    private static Interceptor getCacheIntercepter(){
        Interceptor cacheIntercepter=new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //对request的设置用来指定有网/无网下所走的方式
                //对response的设置用来指定有网/无网下的缓存时长

                Request request = chain.request();
                if (BaseApplication.getInstance().getAppManager().getNetStatus() == AppManager.NetStatus.NETWORK_NONE) {
                    //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
                    //有网络时则根据缓存时长来决定是否发出请求
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE).build();
                }

                Response response = chain.proceed(request);
                if (BaseApplication.getInstance().getAppManager().getNetStatus() != AppManager.NetStatus.NETWORK_NONE) {
                    //有网络情况下，超过1分钟，则重新请求，否则直接使用缓存数据
                    int maxAge = 60; //缓存一分钟
                    String cacheControl = "public,max-age=" + maxAge;
                    //当然如果你想在有网络的情况下都直接走网络，那么只需要
                    //将其超时时间maxAge设为0即可
                    return response.newBuilder()
                            .header("Cache-Control",cacheControl)
                            .removeHeader("Pragma").build();
                } else {
                    //无网络时直接取缓存数据，该缓存数据保存1周
                    int maxStale = 60 * 60 * 24 * 7 * 1;  //1周
                    return response.newBuilder()
                            .header("Cache-Control", "public,only-if-cached,max-stale=" + maxStale)
                            .removeHeader("Pragma").build();
                }

            }
        };


        return cacheIntercepter;
    }
    private static Interceptor getHeaderInterceptor(final Map<String,String> headers){
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                final Request.Builder builder = originalRequest.newBuilder();
                //设置具体的header内容
                headers.keySet().iterator();
                Observable.fromIterable(headers.keySet())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                builder.header(s, headers.get(s));
                            }
                        });

                Request.Builder requestBuilder =
                        builder.method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
        return headerInterceptor;
    }
    private static Interceptor getLoggerIntercepter(){
        //启用Log日志
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }
}
