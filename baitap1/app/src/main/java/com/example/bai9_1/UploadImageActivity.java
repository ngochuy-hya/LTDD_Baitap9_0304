package com.example.bai9_1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bai9_1.api.ServiceAPI;
import com.example.bai9_1.utils.RealPathUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageActivity extends AppCompatActivity {

    private ImageView imgSelected;
    private Button btnChoose, btnUpload;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    public static final String TAG = "UploadImageActivity";
    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // Ánh xạ view
        imgSelected = findViewById(R.id.imgSelected);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Chọn ảnh từ thư viện
        btnChoose.setOnClickListener(view -> checkPermissionAndOpenGallery());

        // Upload ảnh
        btnUpload.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mở thư viện ảnh
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imgSelected.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    // Kiểm tra quyền truy cập thư viện và mở nếu được cấp quyền
    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-12 (API 23-32)
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        } else {
            // Android < 6 (API < 23) - không cần yêu cầu quyền runtime
            openGallery();
        }
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // Upload ảnh lên server
    private void uploadImage() {
        progressDialog.show();

        // Giả sử id là 123, có thể lấy từ SharedPreferences hoặc truyền vào từ đâu đó
        String id = "123"; // Thay "123" bằng ID thực tế

        // Chuyển URI thành file
        String imagePath = RealPathUtil.getRealPath(this, imageUri);
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // Gửi yêu cầu upload với id và image
        RequestBody idRequestBody = RequestBody.create(MediaType.parse("text/plain"), id);

        ServiceAPI.getServiceAPI().uploadImage(idRequestBody, imagePart).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(UploadImageActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadImageActivity.this, "Upload thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "Upload failed: " + t.getMessage());
                Toast.makeText(UploadImageActivity.this, "Lỗi khi tải lên!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
