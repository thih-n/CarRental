package com.carrental.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleTokenVerifier {

    // API của Google để kiểm tra Token
    private static final String GOOGLE_TOKEN_INFO_URL = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=";
    private static final String FIREBASE_API_KEY = "AIzaSyCW-"; 

    public static String getPhoneNumberFromToken(String idToken) {
        try {
            // Google yêu cầu xác thực qua Identity Toolkit API
            // URL: https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=[API_KEY]
            // Method: POST
            // Body: { "idToken": "[TOKEN_FROM_CLIENT]" }

            URL url = new URL(GOOGLE_TOKEN_INFO_URL + FIREBASE_API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"idToken\": \"" + idToken + "\"}";

            try(java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Parse JSON bằng thư viện GSON
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            
            // Firebase trả về mảng "users", lấy phần tử đầu tiên
            if (jsonObject.has("users")) {
                JsonObject userObj = jsonObject.getAsJsonArray("users").get(0).getAsJsonObject();
                if (userObj.has("phoneNumber")) {
                    return userObj.get("phoneNumber").getAsString(); // Trả về SĐT (+84...)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu token sai hoặc hết hạn
    }
}