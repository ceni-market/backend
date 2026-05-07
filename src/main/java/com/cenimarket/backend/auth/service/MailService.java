package com.cenimarket.backend.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationMail(String toEmail, String token) {

        String baseUrl = "http://localhost:8088";

        // 2. 인증 링크 조립 (Query String 방식)
        // 사용자가 클릭 시 GET 요청으로 전달됨
        String verificationLink = String.format(
                "%s/api/auth/email-verification/verify?email=%s&token=%s",
                baseUrl, toEmail, token
        );
        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; text-align: center;'>" +
                        "  <h2>Ceni Market 가입을 환영합니다!</h2>" +
                        "  <p>아래 버튼을 클릭하면 이메일 인증이 완료됩니다.</p>" +
                        "  <a href='%s' style='background-color: #4CAF50; color: white; padding: 15px 25px; " +
                        "  text-decoration: none; display: inline-block; border-radius: 5px; margin: 20px 0;'>" +
                        "    이메일 인증 완료하기" +
                        "  </a>" +
                        "  <p>이 링크는 발송 후 5분 동안만 유효합니다.</p>" +
                        "</div>",
                verificationLink
        );


        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[Ceni Market] 이메일 인증 안내");

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 중 오류가 발생했습니다.", e);
        }
    }
}