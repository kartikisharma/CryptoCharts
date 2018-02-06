package kartiki.cryptocharts;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kartiki on 2018-02-02.
 */

@Entity(tableName = "coins")
public final class Coin {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @SerializedName("Name")
    @ColumnInfo(name = "coin_name")
    private String coinName;

    @ColumnInfo(name = "is_favourite")
    private Boolean isFavourite;

    public Coin(String coinName) {
        this.coinName = coinName;
        this.isFavourite = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coin) {
            return this.coinName.equals(((Coin)obj).getCoinName());
        }
        else return false;
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
}
