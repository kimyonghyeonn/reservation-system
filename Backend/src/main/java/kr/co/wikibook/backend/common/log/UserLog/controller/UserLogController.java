package kr.co.wikibook.backend.common.log.UserLog.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.wikibook.backend.common.log.UserLog.model.UserLog;
import kr.co.wikibook.backend.common.log.UserLog.service.UserLogService;
import kr.co.wikibook.backend.common.utils.HttpUtils;
import kr.co.wikibook.backend.common.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserLogController {

    private final UserLogService userLogService;

    @PostMapping("/log/user")
    public ResponseEntity<Void> logUserAction(@RequestBody UserLog log, HttpServletRequest req) {
        // ✅ 사용자 ID 추출
        String userId = extractUserIdFromToken(req);
        String ip = null;
        String sessionId = "NO_SESSION";
        String endpoint = req.getRequestURI();

        // ✅ 필드 보완
        log.setUserId(userId);
        log.setIp(null);
        log.setSessionId(sessionId);
        log.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // ✅ target이 없을 경우 endpoint로 대체
        if (log.getTarget() == null || log.getTarget().isBlank()) {
            log.setTarget(endpoint);
        }

        // ✅ 저장
        userLogService.insertUserLog(log);

        return ResponseEntity.ok().build();
    }

    // ✅ 토큰에서 사용자 ID 추출
    private String extractUserIdFromToken(HttpServletRequest request) {
        String token = HttpUtils.getBearerToken(request);
        if (token != null && TokenUtils.isValid(token)) {
            Map<String, Object> body = TokenUtils.getBody(token);
            Object id = body.get("memberId");
            return id != null ? id.toString() : "UNKNOWN";
        }
        return "UNKNOWN";
    }

}
