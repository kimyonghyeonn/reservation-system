package kr.co.wikibook.backend.common.utils;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.wikibook.backend.common.etc.AccountsConstant;
import kr.co.wikibook.backend.common.exception.UnauthorizedException;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

    private static final Key signKey;

    static {
        String secretKey = "# Add Your Info Here => Ex) SECURITY_KEY..."; // 실제 서비스시 반드시 다른 값 사용
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        signKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // 토큰 발급
    public static String createToken(String subject, String name, Object value, int expMinutes) { // ③
        // 만료 시간 설정
        Date expTime = new Date();

        // 분(minute)을 밀리초(millisecond)로 변환해 입력
        expTime.setTime(expTime.getTime() + 1000L * 60 * expMinutes);

        // 기본 정보 입력
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        // 클레임 입력
        /*클레임이란? payload 부분에 담을 정보의 한 부분을 의미하며, 키, 값 구조로 되어있음*/
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(name, value);

        // 토큰 발급
        JwtBuilder builder = Jwts.builder()
                .setHeader(headerMap)
                .setSubject(subject)
                .setExpiration(expTime)
                .addClaims(claims)
                .signWith(signKey, SignatureAlgorithm.HS256);

        return builder.compact();
    }

    public static boolean isValid(String token) { // ④ 토큰 상태가 유효한지 확인
        // 토큰 값이 있다면
        if (StringUtils.hasLength(token)) {
            try {
                Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
                return true;
            } catch (ExpiredJwtException e) { // 만료됨
                System.out.println("❗JWT 만료됨: " + e.getMessage());
            } catch (JwtException e) { // 유효하지 않음
                System.out.println("❗JWT 파싱 에러: " + e.getMessage());
            }
        }

        return false;
    }

    // 토큰 상태 검사
    public static String isExpired(String token) { // ④ 토큰 상태가 유효한지 확인
        // 토큰 값이 있다면
        if (StringUtils.hasLength(token)) {
            try {
                Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
                return token;
            } catch (ExpiredJwtException e) { // 만료됨
                System.out.println("❗JWT 만료됨: " + e.getMessage());
                return "expired";
            } catch (JwtException e) { // 유효하지 않음
                return "notValid";
            }
        } else{
            return "notExist";
        }
    }

    // 토큰 값 추출
    public static Map<String, Object> getBody(String token) { // ⑤ 토큰 내부 값 조회 메소드
        return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
    }

    public static String regenerateAccessTokenIfExpired(HttpServletRequest req) {
        // 1. Access Token 추출
        String accessToken = HttpUtils.getBearerToken(req);

        // 2. Access Token이 유효하다면 그대로 반환
        if (TokenUtils.isValid(accessToken)) {
            return accessToken;
        }

        // 3. Access Token이 만료 or 유효하지 않다면 → Refresh Token 확인
        String refreshToken = HttpUtils.getCookieValue(req, AccountsConstant.REFRESH_TOKEN_NAME);

        if (!StringUtils.hasText(refreshToken)) {
            System.out.println("❗리프레시 토큰이 없음");
            return null;
        }

        // 4. Refresh Token 유효성 확인
        if (TokenUtils.isValid(refreshToken)) {
            Map<String, Object> claims = TokenUtils.getBody(refreshToken);
            Integer memberId = (Integer) claims.get(AccountsConstant.MEMBER_ID_NAME);

            if (memberId != null) {
                // 5. 새로운 Access Token 발급
                String newAccessToken = TokenUtils.createToken(
                        AccountsConstant.ACCESS_TOKEN_NAME,
                        AccountsConstant.MEMBER_ID_NAME,
                        memberId,
                        AccountsConstant.ACCESS_TOKEN_EXP_MINUTES
                );
                System.out.println("✅ 새로운 Access Token 재발급 완료");
                return newAccessToken;
            } else {
                System.out.println("❗리프레시 토큰에서 memberId 추출 실패");
            }
        } else {
            System.out.println("❗리프레시 토큰이 유효하지 않음");
            throw new UnauthorizedException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }

        return null;
    }

}
