package com.phoneclone.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.phoneclone.database.entity.LocalApp;
import java.util.List;

@Dao
public interface AppDao {
    @Query("SELECT * FROM local_apps")
    List<LocalApp> getAll();
    
    @Query("SELECT * FROM local_apps WHERE id = :id")
    LocalApp getById(Long id);
    
    @Query("SELECT * FROM local_apps WHERE synced = 0")
    List<LocalApp> getUnsynced();
    
    @Insert
    void insert(LocalApp app);
    
    @Update
    void update(LocalApp app);
    
    @Delete
    void delete(LocalApp app);
}

