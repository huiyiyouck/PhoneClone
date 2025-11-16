package com.phoneclone.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.phoneclone.database.entity.LocalAppInstance;
import java.util.List;

@Dao
public interface AppInstanceDao {
    @Query("SELECT * FROM local_app_instances WHERE appId = :appId")
    List<LocalAppInstance> getByAppId(Long appId);
    
    @Query("SELECT * FROM local_app_instances WHERE synced = 0")
    List<LocalAppInstance> getUnsynced();
    
    @Insert
    void insert(LocalAppInstance instance);
    
    @Update
    void update(LocalAppInstance instance);
    
    @Delete
    void delete(LocalAppInstance instance);
}

