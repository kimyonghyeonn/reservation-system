package kr.co.wikibook.backend.common.log.AccessLog.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AccessLog {
    private Long id;
    private String userId;
    private String action;
    private String ip;
    private String userAgent;
    private String sessionId;
    private String location; // GeoIP 사용 시
    private Timestamp createdAt;
}
