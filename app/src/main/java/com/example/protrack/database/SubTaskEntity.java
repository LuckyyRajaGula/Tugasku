package com.example.protrack.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "subtasks",
        foreignKeys = @ForeignKey(
                entity = TaskEntity.class,
                parentColumns = "id",
                childColumns = "taskId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("taskId")}
)
public class SubTaskEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int taskId;
    private String title;
    private boolean isCompleted;

    public SubTaskEntity() {}

    public SubTaskEntity(int taskId, String title, boolean isCompleted) {
        this.taskId = taskId;
        this.title = title;
        this.isCompleted = isCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
