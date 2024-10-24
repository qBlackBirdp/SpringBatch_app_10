package com.hys.exam.springbatch_app_10.app.security.service;

import com.hys.exam.springbatch_app_10.app.member.entity.Member;
import com.hys.exam.springbatch_app_10.app.member.repository.MemberRepository;
import com.hys.exam.springbatch_app_10.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService { // 사용자 인증 정보 조회
    // 데이터의 출처는 DB -> Spring Security에서 사용가능하게 변환
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER")); // MEMBER 권한을 부여/ 권한 객체는 SimpleGrantedAuthority
        return new MemberContext(member, authorities);
    }
}