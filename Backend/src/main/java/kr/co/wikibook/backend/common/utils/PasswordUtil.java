package kr.co.wikibook.backend.common.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordUtil {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALL = UPPER + LOWER + DIGITS;

    private static final SecureRandom random = new SecureRandom();

    public static String generateResetPassword() {
        List<Character> passwordChars = new ArrayList<>();

        // 필수 조건: 대문자 1개, 소문자 1개, 숫자 1개
        passwordChars.add(randomChar(UPPER));
        passwordChars.add(randomChar(LOWER));
        passwordChars.add(randomChar(DIGITS));

        // 나머지 5자리는 랜덤으로 채움
        for (int i = 0; i < 5; i++) {
            passwordChars.add(randomChar(ALL));
        }

        // 순서를 섞어 예측 방지
        Collections.shuffle(passwordChars);

        // 문자 리스트를 문자열로 변환
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private static char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}
