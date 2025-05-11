package kr.co.wikibook.backend.room.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Room {
    private Integer roomId;          // 합주실 아이디
    private String roomName;         // 합주실 명
    private String roomAddr;      // 합주실 주소
    private String addrDetail;
    private String phoneNumber;      // 합주실 연락처
    private String description;      // 합주실 설명
    private int capacity;        // 합주실 허용인원 수
    private String startTime;        // 영업 시작시간
    private String endTime;       // 영업 종료시간
    private String operTime;
    private String createDt; // 생성 일시
    private String imageBase64;
    private LocalDateTime created; // 생성 일시
}
