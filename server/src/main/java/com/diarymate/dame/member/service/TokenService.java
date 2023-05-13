package com.diarymate.dame.member.service;

import com.diarymate.dame.member.entity.Member;
import com.diarymate.dame.member.entity.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {

  private static final String BEARER_PREFIX = "Bearer ";
  private final String JWT_SECRET;
  private final int JWT_ACCESS_EXPIRATION_TIME;
  private final int JWT_REFRESH_EXPIRATION_TIME;
  private final MemberService memberService;
  private Key key;

  public TokenService(
      @Value("${jwt.secret}") String jwt_secret,
      @Value("${jwt.expiration.access}") int jwt_access_expiration_time,
      @Value("${jwt.expiration.refresh}") int jwt_refresh_expiration_time,
      MemberService memberService
  ) {
    this.JWT_SECRET = jwt_secret;
    this.JWT_ACCESS_EXPIRATION_TIME = jwt_access_expiration_time;
    this.JWT_REFRESH_EXPIRATION_TIME = jwt_refresh_expiration_time;
    this.memberService = memberService;
  }

  @PostConstruct
  protected void init() {
    byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public Token generateToken(long memberId, String role) {
    log.info(role);
    Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
    claims.put("role", role);

    Instant now = Instant.now();
    return new Token(
        makeJwtValue(claims, now, JWT_ACCESS_EXPIRATION_TIME),
        makeJwtValue(claims, now, JWT_REFRESH_EXPIRATION_TIME));
  }

  private String makeJwtValue(Claims claims, Instant now, long accessTokenValidityInMilliseconds) {
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(now.toEpochMilli()))
        .setExpiration(new Date(now.toEpochMilli() + accessTokenValidityInMilliseconds))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    if (null == token) {
      return false;
    }

    if (false == token.startsWith(BEARER_PREFIX)) {
      return false;
    }

    String value = token.substring(BEARER_PREFIX.length());

    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(value);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.error("잘못된 jwt 서명을 가진 토큰입니다", e);
    } catch (ExpiredJwtException e) {
      log.error("만료된 jwt 토큰입니다", e);
    } catch (UnsupportedJwtException e) {
      log.error("지원하지 않는 jwt 토큰입니다", e);
    } catch (IllegalArgumentException e) {
      log.error("잘못된 jwt 토큰입니다", e);
    }
    return false;
  }

  public Authentication getAuthentication(String token) {
    Long memberId = getMemberId(token);
    Optional<Member> findMember = memberService.findMemberById(memberId);
    return new UsernamePasswordAuthenticationToken(findMember, "",
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
  }

  public long getMemberId(String token) {
    String value = token.substring(BEARER_PREFIX.length());
    String subject = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(value).getBody()
        .getSubject();
    return Long.parseLong(subject);
  }

}
