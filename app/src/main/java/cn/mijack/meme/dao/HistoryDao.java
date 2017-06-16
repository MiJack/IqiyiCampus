package cn.mijack.meme.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import cn.mijack.meme.model.HistoryEntity;

/**
 * @author admin
 * @date 2017/6/16
 */
@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistory(HistoryEntity entity);


    @Query("SELECT * FROM history")
    LiveData<List<HistoryEntity>> loadAllHistory();
}
