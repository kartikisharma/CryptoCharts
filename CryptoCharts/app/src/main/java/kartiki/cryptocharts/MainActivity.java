package kartiki.cryptocharts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;
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

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.cryptocompare.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    CryptoAPIService apiService = retrofit.create(CryptoAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchAndSetCryptoItems();
    }

    private void fetchAndSetCryptoItems() {
        Call<CoinListResponse> call = apiService.getCoinList();

        call.enqueue(new Callback<CoinListResponse>() {
            @Override
            public void onResponse(Call<CoinListResponse> call, Response<CoinListResponse> response) {
                if (response.isSuccessful()) {
                    Map<String, Coin> coinsList = response.body().getCoins();
                    // proceed with remaining calls and setup
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
}
