package cn.mijack.meme.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import cn.mijack.meme.model.HistoryEntity;

/**
 * @author admin
 * @date 2017/6/16
 */
@Database(entities = {HistoryEntity.class}, version = 3, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract HistoryDao historyDao();

}
