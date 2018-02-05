package kartiki.cryptocharts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_loader)
    ProgressBar progressBar;

    private ArrayList<Pair<String, Boolean>> coinsNameList = new ArrayList<>();
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
    }

    private void fetchCryptoCoinsData() {
        apiService.getCoinList()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(coinListResponse -> {
                            ArrayList<String> strings = new ArrayList<>(coinListResponse.body().getCoins().keySet());
                            for (int i = 0; i < strings.size(); ++i) {
                                coinsNameList.add(new Pair<>(strings.get(i), false));
                            }
                            setupAdapter(coinsNameList);
                        },
                        error -> {
                            Log.e("error", error.getMessage());
                            Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        });
    }


    private void setupAdapter(ArrayList<Pair<String, Boolean>> coinsNameList) {
        adapter = new CoinsAdapter(coinsNameList);
        this.runOnUiThread(() -> {
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
