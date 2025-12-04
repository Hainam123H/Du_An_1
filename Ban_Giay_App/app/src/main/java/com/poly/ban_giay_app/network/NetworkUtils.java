package com.poly.ban_giay_app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private NetworkUtils() {
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    public static String extractErrorMessage(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            // Kiểm tra status code nếu có
            if (response != null) {
                int code = response.code();
                if (code == 404) {
                    return "Không tìm thấy endpoint này trên server. Vui lòng kiểm tra server API đã được khởi động lại chưa.";
                }
            }
            return "Có lỗi xảy ra. Vui lòng thử lại.";
        }
        ResponseBody errorBody = response.errorBody();
        try {
            String raw = errorBody.string();
            if (TextUtils.isEmpty(raw)) {
                // Kiểm tra status code
                int code = response.code();
                if (code == 404) {
                    return "Không tìm thấy endpoint. Vui lòng kiểm tra server API.";
                }
                return "Có lỗi xảy ra. Vui lòng thử lại.";
            }
            
            // Kiểm tra nếu response là HTML (thường là 404 page từ Express)
            if (raw.trim().startsWith("<!DOCTYPE") || raw.trim().startsWith("<html")) {
                int code = response.code();
                if (code == 404) {
                    // Trích xuất thông báo lỗi từ HTML nếu có
                    if (raw.contains("Cannot POST") || raw.contains("Cannot GET") || raw.contains("Cannot PUT") || raw.contains("Cannot DELETE")) {
                        String method = response.raw().request().method();
                        String path = response.raw().request().url().encodedPath();
                        return String.format("Không tìm thấy endpoint: %s %s. Vui lòng kiểm tra:\n1. Server API đã được khởi động lại chưa\n2. Routes đã được đăng ký đúng chưa", method, path);
                    }
                    return "Không tìm thấy endpoint này trên server. Vui lòng kiểm tra server API đã được khởi động lại chưa.";
                }
                return "Server trả về lỗi. Status code: " + response.code();
            }
            
            // Thử parse JSON
            try {
                JSONObject json = new JSONObject(raw);
                if (json.has("message")) {
                    return json.getString("message");
                }
                if (json.has("error")) {
                    return json.getString("error");
                }
                return raw;
            } catch (org.json.JSONException e) {
                // Không phải JSON, trả về raw message hoặc status code
                int code = response.code();
                if (code == 404) {
                    return "Không tìm thấy endpoint. Vui lòng kiểm tra server API đã được khởi động lại chưa.";
                }
                return "Lỗi từ server (HTTP " + code + "): " + raw;
            }
        } catch (Exception e) {
            Log.e(TAG, "extractErrorMessage: ", e);
            int code = response.code();
            if (code == 404) {
                return "Không tìm thấy endpoint này trên server. Vui lòng kiểm tra server API đã được khởi động lại chưa.";
            }
            return "Không thể kết nối máy chủ. Status code: " + code;
        } finally {
            try {
                errorBody.close();
            } catch (Exception ignored) {
            }
        }
    }
}
