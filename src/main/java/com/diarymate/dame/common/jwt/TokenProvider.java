package com.diarymate.dame.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.io.Encoders;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {
  @Getter
  @Value("${jwt.key}")
  private String secretKey;

  @Getter
  @Value("${jwt.expiration.access}")
  private int accessTokenExpirationMinutes;

  @Getter
  @Value("${jwt.expiration.refresh}")
  private int refreshTokenExpirationMinutes;

  // Plain Text 형태인 Secret Key의 byte[]를 Base64 형식의 문자열로 인코딩 해줌
  public String encodeBase64SecretKey(String secretKey) {
    return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * @param claims : 인증된 사용자 관련 정보
   * @param subject : JWT Title
   * @param expiration : Access Token 만료일
   * @param base64EncodedSecretKey : Sign을 위한 Key를 만들기 위한 인코딩된 Secret Key
   * @desc 인증된 사용자에게 JWT를 최초로 발급해주기 위한 JWT 생성 함수
   */
  public String generateAccessToken(
      Map<String, Object> claims,
      String subject,
      Date expiration,
      String base64EncodedSecretKey
  ) {
    Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

    return Jwts.builder()
        .setClaims(claims) // Authenticated User Info
        .setSubject(subject) // JWT Tiele
        .setIssuedAt(Calendar.getInstance().getTime()) // 발행일자 지정
        .setExpiration(expiration) // 만료일 지정
        .signWith(key) // 서명
        .compact(); // JWT 생성, 직렬화
  }

  // Refresh Token 생성 함수, AccessToken 만료 시 재발급, 여기서 setClaims()는 필요 없다
  public String generateRefreshToken(
      String subject,
      Date expiration,
      String base64EncodedSecretKey
  ) {
    Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(Calendar.getInstance().getTime())
        .setExpiration(expiration)
        .signWith(key)
        .compact();
  }

  // JWT의 Sign에 사용할 Secret Key를 생성해주는 함수
  private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
    // Encoding된 Secret Key를 Decoding 한 후, Byte Array 반환
    byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

    // Byte Array를 기반으로 적절한 HMAC 알고리즘을 적용한 Key 객체 반환
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Jwt의 Signature 검증 함수
  public void verifySignature(
      String jws,
      String base64EncodedSecretKey
  ) {
    Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

    Jwts.parserBuilder()
        .setSigningKey(key) // Sign에 사용된 Key 지정
        .build()
        .parseClaimsJws(jws); // Jwt를 Parsing해서 Claims를 얻는다
  }

  // Token 만료 검증
  public Date getTokenExpiration(int expirationMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MINUTE, expirationMinutes);

    return calendar.getTime();
  }

  // Claims 반환
  public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
    Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

    Jws<Claims> claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(jws);
    return claims;
  }
}
