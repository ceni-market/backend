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
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[Ceni Market] 이메일 인증 안내");

            // 이메일 본문 (HTML 형식)
            String content = "<h3>안녕하세요, Ceni Market입니다.</h3>" +
                    "<p>아래 인증 번호를 입력하여 인증을 완료해 주세요.</p>" +
                    "<h2>" + token + "</h2>" +
                    "<p>본 인증 번호는 5분간 유효합니다.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 중 오류가 발생했습니다.", e);
        }
    }
}