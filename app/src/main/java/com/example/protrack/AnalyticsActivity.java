package com.example.protrack;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.viewmodel.TaskViewModel;

public class AnalyticsActivity extends AppCompatActivity {

    private ImageView ivAnalyticsBack;
    private ProgressBar pbCircularProgress;
    private TextView tvCompletionPercent, tvCompletionSummary;
    private TextView tvCompletedTasksCount, tvPendingTasksCount;
    private TextView tvHighCount, tvMediumCount, tvLowCount;
    private ProgressBar pbHigh, pbMedium, pbLow;

    private TaskViewModel taskViewModel;

    private int totalCount = 0;
    private int completedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        initViews();
        setupViewModel();
        observeStatistics();

        ivAnalyticsBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        ivAnalyticsBack = findViewById(R.id.ivAnalyticsBack);
        pbCircularProgress = findViewById(R.id.pbCircularProgress);
        tvCompletionPercent = findViewById(R.id.tvCompletionPercent);
        tvCompletionSummary = findViewById(R.id.tvCompletionSummary);
        tvCompletedTasksCount = findViewById(R.id.tvCompletedTasksCount);
        tvPendingTasksCount = findViewById(R.id.tvPendingTasksCount);
        tvHighCount = findViewById(R.id.tvHighCount);
        tvMediumCount = findViewById(R.id.tvMediumCount);
        tvLowCount = findViewById(R.id.tvLowCount);
        pbHigh = findViewById(R.id.pbHigh);
        pbMedium = findViewById(R.id.pbMedium);
        pbLow = findViewById(R.id.pbLow);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void observeStatistics() {
        // Ambil total tugas
        taskViewModel.getTotalTaskCount().observe(this, count -> {
            totalCount = count != null ? count : 0;
            updateCompletionProgress();
        });

        // Ambil tugas selesai
        taskViewModel.getTaskCountByStatus(TaskStatus.DONE).observe(this, count -> {
            completedCount = count != null ? count : 0;
            tvCompletedTasksCount.setText(String.valueOf(completedCount));
            updateCompletionProgress();
        });

        // Ambil tugas pending (sumber hitungan pending = total - completed)
        taskViewModel.getTaskCountByStatus(TaskStatus.PENDING).observe(this, count -> {
            int pending = count != null ? count : 0;
            // Ambil in_progress juga
            taskViewModel.getInProgressTaskCount().observe(this, progressCount -> {
                int totalPendingOrProgress = pending + (progressCount != null ? progressCount : 0);
                tvPendingTasksCount.setText(String.valueOf(totalPendingOrProgress));
            });
        });

        // Hitung distribusi prioritas
        taskViewModel.getTaskCountByPriority(TaskPriority.HIGH).observe(this, count -> {
            int high = count != null ? count : 0;
            tvHighCount.setText(high + " Tugas");
            pbHigh.setMax(totalCount > 0 ? totalCount : 100);
            pbHigh.setProgress(high);
        });

        taskViewModel.getTaskCountByPriority(TaskPriority.MEDIUM).observe(this, count -> {
            int medium = count != null ? count : 0;
            tvMediumCount.setText(medium + " Tugas");
            pbMedium.setMax(totalCount > 0 ? totalCount : 100);
            pbMedium.setProgress(medium);
        });

        taskViewModel.getTaskCountByPriority(TaskPriority.LOW).observe(this, count -> {
            int low = count != null ? count : 0;
            tvLowCount.setText(low + " Tugas");
            pbLow.setMax(totalCount > 0 ? totalCount : 100);
            pbLow.setProgress(low);
        });
    }

    private void updateCompletionProgress() {
        if (totalCount == 0) {
            pbCircularProgress.setProgress(0);
            tvCompletionPercent.setText("0%");
            tvCompletionSummary.setText("Belum ada tugas yang dibuat.");
            return;
        }

        int percentage = (int) (((double) completedCount / totalCount) * 100);
        pbCircularProgress.setProgress(percentage);
        tvCompletionPercent.setText(percentage + "%");
        tvCompletionSummary.setText(completedCount + " dari " + totalCount + " tugas selesai.");
    }
}
