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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;
import kartiki.cryptocharts.database.AppDatabase;
import kartiki.cryptocharts.retrofit.Coin;
import kartiki.cryptocharts.retrofit.apiservices.CryptoPriceAPIService;

import static kartiki.cryptocharts.retrofit.CADPrice.CurrencyType;


/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    public static final int STARTING_INDEX_OF_COINS_LIST = 0;
    ArrayList<Coin> coinsList;
    int numOfFavouriteCoins;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private CryptoPriceAPIService cryptoPriceAPIService;
    private Context context;

    public CoinsAdapter(ArrayList<Coin> coinsList,
                        int numOfFavouriteCoins,
                        CryptoPriceAPIService cryptoPriceAPIService,
                        Context context) {
        this.context = context;
        this.coinsList = coinsList;
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

        String price = coinsList.get(holder.getAdapterPosition()).getPrice();
        if (price == null) {
            fetchPriceOfCoin(holder, coinName);
        } else {
            holder.coinPrice.setText(String.format(context.getResources().getString(R.string.price), price));
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
                    coinsList.get(holder.getAdapterPosition()).setPrice(cadPrice.getPrice());

                    if (cadPrice.getPrice() != null) {
                        Runnable updatePrice = () -> {
                            holder.coinPrice.setText(
                                    String.format(context.getResources().getString(R.string.price),
                                    cadPrice.getPrice()));
                            holder.coinPrice.setVisibility(View.VISIBLE);

                            //favourited coins are already in db, but since their price was missing,
                            // it need to be updated on successful response
                            if (holder.favButton.isSelected()) {
                                AppDatabase.getAppDatabase(context).coinDao()
                                        .insertOrReplace(coinsList.get(holder.getAdapterPosition()));
                            }
                        };

                        mainHandler.post(updatePrice);
                    }
                }, error -> {
                    if (error instanceof IOException) {
                        Runnable showNoPrice = () -> {
                            if (coinsList.get(holder.getAdapterPosition()).getPrice() != null) {
                                holder.coinPrice.setText(String.format(context.getResources().getString(R.string.price),
                                        coinsList.get(holder.getAdapterPosition()).getPrice()));

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
        Coin favouritedCoin = coinsList.get(index);
        favouritedCoin.setFavourite(true);

        coinsList.remove(index);
        notifyItemRemoved(index);

        coinsList.add(STARTING_INDEX_OF_COINS_LIST, favouritedCoin);
        notifyItemInserted(STARTING_INDEX_OF_COINS_LIST);
        numOfFavouriteCoins++;
        notifyDataSetChanged();

        AppDatabase.getAppDatabase(context).coinDao()
                .insertOrReplace(coinsList.get(STARTING_INDEX_OF_COINS_LIST));
    }

    private void unfavouriteMoveOutOfFavouriteRegion(CoinDataViewHolder holder, String coinName) {
        int index = holder.getAdapterPosition();
        Coin unFavouritedCoin = coinsList.get(index);
        unFavouritedCoin.setFavourite(false);

        AppDatabase.getAppDatabase(context).coinDao().delete(coinName);

        coinsList.remove(holder.getAdapterPosition());
        notifyItemRemoved(holder.getAdapterPosition());
        numOfFavouriteCoins--;

        coinsList.add(numOfFavouriteCoins, unFavouritedCoin);
        notifyItemInserted(numOfFavouriteCoins);
        notifyDataSetChanged();
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
