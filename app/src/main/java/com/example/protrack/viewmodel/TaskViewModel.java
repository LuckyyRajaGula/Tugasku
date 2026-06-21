package com.example.protrack.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.database.SubTaskEntity;
import com.example.protrack.repository.TaskRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<TaskEntity>> allTasks;
    private LiveData<List<TaskEntity>> activeTasks;
    private LiveData<Integer> totalTaskCount; // New LiveData for total tasks
    private LiveData<Integer> inProgressTaskCount; // Sudah dideklarasikan, sekarang akan diinisialisasi

    private MutableLiveData<TaskPriority> selectedPriorityFilter = new MutableLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<String> selectedSortOrder = new MutableLiveData<>();

    private MediatorLiveData<List<TaskEntity>> filteredTasks = new MediatorLiveData<>();

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
        activeTasks = repository.getActiveTasks();
        totalTaskCount = repository.getTotalTaskCount();
        inProgressTaskCount = repository.getInProgressTaskCount();

        selectedPriorityFilter.setValue(null);
        searchQuery.setValue("");
        selectedSortOrder.setValue("date"); // Default sort by date

        filteredTasks.addSource(selectedPriorityFilter, filter -> applyFilter());
        filteredTasks.addSource(searchQuery, query -> applyFilter());
        filteredTasks.addSource(selectedSortOrder, sort -> applyFilter());
        filteredTasks.addSource(activeTasks, tasks -> applyFilter());
    }

    // Metode baru untuk menerapkan filter, pencarian, dan pengurutan
    private void applyFilter() {
        TaskPriority currentFilter = selectedPriorityFilter.getValue();
        String query = searchQuery.getValue();
        String sortOrder = selectedSortOrder.getValue();
        List<TaskEntity> currentActiveTasks = activeTasks.getValue();

        if (currentActiveTasks == null) {
            filteredTasks.setValue(new ArrayList<>());
            return;
        }

        List<TaskEntity> result = currentActiveTasks.stream()
                .filter(task -> currentFilter == null || task.getPriority() == currentFilter)
                .filter(task -> {
                    if (query == null || query.trim().isEmpty()) {
                        return true;
                    }
                    String q = query.toLowerCase();
                    boolean titleMatch = task.getTitle() != null && task.getTitle().toLowerCase().contains(q);
                    boolean noteMatch = task.getNote() != null && task.getNote().toLowerCase().contains(q);
                    return titleMatch || noteMatch;
                })
                .sorted((t1, t2) -> {
                    if ("deadline".equals(sortOrder)) {
                        if (t1.getDeadline() == null) return 1;
                        if (t2.getDeadline() == null) return -1;
                        return t1.getDeadline().compareTo(t2.getDeadline());
                    } else if ("priority".equals(sortOrder)) {
                        return Integer.compare(t2.getPriority().ordinal(), t1.getPriority().ordinal());
                    } else {
                        // Default: tanggal dibuat (terbaru dahulu)
                        return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                    }
                })
                .collect(Collectors.toList());

        filteredTasks.setValue(result);
    }

    // LiveData getters
    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getFilteredTasks() {
        return filteredTasks;
    }

    public LiveData<List<TaskEntity>> getActiveTasks() {
        return activeTasks;
    }

    public LiveData<TaskEntity> getTaskById(int id) {
        return repository.getTaskById(id);
    }

    public LiveData<Integer> getTaskCountByPriority(TaskPriority priority) {
        return repository.getTaskCountByPriority(priority);
    }

    public LiveData<Integer> getTotalTaskCount() {
        return totalTaskCount;
    }

    public LiveData<Integer> getTaskCountByStatus(TaskStatus status) {
        return repository.getTaskCountByStatus(status);
    }

    public LiveData<Integer> getInProgressTaskCount() {
        return inProgressTaskCount;
    }

    // Data modification methods
    public void insert(TaskEntity task) {
        repository.insert(task);
    }

    public void insertWithCallback(TaskEntity task, TaskRepository.InsertCallback callback) {
        repository.insertWithCallback(task, callback);
    }

    public void update(TaskEntity task) {
        repository.update(task);
    }

    public void delete(TaskEntity task) {
        repository.delete(task);
    }

    public void markTaskAsCompleted(int id) {
        repository.markTaskAsCompleted(id);
    }

    public void markTaskAsIncomplete(int id, TaskStatus status) {
        repository.markTaskAsIncomplete(id, status);
    }

    public void setTaskPriorityFilter(TaskPriority priority) {
        if (!Objects.equals(selectedPriorityFilter.getValue(), priority)) { // Hindari update jika filter sama
            selectedPriorityFilter.setValue(priority);
        }
    }

    public LiveData<List<SubTaskEntity>> getSubTasksForTask(int taskId) {
        return repository.getSubTasksForTask(taskId);
    }

    public void insertSubTask(SubTaskEntity subTask) {
        repository.insertSubTask(subTask);
    }

    public void updateSubTask(SubTaskEntity subTask) {
        repository.updateSubTask(subTask);
    }

    public void deleteSubTask(SubTaskEntity subTask) {
        repository.deleteSubTask(subTask);
    }

    public void setSearchQuery(String query) {
        if (!Objects.equals(searchQuery.getValue(), query)) {
            searchQuery.setValue(query);
        }
    }

    public void setSortOrder(String order) {
        if (!Objects.equals(selectedSortOrder.getValue(), order)) {
            selectedSortOrder.setValue(order);
        }
    }
}