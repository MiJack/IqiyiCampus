package cn.mijack.meme.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;

import java.util.List;

import cn.mijack.meme.dao.HistoryDatabase;
import cn.mijack.meme.model.HistoryEntity;

/**
 * @author Mr.Yuan
 * @date 2017/6/17
 */
public class VideoHistoryViewModel extends AndroidViewModel {
    private HistoryDatabase database;
    private LiveData<List<HistoryEntity>> historyLiveData;

    public VideoHistoryViewModel(Application application) {
        super(application);
        database = Room.databaseBuilder(application,
                HistoryDatabase.class, "database-name").allowMainThreadQueries().build();
    }

    public LiveData<List<HistoryEntity>> loadVideos() {
        if (historyLiveData == null) {
            historyLiveData = database.historyDao().loadAllHistory();
        }
        return historyLiveData;
    }
}
