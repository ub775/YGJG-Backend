package com.springboot.yogijogi.jwt;


import com.springboot.yogijogi.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException{
        String token = jwtProvider.resolveToken(request);
        System.out.println(token);

        logger.info("[dofilterInternal] token 값 추출 완료. token : {} " , token);

        logger.info("[dofilterInternal] token 값 추출 체크 시작" );
        if(token!=null && jwtProvider.validToken(token)){
            Authentication authentication = jwtProvider.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("[dofilterInternal] token 값 유효성 체크 완료");
        }
        filterChain.doFilter(request,response);


    }



}
