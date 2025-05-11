package kr.co.wikibook.backend.mail.service;

import kr.co.wikibook.backend.common.log.MailLog.mapper.PasswordResetLogMapper;
import kr.co.wikibook.backend.common.log.MailLog.model.PasswordResetLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private PasswordResetLogMapper passwordResetLogMapper;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;  // 발신자 (application.properties의 username)

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetMail(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        message.setSubject("[QWERTY] 비밀번호 초기화 안내");
        message.setText(
                "안녕하세요.\n\n" +
                        "요청하신 임시 비밀번호는 다음과 같습니다:\n\n" +
                        tempPassword + "\n\n" +
                        "로그인 후 반드시 비밀번호를 변경해주세요.\n\n" +
                        "감사합니다."
        );
        mailSender.send(message);

        // ✅ 메일 발송 후 DB에 기록
        PasswordResetLog log = new PasswordResetLog();
        log.setToEmail(toEmail);
        log.setTempPassword(tempPassword);
        log.setContent(message.getText()); // ✅ 본문 내용 저장
        log.setCreatedAt(LocalDateTime.now());

        passwordResetLogMapper.insertPasswordResetLog(log);
    }
}
