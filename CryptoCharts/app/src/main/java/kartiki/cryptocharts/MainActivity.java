package kartiki.cryptocharts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
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

        //progressBar.setVisibility(View.GONE);
        //recyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchCryptoCoinsData() {

        apiService.getCoinList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnNext(coinListResponse -> coinsNameList = new ArrayList<>(coinListResponse.body().getCoins().keySet()))
                .flatMap(coinListResponse -> Observable.just(coinListResponse.body().getCoins().keySet()))
                .flatMapIterable(baseDatas -> baseDatas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(coinsName ->
                        apiService2
                                .getCoinPrice(coinsName, "CAD")
                                .flatMap(cadPrice -> Observable.just(new Pair<>(coinsName, cadPrice.getPrice())))
                                .doOnNext(pair -> coinPriceMap.put(pair.first, pair.second)))
//                .doOnNext(pair -> coinPriceMap.put(pair.first, pair.second))
                .toList()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> setupAdapter(coinsNameList, coinPriceMap),
                        error -> {
                            Log.e("error", error.getMessage());
                            Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        });



        // also didn't work wtf
//        apiService.getCoinList()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .doOnNext(coinListResponse -> {
//                    if (coinListResponse.isSuccessful()) {
//                        coinsNameList = new ArrayList<>(coinListResponse.body().getCoins().keySet());
//                    }
//                })
//                .flatMap(coinListResponse -> Observable.just(coinListResponse.body().getCoins().keySet()))
//                .doOnNext(coinsNameList -> priceArg = android.text.TextUtils.join(",", coinsNameList))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .flatMap(price -> apiService2.getCoinPriceMulti(priceArg, "CAD"))
//                .subscribe(priceMultiResponse -> setupAdapter(coinsNameList, priceMultiResponse.priceList),
//                        error -> {
//                            Log.e("error", error.getMessage());
//                            Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                        });

        // Failed as well, too many files open for responses on prices
//        apiService.getCoinList()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .doOnNext(coinListResponse -> coinsNameList = new ArrayList<>(coinListResponse.getCoins().keySet()))
//                .flatMap(coinListResponse -> Observable.just(coinListResponse.getCoins().keySet()))
//                .flatMapIterable(baseDatas -> baseDatas)
//                .flatMap(coinsName ->
//                    apiService2.getCoinPrice(coinsName, "CAD")
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                            .doOnNext(cadPrice -> curCadPrice = cadPrice.getPrice())
//                            .doOnNext(cadPrice -> coinPriceMap.put(coinsName, cadPrice.getPrice())))
//                .toList()
//                .subscribe(success -> setupAdapter(coinsNameList, coinPriceMap),
//                        error -> Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show()
//                );

//        apiService.getCoinList()
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(coinListResponseResponse -> {
//                            if (coinListResponseResponse.isSuccessful()) {
//                                coinsNameList = new ArrayList<>(coinListResponseResponse.body().getCoins().keySet());
//                                Observable.just(coinsNameList)
//                                        .flatMapIterable(nameList -> nameList)
//                                        .flatMap(coinName -> apiService2.getCoinPrice(coinName, "CAD")
//                                                .doOnNext(cadPriceResponse -> Observable.just(new Pair<>(coinName, cadPriceResponse.body().getPrice()))
//                                                        .toList()
//                                                        .subscribe(s -> setupAdapter(s),
//                                                                error -> Log.e("error", error.getMessage())
//                                                        )
//                                                )
//                                        );
//
//                            } else {
//                                Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                            }
//                        }, error -> {
//                            Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                        }
//                );


////                        //Observable.just(new ArrayList<>(coinListResponse.body().getCoins().keySet()))
////                        //        .onErrorResumeNext(Observable.<ArrayList<String>>empty()))
////                .map(list -> coinsNameList)
////                .subscribe(success -> {
////                    Observable.just(coinsNameList)
////                            .flatMapIterable(coinNames -> coinNames)
////                            .flatMap(coinName ->
////                                    apiService2.getCoinPrice(coinName, "CAD")
////                                            .flatMap(cadPriceResponse -> Observable.just(cadPriceResponse.body().getPrice()))
////                                            .flatMap(price -> Observable.just(new Pair<>(coinName, price))))
////                            .toList()
////                            .observeOn(AndroidSchedulers.mainThread())
////                            .subscribe(listOfPairs -> setupAdapter(listOfPairs),
////                                    error -> Log.e("error", error.getStackTrace().toString()));
////                },
////                error -> {
////
//    });
//                .flatMap(coinName -> apiService2.getCoinPrice(coinName, "CAD")
//                                .doOnNext(cadPriceResponse -> cadPriceResponse.body().getPrice())
//                                .map(price -> coinName.)
//                )


//                .subscribe(coinListResponse -> {
//                    if (coinListResponse.isSuccessful()) {
//                        Map<String, Coin> coinsMap = coinListResponse.body().getCoins();
//                        coinsNameList = new ArrayList<>(coinsMap.keySet());
//
//                        Observable.fromIterable(coinsNameList)
//
//                                .forEach(item -> fetchAndStorePriceOfCoin(item))
//                                .notify();
//
//
//                    }
//                }, error -> {
//                    Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                });


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

//    void fetchAndStorePriceOfCoin(final String coinName) {
//        apiService2.getCoinPrice(coinName, "CAD")
//                .subscribe(cadPriceResponse -> {
//                    if (cadPriceResponse.isSuccessful()) {
//                        coinPriceMap.put(coinName, cadPriceResponse.body().getPrice());
//                    }
//                }, error -> {
//                    Log.e("CoinPriceAPIRespFailure", String.format("Failed to retrieve price of %s", coinName));
//                });
//    }

    private void setupAdapter(ArrayList<String> coinsNameList, HashMap<String, String> namePriceList) {
        this.runOnUiThread(() -> {
                adapter = new CoinsAdapter(coinsNameList, namePriceList);
                recyclerView.setAdapter(adapter);
        });
    }
}
