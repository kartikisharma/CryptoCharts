package kartiki.cryptocharts;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;
import kartiki.cryptocharts.database.AppDatabase;
import kartiki.cryptocharts.retrofit.Coin;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {
    @BindView(R.id.recycler_container)
    RelativeLayout relativeLayout;

    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_loader)
    ProgressBar progressBar;

    CoinsAdapter adapter;

    private ArrayList<Coin> coinArrayList;

    @Override
    public void onInternetUnavailable() {
        // hide content UI
        // show No Internet Connection UI
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((App) getApplication()).setInternetConnectionListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchCryptoCoinsData();
    }

    private void fetchCryptoCoinsData() {
        ((App) getApplication()).getCryptoCoinListAPIService().getCoinList()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(coinListResponse -> {
                            coinArrayList = new ArrayList<>(coinListResponse.body().getCoins().values());

                            // fetching favourited coins from database and replacing their instance from api fetched data
                            List<Coin> favouriteCoins = AppDatabase.getAppDatabase(getApplicationContext())
                                    .coinDao().getCoindataByFavourite(true);
                            for (int i = 0; i < favouriteCoins.size(); i++) {
                                coinArrayList.remove(favouriteCoins.get(i));
                                coinArrayList.add(0, favouriteCoins.get(i));
                            }

                            setupAdapter(coinArrayList, favouriteCoins.size());
                        },
                        throwable -> {
                            if (throwable instanceof IOException) {
                                this.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                                Snackbar.make(relativeLayout, R.string.network_connection_failed, Snackbar.LENGTH_SHORT)
                                        .show();
                            } else {
                                Log.e("throwable", throwable.getMessage());
                                Snackbar.make(relativeLayout, R.string.please_try_again, Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
    }

    private void setupAdapter(ArrayList<Coin> coinsNameList, int numOfFavouriteCoins) {
        adapter = new CoinsAdapter(coinsNameList, numOfFavouriteCoins,
                ((App) getApplication()).getCryptoPriceAPIService(), getApplicationContext());
        this.runOnUiThread(() -> {
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onPause() {
        ((App) getApplication()).removeInternetConnectionListener();
        super.onPause();
    }
}
