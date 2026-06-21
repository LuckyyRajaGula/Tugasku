package com.example.protrack.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface SubTaskDao {

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY id ASC")
    LiveData<List<SubTaskEntity>> getSubTasksForTask(int taskId);

    @Insert
    long insert(SubTaskEntity subTask);

    @Update
    void update(SubTaskEntity subTask);

    @Delete
    void delete(SubTaskEntity subTask);

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    void deleteSubTasksForTask(int taskId);
}
