package kr.co.wikibook.backend.room.controller;

import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.member.service.BaseMembersService;
import kr.co.wikibook.backend.member.service.MembersService;
import kr.co.wikibook.backend.room.model.Room;
import kr.co.wikibook.backend.room.service.BaseRoomService;
import kr.co.wikibook.backend.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    BaseRoomService baseRoomService;

    @GetMapping("/room/allRooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/room/{roomId}")
    public Room getRoomById(@PathVariable Integer roomId){
        return roomService.getRoomById(roomId);
    }

    @PostMapping("/room/create")
    public int createRoom(@RequestBody Room room){
        return roomService.createRoom(room);
    }

    @PutMapping("/room/update")
    public int updateRoom(@RequestBody Room room){
        return roomService.updateRoom(room);
    }

    @DeleteMapping("/room/delete/{roomId}")
    public int deleteRoom(@PathVariable Integer roomId){
        return roomService.deleteRoom(roomId);
    }
}
