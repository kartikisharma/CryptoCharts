package kartiki.cryptocharts;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    private List<Pair<String, String>> namePriceList;


    public CoinsAdapter(List<Pair<String, String>> namePriceList) {
        this.namePriceList = namePriceList;
    }

    @Override
    public void onBindViewHolder(CoinDataViewHolder holder, int position) {
        holder.coinName.setText(namePriceList.get(position).first);
        holder.coinPrice.setText(namePriceList.get(position).second);

//        new Thread(new Runnable() {
//            String coinPrice = coinPriceMap.get(coinName);
//            @Override
//            public void run() {
//
//                if (coinPrice == null) {
//                    MainActivity.fetchAndStorePriceOfCoin(coinName);
//                    coinPrice = coinPriceMap.get(coinName);
//                }
//                holder.coinPrice.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        holder.coinPrice.setText(coinPrice);
//                    }
//                });
//            }
//        }).start();
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
