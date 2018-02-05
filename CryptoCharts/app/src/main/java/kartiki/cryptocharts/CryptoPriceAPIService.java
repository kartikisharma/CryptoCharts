package kartiki.cryptocharts;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kartiki on 2018-02-05.
 */

public interface CryptoPriceAPIService {
    String baseUrl = "https://min-api.cryptocompare.com/";
    @GET("data/price")
    Observable<CADPrice> getCoinPrice(@Query("fsym") String coinName,
                                      @Query("tsyms") String currencyType);
}
