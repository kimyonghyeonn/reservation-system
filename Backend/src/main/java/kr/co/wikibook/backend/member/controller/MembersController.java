package kr.co.wikibook.backend.member.controller;

import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.member.service.BaseMembersService;
import kr.co.wikibook.backend.member.service.MembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MembersController {

    @Autowired
    MembersService membersService;

    @Autowired
    BaseMembersService baseMembersService;

    @GetMapping("/members/allMembers")
    public List<Members> getAllMembers(){
        return membersService.getAllMembers();
    }

    @GetMapping("/members/{id}")
    public Members getMemberById(@PathVariable Integer id){
        return membersService.getMemberById(id);
    }

    @PutMapping("/members/update")
    public int updateMember(@RequestBody Members members){
        return baseMembersService.updateMember(members);
    }

    @DeleteMapping("/members/delete/{id}")
    public int deleteMember(@PathVariable Integer id){
        return baseMembersService.deleteMember(id);
    }
}
