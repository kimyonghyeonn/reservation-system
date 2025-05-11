package kr.co.wikibook.backend.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class HashUtils {
    // 기존 비밀번호 해싱 처리하는 클래스
    // 일관적인 해싱(=단순한 패스워드 해싱) 처리로는 레인보우 테이블 공격에 취약하기에 솔트 사용
    // 솔트 : 데이터 원본을 그대로 해싱하는게 아닌 특정 값을 포함해서 해싱하는것 => 같은 값 입력해도 랜덤한 솔트 문자열 생성
    // 솔트생성
    public static String generalSalt(int size) {
        char[] resultArr = new char[size];
        Random rand = new Random();

        // 랜덤한 문자열을 만들기 위한 문자열
        String options = "# Add Your Info Here => Ex) ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()";

        for (int i = 0; i < size; i++) {
            resultArr[i] = options.toCharArray()[rand.nextInt(options.length())];
        }

        return new String(resultArr);
    }

    // 해시 데이터 생성
    public static String generateHash(String value, String salt) {
        try {

            MessageDigest md = MessageDigest.getInstance("# Add Your Info Here => Ex) SHA-256, MD5, SHA-384, ETC...");

            // 원본 값과 솔트 합치기
            String passwordSalted = value + salt;

            // 문자열 데이터 해싱
            byte[] hashBytes = md.digest(passwordSalted.getBytes(StandardCharsets.UTF_8));

            // 바이트 배열을 Base64 로 인코딩해서 반환
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해싱 중 오류가 발생했습니다.");
        }
    }
}
