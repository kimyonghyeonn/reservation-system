package kr.co.wikibook.backend.common.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.wikibook.backend.common.log.UserLog.service.UserLogService;
import kr.co.wikibook.backend.common.utils.HttpUtils;
import kr.co.wikibook.backend.common.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserLogInterceptor implements HandlerInterceptor {

    private final UserLogService userLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        int statusCode = response.getStatus();
        String ip = null;

        // ✅ 세션을 생성하지 않고 가져오기
        String sessionId = "NO_SESSION";

        String userId = extractUserIdFromToken(request);

        // ✅ 로그 서비스 호출 시 세션을 넘기지 않거나 안전하게 처리
        userLogService.log(
                userId,
                "API_CALL",
                uri,
                statusCode < 400,
                request,
                response
        );
    }

    private String extractUserIdFromToken(HttpServletRequest request) {

        try {
            String token = HttpUtils.getBearerToken(request);

            // ① 토큰이 없는 경우 (비회원 접근 등)
            if (token == null || token.isBlank()) {
                return "NO_TOKEN";
            }

            // ② 토큰이 존재하나 유효하지 않음
            if (!TokenUtils.isValid(token)) {
                return "INVALID_TOKEN";
            }

            // ③ 정상 토큰인 경우
            Map<String, Object> body = TokenUtils.getBody(token);
            Object id = body.get("memberId");

            return id != null ? id.toString() : "UNKNOWN";

        } catch (Exception e) {
            // ④ 예외 발생 시 대비
            return "UNKNOWN";
        }
    }

}
