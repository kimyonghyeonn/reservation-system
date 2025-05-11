package kr.co.wikibook.backend.account.service;

import kr.co.wikibook.backend.account.mapper.AccountMapper;
import kr.co.wikibook.backend.common.utils.HashUtils;
import kr.co.wikibook.backend.common.utils.PasswordUtil;
import kr.co.wikibook.backend.mail.service.MailService;
import kr.co.wikibook.backend.member.model.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BaseAccountService implements AccountService {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    MailService mailService;

    @Override
    public int join(Members members) {
        String loginPW = members.getLoginPw();
        // 솔트 생성
        String loginPWSalt = HashUtils.generalSalt(16);

        // 입력 패스워드에 솔트 적용
        String loginPWSalted = HashUtils.generateHash(loginPW, loginPWSalt);
        members.setLoginPw(loginPWSalted);
        members.setLoginPwSalt(loginPWSalted);

        return accountMapper.join(members);
    }

    @Override
    public Members login(Members members) {

        Optional<Members> membersOptional = Optional.ofNullable(accountMapper.findMemberByLoginId(members.getLoginId()));
        if (membersOptional.isPresent()) {
            Members nowMember = membersOptional.get();

            // 솔트 조회
            String loginPWSalt = membersOptional.get().getLoginPw();

            // 입력 패스워드에 솔트 조회
            String loginPWSalted = HashUtils.generateHash(members.getLoginPw(), loginPWSalt);

            // 저장된 회원의 패스워드와 솔트값이 일치한다면
            if(nowMember.getLoginPw().equals(loginPWSalt)) {
                return nowMember;
            }
        }
        return null;
    }

    @Override
    public Members findMember(String loginId, String password) {
        return accountMapper.findMember(loginId, password);
    }

    @Override
    public boolean isDuplicated(String loginId) {
        boolean isDuplicated = accountMapper.isDuplicated(loginId);
        // loginId 가 이미 사용중인 ID가 아니라면
        if(isDuplicated) {
            return true;
        }
        // 이미 사용중인 ID라면
        return  false;
    }

    @Override
    public Members findMemberByLoginId(String loginId) {
        return accountMapper.findMemberByLoginId(loginId);
    }

    @Override
    public Members findMemberById(String id) {
        return accountMapper.findMemberById(id);
    }

    @Override
    public int getManagerFlagByMemberId(Integer id) {
        return accountMapper.getManagerFlagByMemberId(id);
    }

    @Override
    public int updateMemberProfileImage(Members members) {
        return accountMapper.updateMemberProfileImage(members);
    }

    @Override
    public Members findMemberByLoginIdAndPhone(String name, String phoneNumber) {
        return accountMapper.findMemberByLoginIdAndPhone(name, phoneNumber);
    }

    @Override
    public Members verifyUser(String loginId, String email, String name, String phoneNumber) {
        return accountMapper.verifyUser(loginId, email, name, phoneNumber);
    }

    @Override
    public int updateMemberPassword(Members members) {
        return accountMapper.updateMemberPassword(members);
    }

    @Override
    public int updateResetPw(Members members) {
        return accountMapper.updateResetPw(members);
    }

    public boolean resetPassword(String loginId ,String email, String name, String phoneNumber) {
        // 사용자 확인
        Members member = accountMapper.verifyUser(loginId, email, name, phoneNumber);
        if (member == null) return false;

        // ① 임시 비밀번호 생성 (8자리, 조건 만족)
        String tempPassword = PasswordUtil.generateResetPassword();

        // ② 솔트 생성
        String salt = HashUtils.generalSalt(16);

        // ③ 솔트 적용 해싱
        String hashedPassword = HashUtils.generateHash(tempPassword, salt);

        // ④ DB 업데이트
        member.setLoginPw(hashedPassword);
        member.setLoginPwSalt(salt);
        accountMapper.updateMemberPassword(member);

        // ⑤ 이메일 전송
        mailService.sendPasswordResetMail(email, tempPassword);

        // 메일발송 성공시 초기화 유무 true 설정
        member.setResetPw(true);
        accountMapper.updateResetPw(member);

        return true;
    }

    @Override
    public int updatePwWithResetFlag(Members members){
        return accountMapper.updatePwWithResetFlag(members);
    }

    public boolean changePassword(String memberId, String newPassword) {
        try {
            String salt = HashUtils.generalSalt(16);
            String hashedPassword = HashUtils.generateHash(newPassword, salt);

            Members member = new Members();
            member.setId(Integer.parseInt(memberId));
            member.setLoginPw(hashedPassword);
            member.setLoginPwSalt(salt);
            member.setResetPw(false); // reset_pw false로 변경

            accountMapper.updatePwWithResetFlag(member);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
