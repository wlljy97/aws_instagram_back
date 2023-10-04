package com.toyproject.instagram.controller;

import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.exception.SignupException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionController {

    // spring framework 에서 예외처리 하는 방법

    // @ExceptionHandler 어노테이션을 사용하여 SignatureException 예외를 처리하는 메소드를 정의
    @ExceptionHandler(SignupException.class)
    public ResponseEntity<?> signupExceptionHandle(SignupException signupException) {
        return ResponseEntity.badRequest().body(signupException.getErrorMap());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> JwtExceptionHandle(JwtException jwtException) {
        return ResponseEntity.badRequest().body(jwtException.getMessage());
    }
}


// ResponseEntity는 HTTP 응답을 나타내는 객체
// ResponseEntity.badRequest()는 HTTP 상태 코드 400 Bad Request를 생성하는 메소드
// .body(signupException.getErrorMap())은 응답 본문에 SignupException에서 생성한 에러 맵을 설정