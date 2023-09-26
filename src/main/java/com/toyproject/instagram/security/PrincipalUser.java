package com.toyproject.instagram.security;

import com.toyproject.instagram.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class PrincipalUser implements UserDetails {

    private String phoneOrEmailOrUsername;
    private String password;

    public PrincipalUser(String phoneOrEmailOrUsername, String password) {
        this.phoneOrEmailOrUsername = phoneOrEmailOrUsername;
        this.password = password;
    }

    // 로그인 할 사용자의 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phoneOrEmailOrUsername;
    }

    // ↓ 하나라도 false 면 로그인이 되지 않음

    // 계정 만료(사용 기간이 정해진 계정 )
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 ( ex) 비밀번호 5번 틀리면 잠금 )
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 자격 증명 만료 (공공인증서, 공동인증서, 휴대폰 인증 등이 만료 되면 false(만료))
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 활성화 (회원가입을 한 후에 본인인증이 아직 되지 않은 경우)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
