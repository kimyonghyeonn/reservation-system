package kr.co.wikibook.backend.common.log.UserLog.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.wikibook.backend.common.log.UserLog.mapper.UserLogMapper;
import kr.co.wikibook.backend.common.log.UserLog.model.UserLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogService {
    private final UserLogMapper userLogMapper;

    public void log(String userId, String action, String target, boolean isSuccess,
                    HttpServletRequest req, HttpServletResponse res) {

        UserLog log = new UserLog();
        log.setUserId(userId != null ? userId : "UNKNOWN");
        log.setAction(action);
        log.setTarget(target);
        log.setResult(isSuccess ? "SUCCESS" : "FAIL");
        log.setHttpMethod(req.getMethod());
        log.setStatusCode(res.getStatus());
        log.setIp(null);
        log.setSessionId(null);

        userLogMapper.insertUserLog(log);
    }

    public void insertUserLog(UserLog log) {
        userLogMapper.insertUserLog(log);
    }

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void archiveAndDeleteOldLogs() {
        int days = 30;
        try {
            log.info("▶️ user_log 이관 시작: {}일 초과 로그 아카이브", days);
            userLogMapper.archiveLogsOlderThan(days);
            userLogMapper.deleteLogsOlderThan(days);
            log.info("✅ user_log 이관 및 삭제 완료 ({}일 초과)", days);
        } catch (Exception e) {
            log.error("❌ user_log 아카이빙 중 에러 발생: {}", e.getMessage(), e);
        }
    }


}
