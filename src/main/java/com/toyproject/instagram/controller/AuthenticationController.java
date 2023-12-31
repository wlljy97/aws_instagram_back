package com.toyproject.instagram.controller;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.exception.SignupException;
import com.toyproject.instagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) {
    // @Vaild 을 검사해라

    // 가능한 ok만 리턴 controller에는 badRequest가 있으면 안됨
        if (bindingResult.hasErrors()) { // 에러가 있으면 True 있을 때만 동작함
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            throw new SignupException(errorMap);
        }

        userService.signupUser(signupReqDto);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login") // 로그인은 post요청 통해서 실행
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {
        String accessToken = userService.signinUser(signinReqDto);
        return ResponseEntity.ok().body(accessToken); // 정상적으로 로그인이 되면 200ok가 난다.
    }

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(userService.authenticate(token)); // 예외를 잡아줌
    }
}


//  서버 실행 하는 곳