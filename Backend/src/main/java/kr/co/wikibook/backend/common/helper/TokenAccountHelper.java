package kr.co.wikibook.backend.common.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.wikibook.backend.account.mapper.AccountMapper;
import kr.co.wikibook.backend.account.service.AccountService;
import kr.co.wikibook.backend.block.mapper.BlockMapper;
import kr.co.wikibook.backend.common.etc.AccountsConstant;
import kr.co.wikibook.backend.common.log.AccessLog.service.AccessLogService;
import kr.co.wikibook.backend.common.utils.HttpUtils;
import kr.co.wikibook.backend.common.utils.TokenUtils;
import kr.co.wikibook.backend.member.model.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Component;

@Component
public class TokenAccountHelper {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    AccessLogService accessLogService;

    public String setAccessToken(Members members, HttpServletRequest req, HttpServletResponse res) {
        Members isMember = accountMapper.findMember(members.getLoginId(), members.getLoginPw());

        // 회원 데이터가 없다면
        if (isMember == null) {
            return null;
        }

        // 회원 아이디
        Integer memberId = isMember.getId();

        // 엑세스 토큰 발급
        String accessToken = TokenUtils.createToken(AccountsConstant.ACCESS_TOKEN_NAME, AccountsConstant.MEMBER_ID_NAME, memberId, AccountsConstant.ACCESS_TOKEN_EXP_MINUTES);

        // 리프레시 토큰 발급
        String refreshToken = TokenUtils.createToken(AccountsConstant.REFRESH_TOKEN_NAME, AccountsConstant.MEMBER_ID_NAME, memberId, AccountsConstant.REFRESH_TOKEN_EXP_MINUTES);

        // 리프레시 토큰을 쿠키에 저장, 유시간을 0으로 입력해 웹브라우저 종료시 삭제
        HttpUtils.setCookie(res, AccountsConstant.REFRESH_TOKEN_NAME, refreshToken, 0);

        return accessToken;
    }

    // 엑세스 토큰 조회
    private String getAccessToken(HttpServletRequest req) {
        return HttpUtils.getBearerToken(req);
    }

    // 리프레시 토큰 조회 (쿠키에 저장, 관리하여 쿠키에서 조회)
    private String getRefreshToken(HttpServletRequest req) { // ⑦
        return HttpUtils.getCookieValue(req, AccountsConstant.REFRESH_TOKEN_NAME);
    }

    // 로그인 여부 학인
    public boolean isLoggedIn(HttpServletRequest req) {
        if(TokenUtils.isValid(getAccessToken(req))){
            return true;
        }
        // 리프레시 토큰 조회
        String refreshToken = getRefreshToken(req);

        // 차단된 리프레시 토큰이 있는 테이블 (=blocks) 를 조회하여 없는지 확인
        return TokenUtils.isValid(refreshToken) && !blockMapper.hasBlock(refreshToken);
    }

    public void logout(HttpServletRequest req, HttpServletResponse res) {

        // 1. userId 추출 (토큰에서)
        String accessToken = getAccessToken(req);
        String userId = "UNKNOWN";
        // 2. 토큰에서 id 추출
        if (TokenUtils.isValid(accessToken)) {
            try {
                Object id = TokenUtils.getBody(accessToken).get(AccountsConstant.MEMBER_ID_NAME);
                if (id != null) {
                    userId = id.toString();
                }
            } catch (Exception e) {
                e.printStackTrace(); // 또는 로그 출력
            }
        }

        // ③ 로그 기록
        accessLogService.log(userId, "LOGOUT", null, null, null, null);


        // 리프레시 토큰 조회
        String refreshToken = getRefreshToken(req);

        // 리프레시 토큰이 있다면
        if(refreshToken != null) {
            // 쿠키에서 삭제
            HttpUtils.removeCookie(res, AccountsConstant.REFRESH_TOKEN_NAME);

            if(!blockMapper.hasBlock(refreshToken)) {
                // 토큰 차단 데이터가 차단 토큰 테이블에 없다면 차단 토큰으로 추가 (이후 check 할때 차단 토큰 테이블에서 조회)
                blockMapper.addBlock(refreshToken);
            }
        }
    }
}
