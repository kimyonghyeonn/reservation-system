package kr.co.wikibook.backend.room.mapper;

import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.room.model.Room;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomMapper {

    List<Room> getAllRooms();

    Room getRoomById(Integer roomId);

    int createRoom(Room room);

    int updateRoom(Room room);

    int deleteRoom(Integer roomId);
}
