package kr.co.wikibook.backend.mail.service;

public interface MailService {
    void sendPasswordResetMail(String toEmail, String tempPassword);
}
