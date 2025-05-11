package kr.co.wikibook.backend.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.wikibook.backend.account.mapper.AccountMapper;
import kr.co.wikibook.backend.common.utils.HttpUtils;
import kr.co.wikibook.backend.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    @Autowired
    AccountMapper accountMapper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String accessToken = HttpUtils.getBearerToken(req);

        // ✅ 1. 유효한 액세스 토큰이면 통과
        if (TokenUtils.isValid(accessToken)) {
            return true;
        }

        // ✅ 2. 토큰이 만료된 경우
        if (TokenUtils.isExpired(accessToken).equals("expired")) {
            String newAccessToken = TokenUtils.regenerateAccessTokenIfExpired(req);

            if (newAccessToken != null) {
                // ✅ 2-1. 새 토큰을 응답 헤더에 포함시켜 클라이언트에 전달
                resp.setHeader("X-New-Access-Token", newAccessToken);
                req.setAttribute("regeneratedAccessToken", newAccessToken);
                return true;
            } else {
                // ✅ 2-2. 재발급 실패
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        // ✅ 3. 토큰이 없거나 유효하지 않은 경우
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }


}
