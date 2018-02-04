package kartiki.cryptocharts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
        holder.coinPrice.setText("0");
//        if (namePriceList.get(coinsNameList.get(position)) == null) {
//            MainActivity.apiService2.getCoinPrice(coinsNameList.get(position), "CAD")
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(Schedulers.newThread())
//                    .doOnNext(cadPrice -> holder.coinPrice.setText(cadPrice.getPrice()));
//        } else {
//            holder.coinPrice.setText(namePriceList.get(coinsNameList.get(position)));
//        }

//        holder.favButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // enablement
//                // move to top
//
//
//            }
//        });
    }

    @Override
    public CoinDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_card_view, parent, false);
        return new CoinDataViewHolder(v);
    }



    @Override
    public int getItemCount() {
        return (namePriceList != null) ? namePriceList.size() : 0;
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
