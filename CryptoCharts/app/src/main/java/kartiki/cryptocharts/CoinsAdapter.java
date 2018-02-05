package kartiki.cryptocharts;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    public static final int STARTING_INDEX_OF_COINS_LIST = 0;
    private ArrayList<Pair<String, Boolean>> coinsNameList;
    private HashMap<String, String> coinPriceMap;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private int numOfFavouriteCoins = 0;
    private CryptoPriceAPIService cryptoPriceAPIService;

    public CoinsAdapter(ArrayList<Pair<String, Boolean>> coinsNameList, CryptoPriceAPIService cryptoPriceAPIService) {
        this.coinsNameList = coinsNameList;
        coinPriceMap = new HashMap<>();
        this.cryptoPriceAPIService = cryptoPriceAPIService;
    }

    @Override
    public void onBindViewHolder(CoinDataViewHolder holder, int position) {
        String coinName = coinsNameList.get(holder.getAdapterPosition()).first;
        holder.coinName.setText(coinName);

        if (coinsNameList.get(holder.getAdapterPosition()).second) {
            holder.favButton.setSelected(true);
        } else if (holder.favButton.isSelected()) {
            holder.favButton.setSelected(false);
        }

        if (coinPriceMap.get(coinName) == null) {
            cryptoPriceAPIService.getCoinPrice(coinName, "CAD")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(cadPrice -> {
                        // storing price mapping in HashMap for consequent binding
                        coinPriceMap.put(coinName, cadPrice.getPrice());

                        if (cadPrice.getPrice() != null) {
                            Runnable updatePrice = () -> {
                                holder.coinPrice.setText(String.format("$%s", cadPrice.getPrice()));
                                holder.coinPrice.setVisibility(View.VISIBLE);
                            };

                            mainHandler.post(updatePrice);
                        }
                    }, error -> {
                        if (error instanceof IOException) {
                            Runnable showNoPrice = () -> {
                                if (coinPriceMap.get(coinName) != null) {
                                    holder.coinPrice.setText(String.format("$%s", coinPriceMap.get(coinName)));
                                    holder.coinPrice.setVisibility(View.VISIBLE);
                                }
                                else {
                                    holder.coinPrice.setVisibility(View.INVISIBLE);
                                }
                            };

                            mainHandler.post(showNoPrice);
                        } else {
                            Log.e("cadPriceAPIRespFailure", error.getMessage());
                        }
                    });
        } else if (coinPriceMap.get(coinName) != null) {
            holder.coinPrice.setText(String.format("$%s", coinPriceMap.get(coinName)));
            holder.coinPrice.setVisibility(View.VISIBLE);
        }

        holder.favButton.setOnClickListener(view -> {
            if (view.isSelected()) {
                unfavouriteMoveOutOfFavouriteRegion(holder, coinName);
            } else {
                favouriteAndMoveToTop(holder);
            }
        });
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

    void favouriteAndMoveToTop(CoinDataViewHolder coinDataViewHolder) {
        int index = coinDataViewHolder.getAdapterPosition();

        if (index == numOfFavouriteCoins) { //only modifying the data at current index
            coinsNameList.set(index, new Pair<>(coinDataViewHolder.coinName.getText().toString(), true));
            numOfFavouriteCoins++;
            notifyDataSetChanged();
        } else {
            coinsNameList.remove(index);
            notifyItemRemoved(index);

            coinsNameList.add(STARTING_INDEX_OF_COINS_LIST, new Pair<>(coinDataViewHolder.coinName.getText().toString(), true));
            notifyItemInserted(STARTING_INDEX_OF_COINS_LIST);
            numOfFavouriteCoins++;
            notifyDataSetChanged();
        }
    }

    private void unfavouriteMoveOutOfFavouriteRegion(CoinDataViewHolder holder, String coinName) {
        int index = holder.getAdapterPosition();

        if (index == numOfFavouriteCoins) { //only modifying the data at current index
            coinsNameList.set(index, new Pair<>(coinName, false));
            numOfFavouriteCoins--;
            notifyDataSetChanged();
        } else {
            coinsNameList.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            numOfFavouriteCoins--;

            coinsNameList.add(numOfFavouriteCoins, new Pair<>(coinName, false));
            notifyItemInserted(numOfFavouriteCoins);
            notifyDataSetChanged();
        }
    }

    public static class CoinDataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coin_name)
        TextView coinName;

        @BindView(R.id.coin_price)
        TextView coinPrice;

        @BindView(R.id.favourite_button)
        ImageButton favButton;


        public CoinDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
