package kartiki.cryptocharts;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;
import kartiki.cryptocharts.retrofit.Coin;
import kartiki.cryptocharts.retrofit.apiservices.CryptoPriceAPIService;
import kartiki.cryptocharts.database.AppDatabase;

import static kartiki.cryptocharts.retrofit.CADPrice.CurrencyType;


/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    public static final int STARTING_INDEX_OF_COINS_LIST = 0;
    ArrayList<Coin> coinsList;
    int numOfFavouriteCoins;
    private HashMap<String, String> coinPriceMap;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private CryptoPriceAPIService cryptoPriceAPIService;
    private Context context;

    public CoinsAdapter(ArrayList<Coin> coinsList,
                        int numOfFavouriteCoins,
                        CryptoPriceAPIService cryptoPriceAPIService,
                        Context context) {
        this.context = context;
        this.coinsList = coinsList;
        coinPriceMap = new HashMap<>();
        this.numOfFavouriteCoins = numOfFavouriteCoins;
        this.cryptoPriceAPIService = cryptoPriceAPIService;
    }

    @Override
    public void onBindViewHolder(CoinDataViewHolder holder, int position) {
        String coinName = coinsList.get(holder.getAdapterPosition()).getCoinName();
        holder.coinName.setText(coinName);

        Boolean isFavourite = coinsList.get(holder.getAdapterPosition()).getFavourite();
        if (isFavourite == null || !isFavourite) {
            holder.favButton.setSelected(false);
        } else {
            holder.favButton.setSelected(true);
        }

        if (coinPriceMap.get(coinName) == null) {
            fetchPriceOfCoin(holder, coinName);
        } else {
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

    private void fetchPriceOfCoin(CoinDataViewHolder holder, String coinName) {
        cryptoPriceAPIService.getCoinCADPrice(coinName, CurrencyType)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(cadPrice -> {
                    // storing price mapping in HashMap for subsequent bindings
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
                            } else {
                                holder.coinPrice.setVisibility(View.INVISIBLE);
                            }
                        };

                        mainHandler.post(showNoPrice);
                    } else {
                        Log.e("cadPriceAPIRespFailure", error.getMessage());
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
        return (coinsList != null) ? coinsList.size() : 0;
    }

    void favouriteAndMoveToTop(CoinDataViewHolder holder) {
        int index = holder.getAdapterPosition();

        if (index == numOfFavouriteCoins) { //only modifying the data at current index
            coinsList.set(index,
                    new Coin(holder.coinName.getText().toString(), true));
            AppDatabase.getAppDatabase(context).coinDao().insertAll(coinsList.get(index));
            numOfFavouriteCoins++;
            notifyDataSetChanged();
        } else {
            coinsList.remove(index);
            notifyItemRemoved(index);

            coinsList.add(STARTING_INDEX_OF_COINS_LIST,
                    new Coin(holder.coinName.getText().toString(), true));
            AppDatabase.getAppDatabase(context).coinDao().insertAll(coinsList.get(0));
            notifyItemInserted(STARTING_INDEX_OF_COINS_LIST);
            numOfFavouriteCoins++;
            notifyDataSetChanged();
        }
    }

    private void unfavouriteMoveOutOfFavouriteRegion(CoinDataViewHolder holder, String coinName) {
        int index = holder.getAdapterPosition();
        AppDatabase.getAppDatabase(context).coinDao().delete(coinName);

        if (index == numOfFavouriteCoins) { //only modifying the data at current index
            coinsList.set(index, new Coin(holder.coinName.getText().toString(), false));
            numOfFavouriteCoins--;
            notifyDataSetChanged();
        } else {
            coinsList.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            numOfFavouriteCoins--;

            coinsList.add(numOfFavouriteCoins, new Coin(holder.coinName.getText().toString(), false));
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
