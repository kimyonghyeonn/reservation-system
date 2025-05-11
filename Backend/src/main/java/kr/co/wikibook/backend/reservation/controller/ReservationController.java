package kr.co.wikibook.backend.reservation.controller;

import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.member.service.BaseMembersService;
import kr.co.wikibook.backend.member.service.MembersService;
import kr.co.wikibook.backend.reservation.model.Reservation;
import kr.co.wikibook.backend.reservation.service.BaseReservationService;
import kr.co.wikibook.backend.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @Autowired
    BaseReservationService baseReservationService;

    @GetMapping("/reservation/allReservations")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/reservation/allReservationsForManager/{memberId}")
    public List<Reservation> getAllReservationsForManager(@PathVariable Integer memberId) {

        return baseReservationService.getAllReservationsForManager(memberId);
    }

    @GetMapping("/reservation/myReservation/{memberId}")
    public List<Reservation>  getAllReservationsByMemberId(@PathVariable Integer memberId){
        return reservationService.getAllReservationsByMemberId(memberId);
    }

    @GetMapping("/reservation/room/{roomId}")
    public List<Reservation>  getAllReservationsByRoomId(@PathVariable Integer roomId){
        return reservationService.getAllReservationsByRoomId(roomId);
    }

    @GetMapping("/reservation/{reserveId}")
    public Reservation getReservationById(@PathVariable Integer reserveId){
        return reservationService.getReservationById(reserveId);
    }

    @PostMapping("/reservation/create")
    public int createReservation(@RequestBody Reservation reservation){
        return baseReservationService.createReservation(reservation);
    }

    @PutMapping("/reservation/update")
    public int updateReservation(@RequestBody Reservation reservation){
        return baseReservationService.updateReservation(reservation);
    }

    @DeleteMapping("/reservation/delete/{reserveId}/{memberId}")
    public int deleteReservation(@PathVariable Integer reserveId, @PathVariable Integer memberId){
        return reservationService.deleteReservation(reserveId, memberId);
    }

    @DeleteMapping("/reservation/deleteByMemberId/{memberId}")
    public int deleteReservationByMemberId(@PathVariable Integer memberId){
        return reservationService.deleteReservationByMemberId(memberId);
    }

    @GetMapping("/reservation/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam int roomId,
            @RequestParam String reserveDate,
            @RequestParam int startTime,
            @RequestParam int endTime) {

        boolean isDuplicate = reservationService.checkDuplicate(roomId, reserveDate, startTime, endTime);
        return ResponseEntity.ok(isDuplicate);
    }


}
