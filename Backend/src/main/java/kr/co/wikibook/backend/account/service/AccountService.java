package kr.co.wikibook.backend.account.service;

import kr.co.wikibook.backend.member.model.Members;

public interface AccountService {

    int join(Members members);

    Members login(Members members);

    Members findMember(String loginId, String password);

    Members findMemberByLoginId(String loginId);

    Members findMemberById(String loginId);

    int getManagerFlagByMemberId(Integer id);

    boolean isDuplicated(String loginId);

    int updateMemberProfileImage(Members members);

    Members findMemberByLoginIdAndPhone(String name, String phoneNumber);

    Members verifyUser(String loginId, String email, String name, String phoneNumber);

    int updateMemberPassword(Members members);

    int updateResetPw(Members members);

    int updatePwWithResetFlag(Members members);
}
