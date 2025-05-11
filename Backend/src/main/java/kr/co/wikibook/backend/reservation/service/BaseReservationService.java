package kr.co.wikibook.backend.reservation.service;

import kr.co.wikibook.backend.account.mapper.AccountMapper;
import kr.co.wikibook.backend.member.mapper.MembersMapper;
import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.member.service.MembersService;
import kr.co.wikibook.backend.reservation.mapper.ReservationMapper;
import kr.co.wikibook.backend.reservation.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class BaseReservationService implements ReservationService {

    @Autowired
    ReservationMapper reservationMapper;

    @Autowired
    AccountMapper accountMapper;

  @Override
    public List<Reservation> getAllReservations() {
      return reservationMapper.getAllReservations();
  }

  @Override
  public List<Reservation> getAllReservationsForManager(Integer memberId) {
    Integer manager = accountMapper.getManagerFlagByMemberId(memberId);
    if (manager != null && manager == 1) {
      // 관리자일 경우 전체 예약 반환
      return reservationMapper.getAllReservationsForManager();
    } else {
      // 비관리자일 경우 빈 목록 또는 예외 처리
      return Collections.emptyList();
    }
  }

  @Override
    public List<Reservation> getAllReservationsByMemberId(Integer memberId) {
      return reservationMapper.getAllReservationsByMemberId(memberId);
  }

  @Override
  public List<Reservation> getAllReservationsByRoomId(Integer roomId) {
    return reservationMapper.getAllReservationsByRoomId(roomId);
  }

  @Override
    public Reservation getReservationById(Integer reserveId) {
      return reservationMapper.getReservationById(reserveId);
  }

  @Override
    public int createReservation(Reservation reservation) {
      String reserveDate = reservation.getReserveDate();
      String StartTime = reservation.getStartTime();
      String endTime = reservation.getEndTime();
      Integer roomId = reservation.getRoomId();
        // 예약 중복 체크
      List<Reservation> reservations = reservationMapper.checkIsReserved(reserveDate, StartTime, endTime, roomId);
      if(reservations.size() > 0) {
          return HttpStatus.CONFLICT.value();
      }
      return reservationMapper.createReservation(reservation);
  }

  @Override
    public int updateReservation(Reservation reservation) {
    String reserveDate = reservation.getReserveDate();
    String StartTime = reservation.getStartTime();
    String endTime = reservation.getEndTime();
    Integer roomId = reservation.getRoomId();
    Integer reserveId = reservation.getReserveId();
    // 예약 중복 체크
    List<Reservation> reservations = reservationMapper.checkIsReservedForUpdate(reserveDate, StartTime, endTime, roomId, reserveId);
    if(reservations.size() > 0) {
      return HttpStatus.CONFLICT.value();
    }

      return reservationMapper.updateReservation(reservation);
  }

  @Override
    public int deleteReservation(Integer reserveId, Integer memberId) {
      return reservationMapper.deleteReservation(reserveId, memberId);
  }

  @Override
  public int deleteReservationByMemberId(Integer memberId) {
    return reservationMapper.deleteReservationByMemberId(memberId);
  }

  @Override
    public List<Reservation> checkIsReserved(String reserveDate, String startTime, String endTime, Integer roomId){
      return reservationMapper.checkIsReserved(reserveDate, startTime, endTime, roomId);
  }

  @Override
  public List<Reservation> checkIsReservedForUpdate(String reserveDate, String startTime, String endTime, Integer roomId, Integer reserveId){
    return reservationMapper.checkIsReservedForUpdate(reserveDate, startTime, endTime, roomId, reserveId);
  }

  @Override
  public boolean checkDuplicate(int roomId, String reserveDate, int startTime, int endTime) {
    return reservationMapper.checkDuplicate(roomId, reserveDate, startTime, endTime);
  }

}
