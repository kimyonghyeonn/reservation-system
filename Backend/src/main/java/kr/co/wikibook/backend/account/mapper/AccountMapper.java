package kr.co.wikibook.backend.account.mapper;

import kr.co.wikibook.backend.member.model.Members;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {

    int join(Members members);

    Members login(Members members);

    Members findMember(String loginId, String password);

    Members findMemberByLoginId(String loginId);

    Members findMemberById(String id);

    int getManagerFlagByMemberId(Integer id);

    boolean isDuplicated(String loginId);

    int updateMemberProfileImage(Members members);

    Members findMemberByLoginIdAndPhone(String name, String phoneNumber);

    Members verifyUser(String loginId, String email, String name, String phoneNumber);

    int updateMemberPassword(Members members);

    int updateResetPw(Members members);

    int updatePwWithResetFlag(Members members);
}
