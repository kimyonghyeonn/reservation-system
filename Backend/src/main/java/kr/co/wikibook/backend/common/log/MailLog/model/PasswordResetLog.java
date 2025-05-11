package kr.co.wikibook.backend.common.log.MailLog.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordResetLog {
    private Long id;
    private String toEmail;
    private String tempPassword;
    private String content; // ✅ 추가: 메일 본문
    private LocalDateTime createdAt;
}
