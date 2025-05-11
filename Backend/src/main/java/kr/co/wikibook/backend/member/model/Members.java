package kr.co.wikibook.backend.member.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Members {
    private Integer id;          // 아이디
    private String name;         // 회원명
    private String loginId;      // 로그인 아이디
    private String loginPw;      // 로그인 패스워드
    private String loginPwSalt;      // 로그인 패스워드
    private String email;        // 이메일
    private String phone;        // 전화번호
    private String remark;       // 비고
    private String position;     // 악기 파트 (코드값)
    private String positionNm;     // 악기 파트 (코드명)
    private String phoneNumber;     // 악기 파트
    private String birthDay;     // 악기 파트
    private Boolean manager;     // 관리자 여부
    private String isManager;     // 관리자 여부
    private String createDt; // 생성 일시
    private String profileImage;
    private boolean resetPw;
    private boolean infoAgree; // 개인정보 처리방침 동의
    private LocalDateTime created; // 생성 일시
}
