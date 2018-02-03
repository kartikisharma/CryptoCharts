package kartiki.cryptocharts;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;
    private ArrayList<String> coinsNameList;
    public static HashMap<String, String> coinPriceMap = new HashMap<>();
    CoinsAdapter adapter;

    static Retrofit retrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static CryptoAPIService apiService = retrofit("https://www.cryptocompare.com/").create(CryptoAPIService.class);
    public static CryptoAPIService apiService2 = retrofit("https://min-api.cryptocompare.com/").create(CryptoAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchCryptoCoinsData();

//        Observable.fromIterable(coinsNameList)
//                .flatMap(new Function<String, ObservableSource<Pair<String, String>>>() {
//                    @Override
//                    public ObservableSource<Pair<String, String>> apply(@NonNull String coinName) throws Exception {
//                        return Observable.zip(
//                                Observable.just(coinName),
//                                fetchAndStorePriceOfCoin(coinName),
//                                new BiFunction<String,, Pair<String, String>>() {
//                                    @Override
//                                    public Pair<String, String> apply(@NonNull String coinName, @NonNull CADPrice price) throws Exception {
//                                        return new Pair<String, String>(coinName, price.getPrice());
//                                    }
//                                });
//                    }
//                })
//                .toList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());

        //progressBar.setVisibility(View.GONE);
        //recyclerView.setVisibility(View.VISIBLE);
    }

//    private void fetchPricesForCryptoCoins() {
//        if (coinsNameList != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                coinsNameList.forEach((coinName) -> fetchAndStorePriceOfCoin(coinName));
//            } else {
//                Iterator<String> it = coinsNameList.iterator();
//                while (it.hasNext()) {
//                    String curCoinName = it.next();
//                    fetchAndStorePriceOfCoin(curCoinName);
//                }
//            }
//
//            setupAdapter();
//        }
//
//    }

    private void fetchCryptoCoinsData() {
        apiService.getCoinList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(coinListResponse -> {
                    if (coinListResponse.isSuccessful()) {
                        Map<String, Coin> coinsMap = coinListResponse.body().getCoins();
                        coinsNameList = new ArrayList<>(coinsMap.keySet());
                        setupAdapter();
                    }
                }, error -> {
                    Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                });


//        Observable.just(coinsNameList)

//        apiService2.getCoinPrice()

//        call.enqueue(new Callback<CoinListResponse>() {
//            @Override
//            public void onResponse(Call<CoinListResponse> call, Response<CoinListResponse> response) {
//                if (response.isSuccessful()) {
//                    Map<String, Coin> coinsMap = response.body().getCoins();
//                    coinsNameList = new ArrayList<>(coinsMap.keySet());
//                } else {
//                    Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                }
//
//                setupAdapter();
//            }
//
//            @Override
//            public void onFailure(Call<CoinListResponse> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                setupAdapter();
//            }
//        });
    }

//    public static String fetchAndStorePriceOfCoin(final String coinName) {
//        Call<CADPrice> call = apiService2.getCoinPrice(coinName, "CAD");
//        String val;
//        call.enqueue(new Callback<CADPrice>() {
//            String coinPrice;
//
//            @Override
//            public void onResponse(Call<CADPrice> call, Response<CADPrice> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getPrice() != null) {
//                    coinPrice = response.body().getPrice();
//                } else {
//                    Log.e("CoinPriceAPIRespFailure", String.format("Failed to retrieve price of %s", coinName));
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CADPrice> call, Throwable t) {
//                Log.e("CoinPriceAPIRespFailure", String.format("Failed to retrieve price of %s", coinName));
//            }
//
//            public String getCoinPrice() {
//                return coinPrice;
//            }
//        });
//
//    }

    private void setupAdapter() {
        adapter = new CoinsAdapter(coinsNameList);
        recyclerView.setAdapter(adapter);
    }
}
