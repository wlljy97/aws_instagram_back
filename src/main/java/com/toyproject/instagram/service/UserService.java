package com.toyproject.instagram.service;

import com.toyproject.instagram.controller.AuthenticationController;
import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// UserService 클래스, 사용자 관리 서비스를 나타내며, 회원 가입 및 로그인과 관련된 기능을 수행

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 회원 가입을 처리하는 메서드
    public void signupUser(SignupReqDto signupReqDto) {
        Integer executeCount = userMapper.saveUser(signupReqDto.toUserEntity(passwordEncoder));
        System.out.println(executeCount);
    }

    // 로그인을 처리하는 메서드
    public void signinUser(SigninReqDto signinReqDto) {
        // 사용자 인증을 위한 인증 토큰을 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signinReqDto.getPhoneOrEmailOrUsername(), signinReqDto.getLoginPassword());
        // 인증 매니저를 사용하여 인증 토큰을 검증하고 인증 객체를 반환
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
}

// signinUser 메서드는 로그인을 처리합니다.
// signinReqDto를 사용하여 사용자 인증을 위한 인증 토큰을 생성하고,
// Spring Security의 AuthenticationManagerBuilder를 사용하여 인증 토큰을 검증하고 인증 객체를 반환합니다.
// 이를 통해 사용자가 로그인할 수 있습니다.
