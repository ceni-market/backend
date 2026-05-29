package com.cenimarket.backend.auth.service;

import com.cenimarket.backend.auth.domain.VerificationPurpose;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    @Async
    public void sendVerificationMail(String toEmail, String token, VerificationPurpose purpose) {

        //String baseUrl = "http://localhost:8088";

        //배포환경
        String baseUrl = "https://api.ceni-market.site";

        // 1. 용도에 따른 문구 및 링크 설정
        String title;
        String description;
        String buttonText;
        String linkPath;

        if (purpose == VerificationPurpose.PASSWORDRESET) {
            title = "비밀번호 재설정 안내";
            description = "아래 버튼을 클릭하여 비밀번호 변경을 완료해 주세요.";
            buttonText = "비밀번호 변경하기";
            linkPath = "/api/auth/password-reset/verify"; // 비밀번호 변경 검증 엔드포인트
        } else {
            title = "Ceni Market 가입을 환영합니다!";
            description = "아래 버튼을 클릭하면 이메일 인증이 완료됩니다.";
            buttonText = "이메일 인증 완료하기";
            linkPath = "/api/auth/signup/verify"; // 회원가입 인증 엔드포인트
        }

        // 2. 인증 링크 조립
        String verificationLink = String.format(
                "%s%s?email=%s&token=%s",
                baseUrl, linkPath, toEmail, token
        );

        // 3. HTML 컨텐츠 구성
        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; text-align: center; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>" +
                        "  <h2 style='color: #333;'>%s</h2>" +
                        "  <p style='color: #666;'>%s</p>" +
                        "  <a href='%s' style='background-color: #4CAF50; color: white; padding: 15px 25px; " +
                        "  text-decoration: none; display: inline-block; border-radius: 5px; margin: 20px 0; font-weight: bold;'>" +
                        "    %s" +
                        "  </a>" +
                        "  <p style='color: #999; font-size: 12px;'>이 링크는 발송 후 5분 동안만 유효합니다.</p>" +
                        "</div>",
                title, description, verificationLink, buttonText
        );


        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[Ceni Market] " + title);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 중 오류가 발생했습니다.", e);
        }
    }
}