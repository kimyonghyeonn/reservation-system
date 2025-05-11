package kr.co.wikibook.backend.reservation.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reservation {
    private Integer reserveId;          // 예약 아이디
    private Integer memberId;         // 회원 아이디
    private Integer roomId;      // 합주실 아이디
    private String startTime;      // 예약 시작 시간
    private String endTime;      // 예약 종료 시간
    private String status;        // 예약 상태
    private String memberList;        // 사용자 리스트
    private String reserveDate;        // 예약일(=사용일)
    private String remark;        // 예약일(=사용일)
    private String memberName;        // 등록자 명
    private String operTime;        // 등록자 명
    private LocalDateTime created; // 생성 일시
}
