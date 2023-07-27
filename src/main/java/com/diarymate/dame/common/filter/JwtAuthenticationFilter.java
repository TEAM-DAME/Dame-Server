package com.diarymate.dame.common.filter;

import com.diarymate.dame.common.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final TokenProvider tokenProvider;

  /**
   * @param authenticationManager : 로그인 인증 정보(username/password)를 전달받아 UserDetailsService와 인터랙션 한 뒤
   *                              인증 여부 판단
   * @param tokenProvider          : JWT Token 생성
   */
  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      TokenProvider tokenProvider
  ) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
  }

  // 인증을 시도하는 로직
  @SneakyThrows
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) {
//    OAuth2AuthenticationToken token =

    // AuthenticationManager에게 Token을 전달하며 인증 처리 위임하고 성공하면 '인증된' Authentication 객체 반환
    return authenticationManager.authenticate(authenticationToken);
  }

  // 인증에 성공할 시 호출됨
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws ServletException, IOException {
    String name = authResult.getName();
    Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
    List<String> roles = authorities.stream().map(auth -> auth.toString()).toList();

    // AccessToken 생성
    String accessToken = delegateAccessToken(name, roles);

    // Refresh Token 생성
    String refreshToken = delegateRefreshToken(name);

    // 응답으로 돌려줄 Response의 Header에 Access Token 추가
    response.setHeader("Authorization", "Bearer " + accessToken);

    // 응답으로 돌려줄 Response의 Header에 Refresh Token 추가
    response.setHeader("Refresh", refreshToken);

    // Success Handler 호출
    this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
  }

  // Access Token 생성 함수
  private String delegateAccessToken(String email, List<String> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("email", email);
    claims.put("roles", roles);

    String subject = email;
    Date expiration = tokenProvider.getTokenExpiration(
        tokenProvider.getAccessTokenExpirationMinutes());

    String base64EncodedSecretKey = tokenProvider.encodeBase64SecretKey(tokenProvider.getSecretKey());

    String accessToken = tokenProvider.generateAccessToken(claims, subject, expiration,
        base64EncodedSecretKey);

    return accessToken;
  }

  // Refresh Token 생성 함수
  private String delegateRefreshToken(String name) {
    String subject = name;
    Date expiration = tokenProvider.getTokenExpiration(
        tokenProvider.getRefreshTokenExpirationMinutes());
    String base64EncodedSecretKey = tokenProvider.encodeBase64SecretKey(tokenProvider.getSecretKey());

    String refreshToken = tokenProvider.generateRefreshToken(subject, expiration,
        base64EncodedSecretKey);

    return refreshToken;
  }
}