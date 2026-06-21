package com.example.protrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.protrack.database.AppDatabase;
import com.example.protrack.database.UserDao;
import com.example.protrack.database.UserEntity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvToggleMode;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToggleMode = findViewById(R.id.tvToggleMode);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleAuthAction());
        tvToggleMode.setOnClickListener(v -> toggleMode());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            btnLogin.setText("MASUK");
            tvToggleMode.setText("Belum punya akun? Daftar");
        } else {
            btnLogin.setText("DAFTAR AKUN");
            tvToggleMode.setText("Sudah punya akun? Masuk");
        }
    }

    private void handleAuthAction() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 3) {
            etPassword.setError("Password minimal 3 karakter");
            etPassword.requestFocus();
            return;
        }

        if (isLoginMode) {
            executeLogin(username, password);
        } else {
            executeRegister(username, password);
        }
    }

    private void executeLogin(String username, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserDao userDao = AppDatabase.getDatabase(LoginActivity.this).userDao();
            UserEntity user = userDao.getUser(username, password);

            runOnUiThread(() -> {
                if (user != null) {
                    // Simpan data session di SharedPreferences
                    SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("userId", user.getId());
                    editor.putString("username", user.getUsername());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    // Beri feedback visual
                    Toast.makeText(LoginActivity.this, "Selamat datang kembali, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();

                    // Pindah ke MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void executeRegister(String username, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserDao userDao = AppDatabase.getDatabase(LoginActivity.this).userDao();
            UserEntity existingUser = userDao.getUserByUsername(username);

            runOnUiThread(() -> {
                if (existingUser != null) {
                    Toast.makeText(LoginActivity.this, "Username '" + username + "' sudah terdaftar!", Toast.LENGTH_SHORT).show();
                } else {
                    // Simpan user baru di background thread
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        String email = username.toLowerCase().replaceAll("\\s+", "") + "@sigmamail.com";
                        UserEntity newUser = new UserEntity(username, password, email);
                        userDao.insertUser(newUser);

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Pendaftaran akun berhasil! Silakan masuk.", Toast.LENGTH_LONG).show();
                            toggleMode(); // Kembali ke mode login
                            etPassword.setText(""); // Bersihkan password
                        });
                    });
                }
            });
        });
    }
}
