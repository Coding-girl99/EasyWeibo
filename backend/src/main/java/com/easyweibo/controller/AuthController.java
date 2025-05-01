package com.easyweibo.controller;

import com.easyweibo.dto.ApiResponse;
import com.easyweibo.dto.LoginRequest;
import com.easyweibo.entity.User;
import com.easyweibo.service.UserService;
import com.easyweibo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        User user = userService.findByUsername(loginRequest.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", jwt);
        result.put("user", user);

        return ApiResponse.success("登录成功", result);
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String nickname = request.get("nickname");

        if (username == null || password == null) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }

        if (nickname == null) {
            nickname = username;
        }

        User user = userService.register(username, password, nickname);
        if (user == null) {
            return ApiResponse.error(400, "用户名已存在");
        }

        return ApiResponse.success("注册成功", null);
    }
}