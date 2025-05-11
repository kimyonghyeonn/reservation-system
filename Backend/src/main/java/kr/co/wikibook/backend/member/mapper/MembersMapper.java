package kr.co.wikibook.backend.member.mapper;

import kr.co.wikibook.backend.member.model.Members;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MembersMapper {

    List<Members> getAllMembers();

    Members getMemberById(Integer id);

    int updateMember(Members members);

    int deleteMember(Integer id);
}
