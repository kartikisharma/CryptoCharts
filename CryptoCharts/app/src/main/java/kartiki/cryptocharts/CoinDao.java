package kartiki.cryptocharts;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

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

    @Delete
    void delete(Coin coin);

    @Query("DELETE FROM coins")
    void deleteAllCoins();

    @Update(onConflict = REPLACE)
    void updateCoin(Coin coin);
}