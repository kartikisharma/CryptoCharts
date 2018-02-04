package kartiki.cryptocharts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_loader)
    ProgressBar progressBar;

    private ArrayList<String> coinsNameList;
    public static HashMap<String, String> coinPriceMap = new HashMap<>();
    CoinsAdapter adapter;

    static Retrofit retrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
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
    }

    private void fetchCryptoCoinsData() {
//        apiService.getCoinList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(coinListResponse -> {
//                    if (coinListResponse.isSuccessful()) {
//                        coinsNameList = new ArrayList<>(coinListResponse.body().getCoins().keySet());
//                        setupAdapter(coinsNameList, coinPriceMap);
//                    }
//                }, error ->
//                        Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show());


        apiService.getCoinList()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .doOnNext(coinListResponse -> coinsNameList = new ArrayList<>(coinListResponse.body().getCoins().keySet()))
                .flatMap(coinListResponse -> Observable.just(coinListResponse.body().getCoins().keySet()))
                .flatMapIterable(baseDatas -> baseDatas)
                .flatMap(coinsName ->
                        apiService2
                                .getCoinPrice(coinsName, "CAD")
                                .flatMap(cadPrice -> Observable.just(new Pair<>(coinsName, cadPrice.getPrice())))
                                .doOnNext(pair -> coinPriceMap.put(pair.first, pair.second)))
                .toList()
                .subscribeOn(Schedulers.newThread())
                .subscribe(list -> setupAdapter(coinsNameList, coinPriceMap),
                        error -> {
                            Log.e("error", error.getMessage());
                            Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        });
    }


    private void setupAdapter(ArrayList<String> coinsNameList, HashMap<String, String> namePriceList) {
        adapter = new CoinsAdapter(coinsNameList, namePriceList);
        this.runOnUiThread(() -> {
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

//    @OnClick(R.id.favourite_button)
//    void enableButtonAndMoveToTop(ImageButton button) {
//        adapter.
//        adapter.getItemId()
//    }
}
