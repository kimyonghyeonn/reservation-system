package kr.co.wikibook.backend.member.service;

import kr.co.wikibook.backend.member.mapper.MembersMapper;
import kr.co.wikibook.backend.member.model.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BaseMembersService implements MembersService {

    @Autowired
    MembersMapper membersMapper;

    @Override
    public List<Members> getAllMembers(){
        return membersMapper.getAllMembers();
    }

    @Override
    @Transactional
    public Members getMemberById(Integer id) {
        return membersMapper.getMemberById(id);
    }

    @Override
    @Transactional
    public int updateMember(Members members) {
        membersMapper.updateMember(members);
        return members.getId();
    }

    @Override
    @Transactional
    public int deleteMember(Integer id) {
       return membersMapper.deleteMember(id);
    }

}
