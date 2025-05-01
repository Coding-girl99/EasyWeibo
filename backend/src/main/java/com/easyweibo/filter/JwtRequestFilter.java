package com.easyweibo.filter;

import com.easyweibo.dto.ApiResponse;
import com.easyweibo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String[] PUBLIC_PATHS = {
            "/auth/**",
            "/weibo/public/**",
            "/images/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/*.html",
            "/*.js",
            "/*.css",
            "/*.ico",
            "/static/**",
            "/favicon.ico",
            "/error"
    };

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(PUBLIC_PATHS)
                .anyMatch(p -> pathMatcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 处理OPTIONS预检请求
        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, x-auth-token");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                try {
                    username = jwtUtil.extractUsername(jwt);
                    if (username == null) {
                        logger.error("无法从Token中提取用户名");
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的Token");
                        return;
                    }
                    // if (jwtUtil.isTokenExpired(jwt)) {
                    // logger.warn("Token已过期: {}", username);
                    // sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token已过期");
                    // return;
                    // }
                } catch (Exception e) {
                    logger.error("Token解析失败: {}", e.getMessage());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的Token");
                    return;
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.debug("Authentication successful for user: {}", username);
                } else {
                    logger.warn("Invalid JWT token for user: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的Token");
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "认证过程中发生错误");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<?> apiResponse = ApiResponse.error(status, message);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        logger.error("JWT验证失败: {} - {} - {}", status, message, response.getHeader("X-Request-ID"));
        logger.debug("错误响应详情: {}", apiResponse);
    }
}
