package kartiki.cryptocharts.retrofit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kartiki on 2018-02-03.
 */

public class CADPrice {
    public static final String CurrencyType = "CAD";
    @NonNull
    @SerializedName(CurrencyType)
    String price;

    public String getPrice() {
        return price;
    }
}
