package com.toyproject.instagram.service;

import com.toyproject.instagram.controller.AuthenticationController;
import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.exception.SignupException;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// UserService 클래스, 사용자 관리 서비스를 나타내며, 회원 가입 및 로그인과 관련된 기능을 수행

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원 가입을 처리하는 메서드
    public void signupUser(SignupReqDto signupReqDto) {
        User user = signupReqDto.toUserEntity(passwordEncoder);
        String emailPattern = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$";
        String phonePattern = "^[0-9]{11}+$";

        Pattern emailRegex = Pattern.compile(emailPattern);
        Pattern phoneRegex = Pattern.compile(phonePattern);

        Matcher emailMatcher = emailRegex.matcher(signupReqDto.getPhoneOrEmail());
        Matcher phoneMatcher = phoneRegex.matcher(signupReqDto.getPhoneOrEmail());

        if(emailMatcher.matches()) {
            user.setEmail(signupReqDto.getPhoneOrEmail());
        }
        if(phoneMatcher.matches()) {
            user.setPhone(signupReqDto.getPhoneOrEmail());
        }

        checkDuplicated(user);
        userMapper.saveUser(user);

    }

    public void checkDuplicated(User user) {

        if(StringUtils.hasText(user.getPhone())) {
            if (userMapper.findUserByPhone(user.getPhone()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("phone", "이미 사용중인 연락처입니다.");
                throw new SignupException(errorMap);
            }
        }
        if(StringUtils.hasText(user.getEmail())) {
            if(userMapper.findUserByEmail(user.getEmail()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("email", "이미 사용중인 이메일입니다.");
                throw new SignupException(errorMap);
            }
        }
        if(StringUtils.hasText(user.getUsername())) {
           if(userMapper.findUserByUsername(user.getUsername()) != null) {
               Map<String, String> errorMap = new HashMap<>();
               errorMap.put("username", "이미 사용중인 사용자 이름 입니다.");
               throw new SignupException(errorMap);
           }
        }

    }

    // 로그인을 처리하는 메서드
    public String signinUser(SigninReqDto signinReqDto) {
        // 사용자 인증을 위한 인증 토큰을 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signinReqDto.getPhoneOrEmailOrUsername(), signinReqDto.getLoginPassword());
        // 인증 매니저를 사용하여 인증 토큰을 검증하고 인증 객체를 반환
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // JWT를 여기서 만들어 줄꺼임
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        return accessToken;
    }

    public Boolean authenticate(String token) {
        String accessToken = jwtTokenProvider.convertToken(token);
        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new JwtException("사용자 정보가 만료되었습니다. 다시 로그인하세요.");
        }
        return true;
    }
}

// signinUser 메서드는 로그인을 처리합니다.
// signinReqDto를 사용하여 사용자 인증을 위한 인증 토큰을 생성하고,
// Spring Security의 AuthenticationManagerBuilder를 사용하여 인증 토큰을 검증하고 인증 객체를 반환합니다.
// 이를 통해 사용자가 로그인할 수 있습니다.
