package kr.co.wikibook.backend.common.config;

import kr.co.wikibook.backend.common.interceptor.ApiInterceptor;
import kr.co.wikibook.backend.common.interceptor.UserLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    ApiInterceptor apiInterceptor;

    @Autowired
    UserLogInterceptor userLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/account/isDupl/**",
                        "/api/account/login",
                        "/api/account/logout",
                        "/api/account/findId",
                        "/api/account/verifyUser",
                        "/api/account/resetPW",
                        "/api/account/join",
                        "/api/room/allRooms",                  // ✅ 방 목록 조회 허용
                        "/api/reservation/room/**");
        // ✅ 모든 API 요청을 로그로 기록
        registry.addInterceptor(userLogInterceptor)
                .addPathPatterns("/api/**");
    }

}
