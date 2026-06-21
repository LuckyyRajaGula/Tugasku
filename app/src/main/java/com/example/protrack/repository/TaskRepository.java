package com.example.protrack.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.database.AppDatabase;
import com.example.protrack.database.TaskDao;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.database.SubTaskDao;
import com.example.protrack.database.SubTaskEntity;
import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private SubTaskDao subTaskDao;
    private LiveData<List<TaskEntity>> allTasks;
    private LiveData<List<TaskEntity>> activeTasks;
    private LiveData<List<TaskEntity>> completedTasks;
    private LiveData<Integer> activeTaskCount;
    private LiveData<Integer> completedTaskCount;
    private LiveData<Integer> totalTaskCount;
    private final LiveData<Integer> inProgressTaskCount;
    private int currentUserId;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        subTaskDao = db.subTaskDao();

        android.content.SharedPreferences pref = application.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);

        allTasks = taskDao.getAllTasks(currentUserId);
        activeTasks = taskDao.getActiveTasks(currentUserId);
        completedTasks = taskDao.getCompletedTasks(currentUserId);
        activeTaskCount = taskDao.getActiveTaskCount(currentUserId);
        completedTaskCount = taskDao.getCompletedTaskCount(currentUserId);
        totalTaskCount = taskDao.getTotalTaskCount(currentUserId);
        inProgressTaskCount = taskDao.getInProgressTaskCount(currentUserId);
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getActiveTasks() {
        return activeTasks;
    }

    public LiveData<List<TaskEntity>> getCompletedTasks() {
        return completedTasks;
    }

    public LiveData<Integer> getActiveTaskCount() {
        return activeTaskCount;
    }

    public LiveData<Integer> getCompletedTaskCount() {
        return completedTaskCount;
    }

    public LiveData<List<TaskEntity>> getTasksByPriority(TaskPriority priority) {
        return taskDao.getTasksByPriority(priority, currentUserId);
    }

    public LiveData<List<TaskEntity>> getTasksByStatus(TaskStatus status) {
        return taskDao.getTasksByStatus(status, currentUserId);
    }

    public LiveData<List<TaskEntity>> searchTasks(String searchQuery) {
        return taskDao.searchTasks(searchQuery, currentUserId);
    }

    public LiveData<TaskEntity> getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public LiveData<Integer> getTaskCountByPriority(TaskPriority priority) {
        return taskDao.getTaskCountByPriority(priority, currentUserId);
    }

    public LiveData<Integer> getTotalTaskCount() {
        return totalTaskCount;
    }

    public LiveData<Integer> getTaskCountByStatus(TaskStatus status) {
        return taskDao.getTaskCountByStatus(status, currentUserId);
    }

    public LiveData<Integer> getInProgressTaskCount() {
        return inProgressTaskCount;
    }

    // Database modification methods (must be called on background thread)
    public void insert(TaskEntity task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            task.setUserId(currentUserId);
            taskDao.insertTask(task);
        });
    }

    public void insertTasks(TaskEntity... tasks) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            for (TaskEntity task : tasks) {
                task.setUserId(currentUserId);
            }
            taskDao.insertTasks(tasks);
        });
    }

    public void update(TaskEntity task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            task.setUserId(currentUserId);
            taskDao.updateTask(task);
        });
    }

    public void delete(TaskEntity task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteTask(task);
        });
    }

    public void deleteById(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteTaskById(id);
        });
    }

    public void deleteCompletedTasks() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteCompletedTasks();
        });
    }

    public void deleteAllTasks() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteAllTasks();
        });
    }

    public void markTaskAsCompleted(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            TaskEntity task = taskDao.getTaskByIdSync(id); // Ambil task secara sinkron
            if (task != null) {
                task.setStatus(TaskStatus.DONE); // Set status, isCompleted akan otomatis diperbarui
                taskDao.updateTask(task); // Simpan perubahan
            }
        });
    }

    public void markTaskAsIncomplete(int id, TaskStatus newStatus) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            TaskEntity task = taskDao.getTaskByIdSync(id); // Ambil task secara sinkron
            if (task != null) {
                task.setStatus(newStatus); // Set status baru, isCompleted akan otomatis diperbarui
                taskDao.updateTask(task); // Simpan perubahan
            }
        });
    }

    public interface InsertCallback {
        void onInsertComplete(long taskId);
    }

    public void insertWithCallback(TaskEntity task, InsertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            task.setUserId(currentUserId);
            long taskId = taskDao.insertTask(task);
            if (callback != null) {
                callback.onInsertComplete(taskId);
            }
        });
    }

    public LiveData<List<SubTaskEntity>> getSubTasksForTask(int taskId) {
        return subTaskDao.getSubTasksForTask(taskId);
    }

    public void insertSubTask(SubTaskEntity subTask) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            subTaskDao.insert(subTask);
        });
    }

    public void updateSubTask(SubTaskEntity subTask) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            subTaskDao.update(subTask);
        });
    }

    public void deleteSubTask(SubTaskEntity subTask) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            subTaskDao.delete(subTask);
        });
    }
}