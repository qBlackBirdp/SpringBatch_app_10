package com.hys.exam.springbatch_app_10.app.handler;


import com.hys.exam.springbatch_app_10.app.security.dto.MemberContext;
import com.hys.exam.springbatch_app_10.util.Ut;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler { // 인증 성공시의 동작을 컨트롤
    private final RequestCache requestCache = new HttpSessionRequestCache();
    // 사용자가 인증 x 상태에서 접근하려고 했던 요청 기억 -> 인증 o -> 리다이렉트
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    // 인증 o -> 다른 URL로 보내

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException { // 인증 성공시 호출
        clearSession(request); // 로그인 실패시에 남아있는 에러 정보를 세션에서 제거
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        /**
         * prevPage가 존재하는 경우 = 사용자가 직접 /auth/login 경로로 로그인 요청
         * 기존 Session의 prevPage attribute 제거
         */
        String prevPage = (String) request.getSession().getAttribute("prevPage");
        if (prevPage != null) {
            request.getSession().removeAttribute("prevPage");
        } // 사용자가 로그인 하기 전의 페이지 => prevPage로 세션에 저장 되어 있는 경우 확인하고 삭제
        // 기본 URI
        String url = "/";
        /**
         * savedRequest 존재하는 경우 = 인증 권한이 없는 페이지 접근
         * Security Filter가 인터셉트하여 savedRequest에 세션 저장
         */
        if (savedRequest != null) { // 사용자가 보호된 페이지에 접근시에 사용자가 보냈던 요청이 savedRequest에 저장
            url = savedRequest.getRedirectUrl();
        } else if (prevPage != null && prevPage.length() > 0) {
            // 회원가입 -> 로그인으로 넘어온 경우 "/"로 redirect
            if (prevPage.contains("/member/join")) {
                url = "/"; // 회원가입 후 바로 로그인 하면 /(home)으로 리다이렉트
            } else {
                url = prevPage; // 다른 페이지에서 로그인 하면 직전 페이지로 리다이렉트
            }
        }
        MemberContext memberContext = (MemberContext) authentication.getPrincipal();
        url = Ut.url.modifyQueryParam(url, "msg", memberContext.getName() + "님 환영합니다.");
        redirectStrategy.sendRedirect(request, response, url);
    }

    // 로그인 실패 후 성공 시 남아있는 에러 세션 제거
    // 실패 상태가 성공 이후에 영향 x
    protected void clearSession(HttpServletRequest request) { // 
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
