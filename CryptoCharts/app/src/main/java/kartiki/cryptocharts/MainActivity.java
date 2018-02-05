package kartiki.cryptocharts;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {
    @BindView(R.id.crypto_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_loader)
    ProgressBar progressBar;

    CoinsAdapter adapter;

    // contains pairs of coin name and if it was favourited
    private ArrayList<Pair<String, Boolean>> coinNameAndFavouriteList = new ArrayList<>();

    @Override
    public void onInternetUnavailable() {
        // hide content UI
        // show No Internet Connection UI
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v("savedInstanceState", "found");
        }
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
                                coinNameAndFavouriteList.add(new Pair<>(strings.get(i), false));
                            }
                            setupAdapter(coinNameAndFavouriteList);
                        },
                        error -> {
                            if (error.getClass().equals(UnknownHostException.class) ||
                                    error.getClass().equals(SocketTimeoutException.class)) {
                                this.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                            }
                            else {
                                Log.e("error", error.getMessage());
                                Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    private void setupAdapter(ArrayList<Pair<String, Boolean>> coinsNameList) {
        adapter = new CoinsAdapter(coinsNameList, ((App) getApplication()).getCryptoPriceAPIService());
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
