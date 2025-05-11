package kr.co.wikibook.backend.room.service;

import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.room.model.Room;

import java.util.List;

public interface RoomService {

    List<Room> getAllRooms();

    Room getRoomById(Integer roomId);

    int createRoom(Room room);

    int updateRoom(Room room);

    int deleteRoom(Integer roomId);

}
