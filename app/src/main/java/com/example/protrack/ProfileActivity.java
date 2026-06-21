package com.example.protrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.viewmodel.TaskViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvProfileName, tvProfileEmail;
    private TextView tvProfileTotalCount, tvProfileDoneCount;
    private Button btnLogout, btnViewAnalytics, btnExportBackup;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupViewModel();
        loadUserData();
        setupClickListeners();
        observeTaskStatistics();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileTotalCount = findViewById(R.id.tvProfileTotalCount);
        tvProfileDoneCount = findViewById(R.id.tvProfileDoneCount);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewAnalytics = findViewById(R.id.btnViewAnalytics);
        btnExportBackup = findViewById(R.id.btnExportBackup);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = pref.getString("username", "Pengguna");
        String email = pref.getString("email", "email@sigmamail.com");

        tvProfileName.setText(username);
        tvProfileEmail.setText(email);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> handleLogout());
        btnViewAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.protrack.AnalyticsActivity.class);
            startActivity(intent);
        });
        btnExportBackup.setOnClickListener(v -> exportDataToJSON());
    }

    private void exportDataToJSON() {
        taskViewModel.getAllTasks().observe(this, tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                Toast.makeText(this, "Tidak ada data tugas untuk diekspor", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                org.json.JSONArray jsonArray = new org.json.JSONArray();
                for (TaskEntity task : tasks) {
                    org.json.JSONObject obj = new org.json.JSONObject();
                    obj.put("title", task.getTitle());
                    obj.put("deadline", task.getDeadline());
                    obj.put("status", task.getStatus().name());
                    obj.put("note", task.getNote());
                    obj.put("priority", task.getPriority().name());
                    obj.put("isCompleted", task.isCompleted());
                    obj.put("category", task.getCategory());
                    jsonArray.put(obj);
                }
                String jsonString = jsonArray.toString(4);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "ProTrack Tasks Backup");
                intent.putExtra(Intent.EXTRA_TEXT, jsonString);
                startActivity(Intent.createChooser(intent, "Ekspor Cadangan Menggunakan"));

            } catch (org.json.JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal mengekspor data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeTaskStatistics() {
        // Mengamati jumlah total tugas
        taskViewModel.getTotalTaskCount().observe(this, count -> {
            if (count != null) {
                tvProfileTotalCount.setText(String.valueOf(count));
            } else {
                tvProfileTotalCount.setText("0");
            }
        });

        // Mengamati jumlah tugas yang selesai
        taskViewModel.getTaskCountByStatus(TaskStatus.DONE).observe(this, count -> {
            if (count != null) {
                tvProfileDoneCount.setText(String.valueOf(count));
            } else {
                tvProfileDoneCount.setText("0");
            }
        });
    }

    private void handleLogout() {
        // Hapus session dari SharedPreferences
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Anda telah keluar dari akun.", Toast.LENGTH_SHORT).show();

        // Redirect ke LoginActivity dan hapus tumpukan activity (clear stack)
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
