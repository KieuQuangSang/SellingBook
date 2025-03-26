package com.booknest.BookNest.controller;

import com.booknest.BookNest.service.AuthService;
import com.booknest.BookNest.model.User;
import com.booknest.BookNest.response.LoginResponse;
import com.booknest.BookNest.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;


    private static final String mailHost = "smtp-relay.brevo.com";
    private static final int mailPort = 587;
    private static final String mailUsername = "h5studiogl@gmail.com";
    private static final String mailPassword = "fScdnZ4WmEDqjBA1";

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = authService.login(username, password);
        return user.map(u -> {
            String token = jwtUtil.createToken(u.getUsername());
            return ResponseEntity.ok(new LoginResponse(token, u));
        }).orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        authService.resetPassword(username, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Logic for logout (e.g., invalidate session or token)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        Optional<User> user = authService.findByEmail(email);
        if (user.isPresent()) {
            String token = jwtUtil.createToken(user.get().getUsername());
            sendPasswordResetEmail(email, token);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = authService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestParam String token, @RequestParam String newPassword) {
        // Xác thực token
        String username = jwtUtil.validateTokenAndGetUsername(token);
        if (username == null) {
            return ResponseEntity.status(401).build(); // Token không hợp lệ
        }

        // Cập nhật mật khẩu
        authService.updatePassword(username, newPassword);
        return ResponseEntity.ok().build();
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    private void sendPasswordResetEmail(String email, String token) {
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        String subject = "Yêu cầu đặt lại mật khẩu";
        String content = "<p>Xin chào,</p>"
                + "<p>Bn đã yêu cầu đặt lại mật khẩu của mình.</p>"
                + "<p>Nhấn vào liên kết bên dưới để thay đổi mật khẩu của bạn:</p>"
                + "<a href=\"" + resetLink + "\">Thay đổi mật khẩu của tôi</a>"
                + "<br>"
                + "<p>Bỏ qua email này nếu bạn nhớ mật khẩu của mình, "
                + "hoặc bạn không thực hiện yêu cầu này.</p>"
                + "<p>Thông tin tài khoản: " + email + "</p>";

        try {
            HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setHostName(mailHost);
            htmlEmail.setSmtpPort(mailPort);
            htmlEmail.setStartTLSEnabled(true);
            htmlEmail.setAuthentication(mailUsername, mailPassword);
            htmlEmail.setFrom(mailUsername);
            htmlEmail.setSubject(subject);
            htmlEmail.setHtmlMsg(content);
            htmlEmail.addTo(email);
            
            htmlEmail.setCharset("UTF-8");

            htmlEmail.send();
            logger.info("Email đã được gửi thành công đến {}", email);
        } catch (EmailException e) {
            logger.error("Lỗi khi gửi email: ", e);
        }
    }
}

