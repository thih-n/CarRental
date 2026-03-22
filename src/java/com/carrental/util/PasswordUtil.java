package com.carrental.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// Đã xóa import jakarta.xml.bind... vì không cần thiết nữa

public class PasswordUtil {

    // Hàm băm mật khẩu (Hash) dùng Java thuần
    public static String hashPassword(String originalPassword) {
        try {
            // 1. Tạo instance SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // 2. Băm chuỗi
            md.update(originalPassword.getBytes());
            byte[] digest = md.digest();
            
            // 3. Chuyển đổi byte sang Hex (Cách 1 - Không cần thư viện ngoài)
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // %02x nghĩa là in ra 2 ký tự Hex viết thường
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Hàm main để test thử ngay lập tức
    public static void main(String[] args) {
        // Chạy thử với pass 123456
        String myPass = "123456";
        String hashed = hashPassword(myPass);
        
        System.out.println("Pass gốc: " + myPass);
        System.out.println("Pass mã hóa: " + hashed);
        
        // Kết quả đúng phải là: 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
    }
}