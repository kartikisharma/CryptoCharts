package kartiki.cryptocharts;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kartiki on 2018-02-03.
 */

public class CADPrice {
    @NonNull
    @SerializedName("CAD")
    String price;

    public String getPrice() {
        return price;
    }
}
