package com.example.protrack;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.protrack.database.SubTaskEntity;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.viewmodel.TaskViewModel;
import java.util.ArrayList;

public class TaskDetailActivity extends AppCompatActivity implements SubTaskAdapter.OnSubTaskActionListener {

    private TextView tvDetailTitle, tvDetailStatus, tvDetailPriority, tvDetailDeadline, tvDetailNote, tvDetailCategory;
    private EditText etSubTaskTitle;
    private Button btnAddSubTask;
    private RecyclerView rvSubTasks;
    private LinearLayout layoutCategory;
    private ImageView ivBack;

    private TaskViewModel taskViewModel;
    private SubTaskAdapter subTaskAdapter;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initViews();
        setupRecyclerView();
        setupViewModel();
        handleIntentData();

        ivBack.setOnClickListener(v -> finish());

        btnAddSubTask.setOnClickListener(v -> addNewSubTask());
    }

    private void initViews() {
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPriority = findViewById(R.id.tvDetailPriority);
        tvDetailDeadline = findViewById(R.id.tvDetailDeadline);
        tvDetailNote = findViewById(R.id.tvDetailNote);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        etSubTaskTitle = findViewById(R.id.etSubTaskTitle);
        btnAddSubTask = findViewById(R.id.btnAddSubTask);
        rvSubTasks = findViewById(R.id.rvSubTasks);
        layoutCategory = findViewById(R.id.layoutCategory);
        ivBack = findViewById(R.id.ivBack);
    }

    private void setupRecyclerView() {
        subTaskAdapter = new SubTaskAdapter(this);
        rvSubTasks.setLayoutManager(new LinearLayoutManager(this));
        rvSubTasks.setAdapter(subTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void handleIntentData() {
        if (getIntent() != null && getIntent().hasExtra("TASK_ID")) {
            taskId = getIntent().getIntExtra("TASK_ID", -1);
        } else if (getIntent() != null && getIntent().hasExtra("OPEN_TASK_ID")) {
            taskId = getIntent().getIntExtra("OPEN_TASK_ID", -1);
        }

        if (taskId != -1) {
            // Muat data tugas utama
            taskViewModel.getTaskById(taskId).observe(this, task -> {
                if (task != null) {
                    populateTaskDetails(task);
                } else {
                    Toast.makeText(this, "Tugas tidak ditemukan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            // Muat data sub-tugas
            taskViewModel.getSubTasksForTask(taskId).observe(this, subTasks -> {
                if (subTasks != null) {
                    subTaskAdapter.submitList(subTasks);
                }
            });
        } else {
            Toast.makeText(this, "ID Tugas tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void populateTaskDetails(TaskEntity task) {
        tvDetailTitle.setText(task.getTitle());
        tvDetailStatus.setText(task.getStatus().name());
        tvDetailPriority.setText(task.getPriority().name());
        tvDetailDeadline.setText(task.getDeadline());

        if (TextUtils.isEmpty(task.getNote())) {
            tvDetailNote.setText("Tidak ada catatan tambahan.");
        } else {
            tvDetailNote.setText(task.getNote());
        }

        // Set status badge color
        if (task.getStatus() == TaskStatus.DONE) {
            tvDetailStatus.setBackgroundTintList(getResources().getColorStateList(R.color.status_green));
        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            tvDetailStatus.setBackgroundTintList(getResources().getColorStateList(R.color.status_orange));
        } else {
            tvDetailStatus.setBackgroundTintList(getResources().getColorStateList(R.color.status_yellow));
        }

        // Set priority badge color
        if (task.getPriority() == TaskPriority.HIGH) {
            tvDetailPriority.setBackgroundTintList(getResources().getColorStateList(R.color.priority_high));
        } else if (task.getPriority() == TaskPriority.MEDIUM) {
            tvDetailPriority.setBackgroundTintList(getResources().getColorStateList(R.color.priority_medium));
        } else {
            tvDetailPriority.setBackgroundTintList(getResources().getColorStateList(R.color.priority_low));
        }

        // Set category badge
        if (task.getCategory() != null && !task.getCategory().isEmpty()) {
            layoutCategory.setVisibility(View.VISIBLE);
            tvDetailCategory.setText(task.getCategory());
        } else {
            layoutCategory.setVisibility(View.GONE);
        }
    }

    private void addNewSubTask() {
        String title = etSubTaskTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etSubTaskTitle.setError("Sub-tugas tidak boleh kosong");
            return;
        }

        SubTaskEntity subTask = new SubTaskEntity(taskId, title, false);
        taskViewModel.insertSubTask(subTask);
        etSubTaskTitle.setText("");
        Toast.makeText(this, "Sub-tugas ditambahkan!", Toast.LENGTH_SHORT).show();
    }

    // SubTaskAdapter.OnSubTaskActionListener implementation
    @Override
    public void onSubTaskToggle(SubTaskEntity subTask) {
        taskViewModel.updateSubTask(subTask);
    }

    @Override
    public void onSubTaskDelete(SubTaskEntity subTask) {
        taskViewModel.deleteSubTask(subTask);
        Toast.makeText(this, "Sub-tugas dihapus!", Toast.LENGTH_SHORT).show();
    }
}
