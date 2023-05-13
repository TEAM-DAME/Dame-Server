package com.diarymate.dame.common.config;

import com.diarymate.dame.member.entity.Member;
import com.diarymate.dame.member.entity.Token;
import com.diarymate.dame.member.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SuccessHandler implements AuthenticationSuccessHandler {
  private final ObjectMapper objectMapper;
  private final TokenService tokenService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
    Member member = Member.of(oAuth2User);

    Token token = tokenService.generateToken(member.getId(), "USER");

    makeTokenResponse(response, token);
  }

  private void makeTokenResponse(HttpServletResponse response, Token token)
      throws IOException {
    response.addHeader(HttpHeaders.AUTHORIZATION, token.getAccessToken());
    response.addHeader("Refresh", token.getRefreshToken());

    PrintWriter writer = response.getWriter();
    writer.println(objectMapper.writeValueAsString(token));
    writer.flush();
  }
}
