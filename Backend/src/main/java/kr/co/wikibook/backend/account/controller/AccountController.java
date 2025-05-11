package kr.co.wikibook.backend.account.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.wikibook.backend.account.service.AccountService;
import kr.co.wikibook.backend.account.service.BaseAccountService;
import kr.co.wikibook.backend.block.service.BlockService;
import kr.co.wikibook.backend.common.etc.AccountsConstant;
import kr.co.wikibook.backend.common.helper.TokenAccountHelper;
import kr.co.wikibook.backend.common.log.AccessLog.service.AccessLogService;
import kr.co.wikibook.backend.common.utils.HttpUtils;
import kr.co.wikibook.backend.common.utils.TokenUtils;
import kr.co.wikibook.backend.member.model.Members;
import kr.co.wikibook.backend.member.service.MembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    MembersService membersService;

    @Autowired
    TokenAccountHelper tokenAccountHelper;

    @Autowired
    BlockService blockService;

    @Autowired
    BaseAccountService baseAccountService;

    @Autowired
    AccessLogService accessLogService;

    @PostMapping("/account/join")
    public ResponseEntity<Members> join(@RequestBody Members members) {

        // 입력값이 없다면
        if(StringUtils.isEmpty(members.getEmail()) || StringUtils.isEmpty(members.getLoginId()) || StringUtils.isEmpty(members.getLoginPw())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 중복 로그인 아이디가 있다면
        if(membersService.getMemberById(members.getId()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        baseAccountService.join(members);

        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @PostMapping("/account/login")
    public ResponseEntity<String> login(HttpServletRequest req, HttpServletResponse res, @RequestBody Members members){
        if(StringUtils.isEmpty(members.getLoginId()) || StringUtils.isEmpty(members.getLoginPw())) {
            // 개인정보들은 모두 null 처리하며, user_id, action, created_at 만 기록한다
//            accessLogService.log("UNKNOWN", "LOGIN_FAIL", req.getRemoteAddr(), req.getHeader("User-Agent"), req.getSession().getId(), null);
            accessLogService.log("UNKNOWN", "LOGIN_FAIL", null, null, null, null);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Members LoginMember = baseAccountService.login(members);

        if(LoginMember == null) {
            accessLogService.log(members.getLoginId(), "LOGIN_FAIL", null, null, null, null);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String loginId = "UNKNOWN";
        if(LoginMember.getId() != null) {
            loginId = String.valueOf((LoginMember.getId()));
        }
        // ✅ 로그인 성공 로그 기록
        accessLogService.log(loginId, "LOGIN", null, null, null, null);


        // 회원 아이디
        Integer memberId = LoginMember.getId();

        // 엑세스 토큰 발급
        String accessToken = TokenUtils.createToken(AccountsConstant.ACCESS_TOKEN_NAME, AccountsConstant.MEMBER_ID_NAME, memberId, AccountsConstant.ACCESS_TOKEN_EXP_MINUTES);

        // 리프레시 토큰 발급
        String refreshToken = TokenUtils.createToken(AccountsConstant.REFRESH_TOKEN_NAME, AccountsConstant.MEMBER_ID_NAME, memberId, AccountsConstant.REFRESH_TOKEN_EXP_MINUTES);

        // 리프레시 토큰 쿠키 저장(유효시간을 0으로 입력해 웹 브라우저 종료시 삭제)
        HttpUtils.setCookie(res, AccountsConstant.REFRESH_TOKEN_NAME, refreshToken,0);


        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @GetMapping("/account/check")
    public ResponseEntity<?> check(HttpServletRequest req) {
        boolean isLoggedIn = tokenAccountHelper.isLoggedIn(req);
        if (isLoggedIn) {
            return new ResponseEntity<>(HttpStatus.OK); // 200
        } else {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED); // 401
        }
    }


    @GetMapping("/account/logout")
    public ResponseEntity<?> check(HttpServletRequest req, HttpServletResponse res) {
        tokenAccountHelper.logout(req, res);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/account/token")
    public ResponseEntity<?> regenerateToken(HttpServletRequest req) {
        String accessToken = "";
        String refreshToken = HttpUtils.getCookieValue(req, AccountsConstant.REFRESH_TOKEN_NAME);

        if(!StringUtils.isEmpty(refreshToken) && TokenUtils.isValid(refreshToken) && blockService.hasBlock(refreshToken)) {
            // 리프레시 토큰 내부 값 조회
            Map<String, Object> tokenBody = TokenUtils.getBody(refreshToken);

            // 리프레시 토큰의 회원 아이디 조회
            Integer memberId = (Integer) tokenBody.get(AccountsConstant.MEMBER_ID_NAME);

            // 엑세스토큰 발급
            accessToken = TokenUtils.createToken(AccountsConstant.ACCESS_TOKEN_NAME, AccountsConstant.MEMBER_ID_NAME, memberId, AccountsConstant.ACCESS_TOKEN_EXP_MINUTES);

        }

        if(accessToken.isEmpty()) {
            return new ResponseEntity<>("토큰 재발급 실패",HttpStatus.BAD_REQUEST);
        } else{
            return new ResponseEntity<>(accessToken, HttpStatus.OK);
        }
    }

    @GetMapping("/account/{loginId}")
    public ResponseEntity<?> check(HttpServletRequest req, @PathVariable String loginId) {
        return new ResponseEntity<>(tokenAccountHelper.isLoggedIn(req),HttpStatus.OK);
    }

    @GetMapping("/account/isDupl/{loginId}")
    public ResponseEntity<?> isDuplicated(HttpServletRequest req, @PathVariable String loginId) {
        return new ResponseEntity<>(accountService.isDuplicated(loginId),HttpStatus.OK);
    }

    @GetMapping("/account/myInfo")
    public ResponseEntity<?> getCurrentUserInfo(HttpServletRequest req) {
        String accessToken = HttpUtils.getBearerToken(req);

        if (!TokenUtils.isValid(accessToken)) {
            return new ResponseEntity<>("사용자 정보를 찾을 수 없습니다", HttpStatus.UNAUTHORIZED);
        }

        // 토큰 클레임 추출
        Map<String, Object> claims = TokenUtils.getBody(accessToken);
        String id =  claims.get("memberId").toString();

        if (id == null) {
            return new ResponseEntity<>("사용자 정보를 찾을 수 없습니다", HttpStatus.UNAUTHORIZED);
        }

        // DB에서 사용자 정보 조회
        Members member = accountService.findMemberById(id);

        if (member == null) {
            return new ResponseEntity<>("사용자가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PutMapping("/account/updateImage")
    public int updateMemberProfileImage(@RequestBody Members members){
        return accountService.updateMemberProfileImage(members);
    }

    @PostMapping("/account/findId")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String phoneNumber = payload.get("phoneNumber");

       Members user = this.accountService.findMemberByLoginIdAndPhone(name, phoneNumber);
        if (user != null && user.getLoginId() != null) {
            return ResponseEntity.ok(Map.of("loginId", user.getLoginId()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/account/verifyUser")
    public ResponseEntity<?> verifyUser(@RequestBody Map<String, String> payload) {
        String loginId = payload.get("loginId");
        String email = payload.get("email");
        String name = payload.get("name");
        String phoneNumber = payload.get("phoneNumber");

        boolean exists = false;
        Members members = this.accountService.verifyUser(loginId, email, name, phoneNumber);

        if(members != null) {
            exists = true;
        }

        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/account/resetPW")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String loginId = payload.get("loginId");
        String email = payload.get("email");
        String name = payload.get("name");
        String phoneNumber = payload.get("phoneNumber");

        boolean resetSuccess = this.baseAccountService.resetPassword(loginId, email, name, phoneNumber);


        if (resetSuccess) {
            return ResponseEntity.ok("비밀번호가 초기화되었습니다. 이메일을 확인해주세요.");
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/account/changePw")
    public ResponseEntity<?> changePassword(HttpServletRequest req, @RequestBody Map<String, String> payload) {
        String accessToken = HttpUtils.getBearerToken(req);

        // ① 토큰 유효성 검사
        if (!TokenUtils.isValid(accessToken)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        // ② 토큰에서 memberId 추출
        Map<String, Object> claims = TokenUtils.getBody(accessToken);
        String id = claims.get("memberId").toString();

        if (id == null) {
            return new ResponseEntity<>("사용자 정보를 찾을 수 없습니다", HttpStatus.UNAUTHORIZED);
        }

        // ③ 요청 비밀번호 유효성 검사
        String newPassword = payload.get("newPassword");
        if (newPassword == null || newPassword.length() < 8) {
            return new ResponseEntity<>("비밀번호는 8자리 이상이어야 합니다", HttpStatus.BAD_REQUEST);
        }

        // ④ 실제 비밀번호 변경 수행
        boolean result = baseAccountService.changePassword(id, newPassword);

        if (result) {
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경에 실패했습니다.");
        }
    }




}
