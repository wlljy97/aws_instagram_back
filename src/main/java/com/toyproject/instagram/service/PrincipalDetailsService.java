package com.toyproject.instagram.service;

import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    // 이 메서드는 UserDetailsService 인터페이스에 필요한 것으로, 사용자의 사용자 이름으로 사용자 세부 정보를 로드하는 데 사용됩니다.
    @Override
    public UserDetails loadUserByUsername(String phoneOrEmailOrUsername) throws UsernameNotFoundException {
        System.out.println("아이디 넘오옴?" + phoneOrEmailOrUsername);

        User user = userMapper.findUserByPhone(phoneOrEmailOrUsername);
        // 전화 번호로 사용자를 찾아보려고 시도합니다.

        // 주어진 전화 번호와 일치하는 사용자를 찾은 경우, 전화번호와 암호로 PrincipalUser 객체를 생성합니다.
        if (user != null) {
            return new PrincipalUser(user.getPhone(), user.getPassword(), user.getAuthorities());

        }
        user = userMapper.findUserByEmail(phoneOrEmailOrUsername);
        if (user != null) {
            return new PrincipalUser(user.getEmail(), user.getPassword(), user.getAuthorities());
        }

        user = userMapper.findUserByUsername(phoneOrEmailOrUsername);
        if (user != null) {
            return new PrincipalUser(user.getUsername(), user.getPassword(), user.getAuthorities());
        }

        // 제공된 식별자와 일치하는 사용자가 없는 경우, 예외를 throw합니다
        throw new UsernameNotFoundException("잘못된 사용자 정보입니다. 다시 확인하세요.");

//        return null;
        }

    }

