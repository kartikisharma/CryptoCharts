package kartiki.cryptocharts.retrofit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kartiki on 2018-02-02.
 */

@Entity(tableName = "coins", indices = {@Index(value = {"coin_name"}, unique = true)})
public final class Coin {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @SerializedName("Name")
    @ColumnInfo(name = "coin_name")
    private String coinName;

    @ColumnInfo(name = "is_favourite")
    private Boolean isFavourite;

    @ColumnInfo(name = "price")
    private String price;

    public Coin(String coinName) {
        this.coinName = coinName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coin) {
            return this.coinName.equals(((Coin) obj).getCoinName());
        } else return false;
    }

    @Ignore
    public Coin(String coinName, Boolean isFavourite) {
        this.coinName = coinName;
        this.isFavourite = isFavourite;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public String getCoinName() {
        return coinName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
