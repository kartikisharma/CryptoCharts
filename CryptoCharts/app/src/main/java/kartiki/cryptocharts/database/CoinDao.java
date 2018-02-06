package kartiki.cryptocharts.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import kartiki.cryptocharts.retrofit.Coin;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


/**
 * Created by Kartiki on 2018-02-05.
 */

@Dao
public interface CoinDao {
    @Query("SELECT * FROM coins")
    List<Coin> getAll();

    @Query("SELECT * FROM coins WHERE is_favourite LIKE (:isFavorite)")
    List<Coin> getCoindataByFavourite(Boolean isFavorite);

    @Insert
    void insertAll(Coin... coins);

    @Query("DELETE FROM coins WHERE coin_name LIKE (:coinName)")
    void delete(String coinName);

    @Query("DELETE FROM coins")
    void deleteAllCoins();

    @Update(onConflict = REPLACE)
    void updateCoin(Coin coin);
}
