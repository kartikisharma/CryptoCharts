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
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {
    @BindView(R.id.recycler_container)
    RelativeLayout relativeLayout;

    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_loader)
    ProgressBar progressBar;

    CoinsAdapter adapter;


    // contains pairs of coin name and if it was favourited
//    private ArrayList<Pair<String, Boolean>> coinNameAndFavouriteList = new ArrayList<>();
    private ArrayList<Coin> coinArrayList = new ArrayList<>();

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
                            ArrayList<String> strings = new ArrayList<>(coinListResponse.body().getCoins().keySet());
                            for (int i = 0; i < strings.size(); ++i) {
                                coinArrayList.add(new Coin(strings.get(i)));
                            }
                            List<Coin> coins= AppDatabase.getAppDatabase(getApplicationContext())
                                    .coinDao().getCoindataByFavourite(true);
                            for (int i = 0; i < coins.size(); ++i) {
                                coinArrayList.remove(coins.get(i));
                                coinArrayList.add(0, coins.get(i));
                            }

                            setupAdapter(coinArrayList, coins.size());
                        },
                        error -> {
                            if (error.getClass().equals(UnknownHostException.class) ||
                                    error.getClass().equals(SocketTimeoutException.class)) {
                                this.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                                Snackbar mySnackbar = Snackbar.make(relativeLayout,
                                        "Network connection unavailable. Please try again later.", Snackbar.LENGTH_SHORT);
                                mySnackbar.show();
                            }
                            else {
                                Log.e("error", error.getMessage());
                                Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    private void setupAdapter(ArrayList<Coin> coinsNameList, int numOfFavouriteCoins) {
        adapter = new CoinsAdapter(coinsNameList, numOfFavouriteCoins, ((App) getApplication()).getCryptoPriceAPIService());
        this.runOnUiThread(() -> {
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }


    @Override
    public void onPause() {
        ((App) getApplication()).removeInternetConnectionListener();

        if (adapter != null) {
            AppDatabase.getAppDatabase(getApplicationContext()).coinDao().deleteAllCoins();

            // iterating from the end of favourites, to maintain order when app is relaunched
            for (int i = adapter.numOfFavouriteCoins - 1; i >= 0; --i) {
                AppDatabase.getAppDatabase(getApplicationContext())
                        .coinDao().insertAll(adapter.coinsList.get(i));
            }
        }

        super.onPause();
    }
}
