package kartiki.cryptocharts.retrofit.apiservices;


import io.reactivex.Observable;
import kartiki.cryptocharts.retrofit.CoinListResponse;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by Kartiki on 2018-02-02.
 */

public interface CryptoCoinListAPIService {
    String baseUrl = "https://www.cryptocompare.com/";

    @GET("api/data/coinlist/")
    Observable<Response<CoinListResponse>> getCoinList();
}
