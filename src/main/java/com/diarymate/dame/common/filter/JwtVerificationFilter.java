package com.diarymate.dame.common.filter;

import com.diarymate.dame.common.jwt.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * OncePerRequestFilter를 확장해서 Request당 1번만 실행되는 필터를 구현한다.
 * Tokenprovider JWT를 검증하고 Claims를 얻는데 사용한다.
 * CustomAuthority는 JWT 검증에 성공하면 Authentication 객체에 사용자에게 권한을 넣어준다.
 */
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      // JWT 검증 함수인 verifyJws()를 사용하여 검증
      Map<String, Object> claims = verifyJws(request);

      // Authentication 객체를 SecurityContextHolder에 저장
      setAuthenticationToContext(claims);
    } catch (ExpiredJwtException e) {
      request.setAttribute("Jwt Expired Exception", e);
    } catch (Exception ee) {
      request.setAttribute("Exception", ee);
    }

    filterChain.doFilter(request, response);
  }

  /* 특정 조건에 부합하면 해당 Filter의 동작을 수행하지 않고 다음 Filter로 건너뛰게 만드는 조건 함수 */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    // Request Header에 Authentication을 가죠온다
    String authorization = request.getHeader("Authorization");

    // Authentication Header가 Null이거나 토큰 타입이 Bearer로 시작하지 않는다면 자격증명이 필요없는 경우로 간주에 Filter를 건너뛴다.
    return authorization == null || !authorization.startsWith("Bearer");
  }

  /**
   * @param request : Request
   * @desc JWT 토큰 검증 함수
   */
  private Map<String, Object> verifyJws(HttpServletRequest request) {
    // Request Header 에서 JWT를 가져오면서 토큰의 타입인 'Bearer ' 을 삭제한다.
    String jws = request.getHeader("Authorization").replace("Bearer ", "");

    // JWT Signature를 검증하기 위한 Secret Key를 얻는다.
    String base64EncodedSecretKey = tokenProvider.encodeBase64SecretKey(tokenProvider.getSecretKey());

    // JWT Claims를 Parsing 해온다, Parsing 해올 수 있다는 의미는 Signature 검증에 성공했다는 의미이기도 하다.
    Map<String, Object> claims = tokenProvider.getClaims(jws, base64EncodedSecretKey).getBody();
    return claims;
  }

  // SecurityContextHolder에 Authentication을 넣어주는 함수
  private void setAuthenticationToContext(Map<String, Object> claims) {
    // JWT에서 Parsing 한 Claims에서 Username을 가져옴
    String username = (String) claims.get("name");

    // JWT의 Claims에서 얻은 권한을 기반으로 List<GranteedAuthority>를 생성
    List<GrantedAuthority> authorities = (List)claims.get("roles");

    // username, authority를 포함한 Authentication 객체 생성
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

    // SecurityContextHolder에 Authentication 객체 저장
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}