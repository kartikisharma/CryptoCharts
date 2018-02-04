package kartiki.cryptocharts;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    private HashMap<String, String> namePriceList;
    private ArrayList<String> coinsNameList;

    public CoinsAdapter(ArrayList<String> coinsNameList, HashMap<String, String> namePriceList) {
        this.coinsNameList = coinsNameList;
        this.namePriceList = namePriceList;
    }

    @Override
    public void onBindViewHolder(CoinDataViewHolder holder, int position) {
        holder.coinName.setText(coinsNameList.get(position));
        if (namePriceList.get(coinsNameList.get(position)) == null) {
            MainActivity.apiService2.getCoinPrice(coinsNameList.get(position), "CAD")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .doOnNext(cadPrice -> holder.coinPrice.setText(cadPrice.getPrice()));
        } else {
            holder.coinPrice.setText(namePriceList.get(coinsNameList.get(position)));
        }
    }

    @Override
    public CoinDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_card_view, parent, false);
        return new CoinDataViewHolder(v);
    }

    public static class CoinDataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coin_name)
        TextView coinName;

        @BindView(R.id.coin_price)
        TextView coinPrice;


        public CoinDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return (namePriceList != null) ? namePriceList.size() : 0;
    }
}
