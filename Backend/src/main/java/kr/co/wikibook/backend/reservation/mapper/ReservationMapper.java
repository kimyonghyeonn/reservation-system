package kr.co.wikibook.backend.reservation.mapper;

import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.reservation.model.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

    List<Reservation> getAllReservations();

    List<Reservation> getAllReservationsForManager();

    List<Reservation> getAllReservationsByMemberId(Integer memberId);

    List<Reservation> getAllReservationsByRoomId(Integer roomId);

    Reservation getReservationById(Integer reserveId);

    int createReservation(Reservation reservation);

    int updateReservation(Reservation reservation);

    int deleteReservation(Integer reserveId, Integer memberId);

    int deleteReservationByMemberId(Integer memberId);

    List<Reservation> checkIsReserved(String reserveDate, String startTime, String endTime, Integer roomId);

    List<Reservation> checkIsReservedForUpdate(String reserveDate, String startTime, String endTime, Integer roomId, Integer reserveId);

    boolean checkDuplicate(@Param("roomId") int roomId,
                           @Param("reserveDate") String reserveDate,
                           @Param("startTime") int startTime,
                           @Param("endTime") int endTime);
}
