package com.example.bai9_1.api;

import com.example.bai9_1.models.ImageUpload;
import com.example.bai9_1.models.Message;
import com.example.bai9_1.models.Const;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceAPI {

    // Cập nhật BASE_URL theo URL mà thầy cung cấp
    String BASE_URL = "http://app.iotstar.vn:8081/appfoods/";

    // Retrofit instance
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Các API endpoints
    @Multipart
    @POST("updateimages.php")  // Đảm bảo URL trùng với API mà bạn muốn gọi
    Call<String> uploadImage(
            @Part("id") RequestBody id,
            @Part MultipartBody.Part image);

    // Lấy ServiceAPI từ Retrofit
    static ServiceAPI getServiceAPI() {
        return retrofit.create(ServiceAPI.class);
    }
}
