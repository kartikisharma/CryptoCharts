package kartiki.cryptocharts;

import java.util.HashMap;

/**
 * Created by Kartiki on 2018-02-04.
 */

public class PriceMultiResponse {
    HashMap<String, CADPrice> priceList;

    PriceMultiResponse(HashMap<String, CADPrice> priceList) {
        this.priceList =priceList;
    }

    public HashMap<String, CADPrice> getPriceList() {
        return priceList;
    }

    public String getPriceByCoinName(String coinName) {
        return priceList.get(coinName).getPrice();
    }
}
