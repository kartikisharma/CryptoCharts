package kartiki.cryptocharts;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kartiki on 2018-02-02.
 */

public interface CryptoAPIService {
    @GET("api/data/coinlist/")
    Call<CoinListResponse> getCoinList();

    @GET("data/price")
    Call<CADPrice> getCoinPrice(@Query("fsym") String coinName, @Query("tsyms") String currencyType);
}
