package kr.co.wikibook.backend.common.log.ErrorLog.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.wikibook.backend.common.log.ErrorLog.mapper.ErrorLogMapper;
import kr.co.wikibook.backend.common.log.ErrorLog.model.ErrorLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class ErrorLogService {
    private final ErrorLogMapper errorLogMapper;

    public void log(Exception e, HttpServletRequest request, String userId) {
        ErrorLog log = new ErrorLog();

        log.setLevel("ERROR");
        log.setMessage(e.getMessage());
        log.setStackTrace(getStackTraceAsString(e));
        log.setExceptionType(e.getClass().getSimpleName());
        log.setEndpoint(null);
        log.setServerName(null);
        log.setUserId(userId != null ? userId : "UNKNOWN");

        errorLogMapper.insertErrorLog(log);
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
