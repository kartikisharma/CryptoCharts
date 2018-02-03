package kartiki.cryptocharts;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    private ArrayList<String> coinsNameList;
    private HashMap<String, String> coinPriceMap;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public CoinsAdapter(ArrayList<String> coinsNameList) {
        this.coinsNameList = coinsNameList;
        coinPriceMap = new HashMap<>();
    }

    @Override
    public void onBindViewHolder(CoinDataViewHolder holder, int position) {
        String coinName = coinsNameList.get(position);
        holder.coinName.setText(coinName);

        if (coinPriceMap.get(coinName) == null) {
            MainActivity.apiService2.getCoinPrice(coinName, "CAD")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(cadPrice -> {
                        // storing price mapping in HashMap for consequent binding
                        coinPriceMap.put(coinName, cadPrice.getPrice());

                        if (cadPrice.getPrice() != null) {
                            Runnable updatePrice = () ->
                                    holder.coinPrice.setText(String.format("$%s", cadPrice.getPrice()));

                            mainHandler.post(updatePrice);
                        }
                    }, error -> Log.e("cadPriceAPIRespFailure", error.getMessage()));
        } else if (coinPriceMap.get(coinName) != null) {
            holder.coinPrice.setText(String.format("$%s", coinPriceMap.get(coinName)));
        }
    }

    @Override
    public CoinDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_card_view, parent, false);
        return new CoinDataViewHolder(v);
    }


    @Override
    public int getItemCount() {
        return (coinsNameList != null) ? coinsNameList.size() : 0;
    }

    public static class CoinDataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coin_name)
        TextView coinName;

        @BindView(R.id.coin_price)
        TextView coinPrice;

//        @BindView(R.id.favourite_button)
//        ImageButton favButton;


        public CoinDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
