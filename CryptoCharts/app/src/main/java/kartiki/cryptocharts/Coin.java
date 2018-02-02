package kartiki.cryptocharts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kartiki on 2018-02-02.
 */

public class Coin {
    @SerializedName("Id")
    String id;

    @SerializedName("Name")
    String name;

    @SerializedName("CoinName")
    String coinName;

    @SerializedName("FullName")
    String fullName;
}
