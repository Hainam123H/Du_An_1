package com.poly.ban_giay_app.network;

import android.content.Context;
import com.poly.ban_giay_app.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    // Giảm timeout từ 30s xuống 15s để tránh chờ quá lâu
    private static final long CONNECT_TIMEOUT_SECONDS = 15L;
    private static final long READ_TIMEOUT_SECONDS = 15L;
    private static final long WRITE_TIMEOUT_SECONDS = 15L;
    
    private static ApiService apiService;
    private static Context appContext;
    // Cache SessionManager để tránh tạo mới mỗi request
    private static com.poly.ban_giay_app.SessionManager cachedSessionManager;

    private ApiClient() {
        // no-op
    }

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        // Khởi tạo SessionManager một lần
        if (cachedSessionManager == null && appContext != null) {
            cachedSessionManager = new com.poly.ban_giay_app.SessionManager(appContext);
        }
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    apiService = buildRetrofit().create(ApiService.class);
                }
            }
        }
        return apiService;
    }
    
    // Reset ApiService để tái tạo với Base URL mới (dùng khi đổi Base URL)
    public static void resetApiService() {
        synchronized (ApiClient.class) {
            apiService = null;
        }
    }

    private static Retrofit buildRetrofit() {
        // Log Base URL để debug
        android.util.Log.d("ApiClient", "Base URL: " + BuildConfig.API_BASE_URL);
        
        // Chỉ log BODY khi ở chế độ debug để tăng hiệu suất
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            // Trong production, chỉ log lỗi hoặc tắt hoàn toàn
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        // Interceptor để thêm token vào header
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                // Sử dụng SessionManager đã cache thay vì tạo mới
                if (cachedSessionManager != null) {
                    String token = cachedSessionManager.getToken();
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        // Connection pool để tái sử dụng kết nối, tăng hiệu suất
        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectionPool(connectionPool)
                // Cho phép retry khi mất kết nối tạm thời
                .retryOnConnectionFailure(true)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
