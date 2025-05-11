package kr.co.wikibook.backend.common.log.ErrorLog.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ErrorLog {
    private Long id;
    private String level;
    private String message;
    private String stackTrace;
    private String exceptionType;
    private String endpoint;
    private String serverName;
    private String userId;
    private Timestamp createdAt;
}
