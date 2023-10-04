package com.toyproject.instagram.security;

import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.service.PrincipalDetailsService;
import com.toyproject.instagram.service.UserService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

// JWT 토큰을 관리해주는 로직
@Component
public class JwtTokenProvider {

    private final Key key;
    private final PrincipalDetailsService principalDetailsService;
    private final UserMapper userMapper;

    // Autowired는 IoC 컨테이너에서 객체를 자동 주입
    // Value는 application.yml에서 변수 데이터를 자동 주입

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Autowired PrincipalDetailsService principalDetailsService,
                            @Autowired UserMapper userMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.principalDetailsService = principalDetailsService;
        this.userMapper = userMapper;
    }

    // JWT 토큰을 생성
    public String generateAccessToken(Authentication authentication) { // Authentication 매개변수로 받은 이유:
        String accessToken = null;

        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        // 만료 시간
        Date tokenExpiresDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24)); // 현재 날짜를 현재시간으로 가지고 옴

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject("AccessToken") // 제목 , User의 ID정보가 들어가 있어야한다.
                .setExpiration(tokenExpiresDate) // 만료기간
                .signWith(key, SignatureAlgorithm.HS256);   // key값


        User user = userMapper.findUserByPhone(principalUser.getUsername());
        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();
        }
        user = userMapper.findUserByEmail(principalUser.getUsername());
        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();
        }

        return jwtBuilder.claim("username", principalUser.getUsername()).compact();

    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String convertToken(String bearerToken) {
        String type = "Bearer ";
        // null인지 확인, 공백인지 확인 해줘야하는데 이것을 동시에 해주는 메소드가 'hasText'
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(type)) {
            return bearerToken.substring(type.length());
        }
        return "";
    }

    public Authentication getAuthentication(String accessToken) {
        Authentication authentication = null;
        String username = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken) // 암호화를 품
                .getBody()
                .get("username")
                .toString();

        PrincipalUser principalUser = (PrincipalUser) principalDetailsService.loadUserByUsername(username);

        authentication = new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
        return authentication;
    }
}
