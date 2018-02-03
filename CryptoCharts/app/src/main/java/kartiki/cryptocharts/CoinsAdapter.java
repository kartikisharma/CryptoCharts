package kartiki.cryptocharts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinDataViewHolder> {
    private ArrayList<String> coinNameList;
    private HashMap<String, String> coinsPriceMap;


    public CoinsAdapter(ArrayList<String> coinNameList, HashMap<String, String> coinsPriceMap) {
        this.coinNameList = coinNameList;
        this.coinsPriceMap = coinsPriceMap;
    }

    @Override
    public void onBindViewHolder(CoinDataViewHolder holder, int position) {
        String coinName = coinNameList.get(position);
        String price = coinsPriceMap.get(coinName);

        holder.coinName.setText(coinName);
        if (price != null) holder.coinPrice.setText(price);
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
        return (coinNameList != null) ? coinNameList.size() : 0;
    }
}
