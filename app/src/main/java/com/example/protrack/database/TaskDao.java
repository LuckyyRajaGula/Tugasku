package com.example.protrack.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<TaskEntity>> getAllTasks(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    LiveData<List<TaskEntity>> getActiveTasks(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1 ORDER BY updatedAt DESC")
    LiveData<List<TaskEntity>> getCompletedTasks(int userId);

    @Query("SELECT * FROM tasks WHERE priority = :priority AND userId = :userId ORDER BY createdAt DESC")
    LiveData<List<TaskEntity>> getTasksByPriority(TaskPriority priority, int userId);

    @Query("SELECT * FROM tasks WHERE status = :status AND userId = :userId ORDER BY createdAt DESC")
    LiveData<List<TaskEntity>> getTasksByStatus(TaskStatus status, int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND (title LIKE '%' || :searchQuery || '%' OR note LIKE '%' || :searchQuery || '%')")
    LiveData<List<TaskEntity>> searchTasks(String searchQuery, int userId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<TaskEntity> getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE id = :id")
    TaskEntity getTaskByIdSync(int id);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 0")
    LiveData<Integer> getActiveTaskCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    LiveData<Integer> getCompletedTaskCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = :priority AND isCompleted = 0 AND userId = :userId")
    LiveData<Integer> getTaskCountByPriority(TaskPriority priority, int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    LiveData<Integer> getTotalTaskCount(int userId); // New query to get total task count

    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status AND userId = :userId")
    LiveData<Integer> getTaskCountByStatus(TaskStatus status, int userId); // New query to get count by status

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'IN_PROGRESS' AND userId = :userId")
    LiveData<Integer> getInProgressTaskCount(int userId);

    @Insert
    long insertTask(TaskEntity task);

    @Insert
    void insertTasks(TaskEntity... tasks);

    @Update
    void updateTask(TaskEntity task);

    @Delete
    void deleteTask(TaskEntity task);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteTaskById(int id);

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    void deleteCompletedTasks();

    @Query("DELETE FROM tasks")
    void deleteAllTasks();
}