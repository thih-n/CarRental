package com.carrental.entity;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

public class Email {

    private final String eFrom = "dongnguyen1968hldh@gmail.com";
    private final String ePass = "wbwp jjue wuqf zrua";

    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean sendEmail(String subject, String message, String to) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Authenticator au = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(eFrom, ePass);
                }
            };

            Session session = Session.getInstance(props, au);

            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.setFrom(new InternetAddress(eFrom));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(message, "text/html; charset=UTF-8");
            
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            System.out.println("Send email failed");
            e.printStackTrace();
            return false;
        }
    }

    // Gửi email quên mật khẩu
    public boolean sendForgotPasswordEmail(String to, String name, String resetToken) {
        String subject = "CarRental - Mã xác nhận đặt lại mật khẩu";
        String message = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Xin chào " + name + ",</h2>"
                + "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu của bạn.</p>"
                + "<p>Đây là mã xác nhận của bạn:</p>"
                + "<div style='background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 28px; letter-spacing: 8px; font-weight: bold; margin: 20px 0;'>"
                + resetToken
                + "</div>"
                + "<p>Mã này có hiệu lực trong 15 phút.</p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "<hr>"
                + "<p style='color: #666; font-size: 12px;'>CarRental Team</p>"
                + "</body>"
                + "</html>";
        return sendEmail(subject, message, to);
    }

    // Gửi email khóa tài khoản
    public boolean sendAccountLockedEmail(String to, String name, String reason) {
        String subject = "CarRental - Thông báo khóa tài khoản";
        String message = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Xin chào " + name + ",</h2>"
                + "<p>Tài khoản của bạn đã bị khóa tạm thời.</p>"
                + "<p><strong>Lý do:</strong> " + reason + "</p>"
                + "<p>Vui lòng liên hệ đội ngũ hỗ trợ để biết thêm chi tiết hoặc mở khóa tài khoản.</p>"
                + "<hr>"
                + "<p style='color: #666; font-size: 12px;'>CarRental Team</p>"
                + "</body>"
                + "</html>";
        return sendEmail(subject, message, to);
    }

    // Gửi email hủy chuyến
    public boolean sendTripCancelledEmail(String to, String name, String bookingId, String reason) {
        String subject = "CarRental - Thông báo hủy chuyến #" + bookingId;
        String message = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Xin chào " + name + ",</h2>"
                + "<p>Chuyến xe #" + bookingId + " của bạn đã bị hủy.</p>"
                + "<p><strong>Lý do hủy:</strong> " + reason + "</p>"
                + "<p>Nếu bạn đã thanh toán, chúng tôi sẽ hoàn tiền trong vòng 3-5 ngày làm việc.</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của CarRental.</p>"
                + "<hr>"
                + "<p style='color: #666; font-size: 12px;'>CarRental Team</p>"
                + "</body>"
                + "</html>";
        return sendEmail(subject, message, to);
    }
}
