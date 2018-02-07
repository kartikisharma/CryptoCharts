package kartiki.cryptocharts;

/**
 * Created by Kartiki on 2018-02-05.
 */

public interface InternetConnectionListener {
    boolean isInternetAvailable();
    void onInternetUnavailable();
}
