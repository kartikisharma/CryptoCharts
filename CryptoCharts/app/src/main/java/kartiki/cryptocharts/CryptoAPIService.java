package kartiki.cryptocharts;


import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kartiki on 2018-02-02.
 */

public interface CryptoAPIService {
    @GET("api/data/coinlist/")
    Observable<Response<CoinListResponse>> getCoinList();

    @GET("data/price")
    Observable<Response<CADPrice>> getCoinPrice(@Query("fsym") String coinName, @Query("tsyms") String currencyType);
}
