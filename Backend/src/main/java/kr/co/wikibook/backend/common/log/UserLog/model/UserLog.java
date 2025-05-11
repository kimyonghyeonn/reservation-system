package kr.co.wikibook.backend.common.log.UserLog.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserLog {
    private Long id;
    private String userId;
    private String action;
    private String target;
    private String result;
    private String httpMethod;
    private Integer statusCode;
    private String ip;
    private String sessionId;
    private Timestamp createdAt;
}
