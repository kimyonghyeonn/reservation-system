package kr.co.wikibook.backend.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpUtils {

    // 쿠키 입력
    public static void setCookie(HttpServletResponse res, String name, String value, int expSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        if (expSeconds > 0) {
            cookie.setMaxAge(expSeconds);
        }
        res.addCookie(cookie);
    }

    // 쿠키 값 조회
    public static String getCookieValue(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 쿠키 삭제
    public static void removeCookie(HttpServletResponse res, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }

    // 토큰 조회
    public static String getBearerToken(HttpServletRequest req) {
//        String authorization = req.getHeader("Authorization");
//
//        if (authorization != null) {
//            return authorization.replace("Bearer ", "");
//        }
        // ✅ 1순위: 재발급된 토큰이 있으면 그걸 반환
        Object regeneratedToken = req.getAttribute("regeneratedAccessToken");
        if (regeneratedToken != null) {
            return (String) regeneratedToken;
        }

        // ✅ 2순위: 기존 Authorization 헤더에서 추출
        String authorization = req.getHeader("Authorization");
        if (authorization != null) {
            return authorization.replace("Bearer ", "");
        }

        return null;
    }
}
