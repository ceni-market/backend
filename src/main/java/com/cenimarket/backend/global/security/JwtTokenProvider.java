package com.cenimarket.backend.global.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private Key key;

    @PostConstruct
    protected void init() {
        // 보안을 위해 비밀키를 Key 객체로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    /*실제 API를 호출할 때마다 서버에 제출하는 토큰입니다.
    역할: 클라이언트가 매 요청마다 헤더에 담아 보내며, 서버는 이를 보고 "아, 이 사람은 email을 가진 role 권한의 사용자구나"라고 즉시 판단합니다.
    특징: 보안을 위해 수명이 매우 짧습니다 (보통 30분~1시간).
    코드 핵심:
    setSubject(email): 토큰의 주인(사용자 이메일)을 기록합니다.
            claims.put("role", role): 이 사용자가 일반 유저인지, 관리자인지 등의 권한 정보를 토큰에 직접 심습니다. 덕분에 서버는 DB를 매번 조회하지 않고도 권한을 체크할 수 있습니다.*/
    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    /*AccessToken이 만료되었을 때, 다시 로그인을 하지 않고도 새로운 AccessToken을 받기 위해 사용하는 토큰입니다.
    역할: AccessToken보다 수명이 훨씬 깁니다 (보통 1~2주).
    특징: 탈취 시 위험이 크기 때문에, 보통 role 같은 상세 정보는 담지 않고 오직 주인 정보(email)만 담아 최소한의 정보로 구성합니다.
    코드 핵심: AccessToken과 달리 별도의 claims를 추가하지 않고 오직 주체(subject)만 설정하여 보안성을 높였습니다.*/

    public String createRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenExpiration() {
        return this.accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return this.refreshTokenExpiration;
    }

}