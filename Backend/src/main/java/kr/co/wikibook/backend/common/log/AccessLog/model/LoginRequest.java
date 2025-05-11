package kr.co.wikibook.backend.common.log.AccessLog.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoginRequest {
    private String userId;
    private String password;
}
