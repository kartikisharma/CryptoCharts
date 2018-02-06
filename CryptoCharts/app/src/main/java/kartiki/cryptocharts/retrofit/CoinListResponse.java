package kartiki.cryptocharts.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Kartiki on 2018-02-02.
 */

public class CoinListResponse {
    @SerializedName("Data")
    private Map<String, Coin> coins;

    public CoinListResponse(Map<String, Coin> coins) {
        this.coins = coins;
    }

    public Map<String, Coin> getCoins() {
        return coins;
    }
}
