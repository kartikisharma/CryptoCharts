package kartiki.cryptocharts;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;
    private ArrayList<String> coinsNameList;
    private HashMap<String, String> coinPriceMap = new HashMap<>();
    private CoinsAdapter adapter;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.cryptocompare.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    CryptoAPIService apiService = retrofit.create(CryptoAPIService.class);

    Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/")
            .addConverterFactory(GsonConverterFactory.create());

    CryptoAPIService apiService2 = retrofitBuilder.build().create(CryptoAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchCryptoCoinsData();

        //setupAdapter();

        //progressBar.setVisibility(View.GONE);
        //recyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchPricesForCryptoCoins() {
        if (coinsNameList != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                coinsNameList.forEach((coinName) -> fetchAndStorePriceOfCoin(coinName));
            } else {
                Iterator<String> it = coinsNameList.iterator();
                while (it.hasNext()) {
                    String curCoinName = it.next();
                    fetchAndStorePriceOfCoin(curCoinName);
                }
            }

            setupAdapter();
        }

    }

    private void fetchCryptoCoinsData() {
        Call<CoinListResponse> call = apiService.getCoinList();

        call.enqueue(new Callback<CoinListResponse>() {
            @Override
            public void onResponse(Call<CoinListResponse> call, Response<CoinListResponse> response) {
                if (response.isSuccessful()) {
                    Map<String, Coin> coinsMap = response.body().getCoins();
                    coinsNameList = new ArrayList<>(coinsMap.keySet());
                    fetchPricesForCryptoCoins();
                } else {
                    Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CoinListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchAndStorePriceOfCoin(final String coinName) {
        Call<CADPrice> call = apiService2.getCoinPrice(coinName, "CAD");
        call.enqueue(new Callback<CADPrice>() {
            @Override
            public void onResponse(Call<CADPrice> call, Response<CADPrice> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPrice() != null) {
                    coinPriceMap.put(coinName, response.body().getPrice());
                } else {
                    Log.e("CoinPriceAPIRespFailure", String.format("Failed to retrieve price of %s", coinName));
                }
            }

            @Override
            public void onFailure(Call<CADPrice> call, Throwable t) {
                Log.e("CoinPriceAPIRespFailure", String.format("Failed to retrieve price of %s", coinName));
            }
        });
    }

    private void setupAdapter() {
        adapter = new CoinsAdapter(coinsNameList, coinPriceMap);
        recyclerView.setAdapter(adapter);
    }
}
