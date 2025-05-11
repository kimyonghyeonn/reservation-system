package kr.co.wikibook.backend.common.log.AccessLog.service;

import kr.co.wikibook.backend.common.log.AccessLog.mapper.AccessLogMapper;
import kr.co.wikibook.backend.common.log.AccessLog.model.AccessLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogMapper accessLogMapper;

    public void log(String userId, String action, String ip, String userAgent, String sessionId, String location) {
        AccessLog log = new AccessLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setSessionId(sessionId);
        log.setLocation(location);

        accessLogMapper.insertAccessLog(log);
    }
}
