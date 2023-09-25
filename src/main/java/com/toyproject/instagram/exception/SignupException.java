package com.toyproject.instagram.exception;

import lombok.Getter;

import java.util.Map;


public class SignupException extends RuntimeException{

    @Getter
    private Map<String, String> errorMap;

    public SignupException(Map<String, String> errorMap) {
        super("회원가입 유효성 검사 오류");
        this.errorMap = errorMap;
        errorMap.forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });

    }
}
