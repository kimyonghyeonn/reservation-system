package kr.co.wikibook.backend.member.service;

import kr.co.wikibook.backend.member.model.Members;

import java.util.List;

public interface MembersService {

    List<Members> getAllMembers();

    Members getMemberById(Integer id);

    int updateMember(Members members);

    int deleteMember(Integer id);
}
