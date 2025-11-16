package com.phoneclone.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.phoneclone.database.dao.AppDao;
import com.phoneclone.database.dao.AppInstanceDao;
import com.phoneclone.database.entity.LocalApp;
import com.phoneclone.database.entity.LocalAppInstance;

@Database(entities = {LocalApp.class, LocalAppInstance.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDao();
    public abstract AppInstanceDao appInstanceDao();
}

