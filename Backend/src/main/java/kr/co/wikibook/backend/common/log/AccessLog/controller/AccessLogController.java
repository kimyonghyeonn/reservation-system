package kr.co.wikibook.backend.common.log.AccessLog.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.wikibook.backend.common.log.AccessLog.model.LoginRequest;
import kr.co.wikibook.backend.common.log.AccessLog.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;

    @PostMapping("/loginLog")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpReq) {
        // 로그인 로직...
        String ip = null;
        String userAgent = httpReq.getHeader("User-Agent");
        String sessionId = "NO_SESSION";

        // 로그인 성공 시
        accessLogService.log(request.getUserId(), "LOGIN", ip, userAgent, sessionId, null);
        // 로그인 실패 로그는 남기지 않음 (Brute-force 공격 감지에 유용하기 때문)
        return ResponseEntity.ok().build();
    }

}
