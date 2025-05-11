package kr.co.wikibook.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.wikibook.backend.common.log.ErrorLog.service.ErrorLogService;
import kr.co.wikibook.backend.common.utils.HttpUtils;
import kr.co.wikibook.backend.common.utils.TokenUtils;
import kr.co.wikibook.backend.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService;

//    기존 예외처리 주석처리
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException e) {
//        Map<String, String> errorResponse = new HashMap<>();
//        errorResponse.put("message", e.getMessage());
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//    }

    // ① 사용자 인증 실패 처리 (401)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(
            UnauthorizedException e,
            HttpServletRequest request
    ) {
        logError(e, request);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // ② 그 외 모든 예외 처리 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllException(
            Exception e,
            HttpServletRequest request
    ) {
        logError(e, request);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "서버 내부 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // ③ 에러 로그 공통 기록 메서드
    private void logError(Exception e, HttpServletRequest request) {
        String userId = extractUserIdFromToken(request);
        errorLogService.log(e, request, userId);
    }

    // ④ 토큰에서 사용자 ID 추출
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
