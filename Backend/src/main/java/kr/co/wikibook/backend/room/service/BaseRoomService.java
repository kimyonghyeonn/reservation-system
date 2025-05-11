package kr.co.wikibook.backend.room.service;

import kr.co.wikibook.backend.room.mapper.RoomMapper;
import kr.co.wikibook.backend.room.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseRoomService implements RoomService {

    @Autowired
    RoomMapper roomMapper;

    @Override
    public List<Room> getAllRooms(){
        return roomMapper.getAllRooms();
    }

   @Override
    public Room getRoomById(Integer roomId) {
        return roomMapper.getRoomById(roomId);
   }

   @Override
    public int createRoom(Room room) {
        return roomMapper.createRoom(room);
   }

   @Override
    public int updateRoom(Room room) {
        return roomMapper.updateRoom(room);
   }

   @Override
    public int deleteRoom(Integer roomId) {
        return roomMapper.deleteRoom(roomId);
   }


}
