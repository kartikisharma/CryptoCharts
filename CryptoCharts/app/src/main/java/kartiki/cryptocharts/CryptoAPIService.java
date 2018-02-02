package kartiki.cryptocharts;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Kartiki on 2018-02-02.
 */

public interface CryptoAPIService {
    @GET("api/data/coinlist/")
    Call<CoinListResponse> getCoinList();
}
