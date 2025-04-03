package com.example.bai9_1; // Thay bằng package của bạn

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvId, tvUsername, tvFullName, tvEmail, tvGender;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AnhXa();

        HienThiThongTin();


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this, UploadImageActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void AnhXa() {
        imgProfile = findViewById(R.id.imgProfile);
        tvId = findViewById(R.id.tvId);
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvGender = findViewById(R.id.tvGender);
        btnLogout = findViewById(R.id.btnLogout);
    }

    // Hàm hiển thị thông tin cá nhân
    private void HienThiThongTin() {
        // Có thể thay bằng dữ liệu từ SharedPreferences hoặc API
        tvId.setText("Mã ID: 10");
        tvUsername.setText("Tên đăng nhập: nguyenngochuy");
        tvFullName.setText("Họ tên: Nguyễn Ngọc Huy");
        tvEmail.setText("Email: nguyenhuypm1@gmail.com");
        tvGender.setText("Giới tính: Male");
    }
}