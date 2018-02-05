package kartiki.cryptocharts;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kartiki on 2018-02-05.
 */

public class App extends Application {
    private CryptoCoinListAPIService cryptoCoinListAPIService;
    private CryptoPriceAPIService cryptoPriceAPIService;
    private InternetConnectionListener mInternetConnectionListener;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setInternetConnectionListener(InternetConnectionListener listener) {
        mInternetConnectionListener = listener;
    }

    public void removeInternetConnectionListener() {
        mInternetConnectionListener = null;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public CryptoCoinListAPIService getCryptoCoinListAPIService() {
        if (cryptoCoinListAPIService == null) {
            cryptoCoinListAPIService = provideRetrofit(CryptoCoinListAPIService.baseUrl)
                    .create(CryptoCoinListAPIService.class);
        }

        return cryptoCoinListAPIService;
    }

    public CryptoPriceAPIService getCryptoPriceAPIService() {
        if (cryptoPriceAPIService == null) {
            cryptoPriceAPIService = provideRetrofit(CryptoPriceAPIService.baseUrl)
                    .create(CryptoPriceAPIService.class);
        }
        return cryptoPriceAPIService;
    }

    private Retrofit provideRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    private OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.connectTimeout(5, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(10, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS);

        okhttpClientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
            @Override
            public boolean isInternetAvailable() {
                return App.this.isInternetAvailable();
            }

            @Override
            public void onInternetUnavailable() {
                if (mInternetConnectionListener != null) {
                    mInternetConnectionListener.onInternetUnavailable();
                }
            }
        });

        return okhttpClientBuilder.build();
    }


}
