package com.diarymate.dame.common.handler;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailureHandler implements
    org.springframework.security.web.authentication.AuthenticationFailureHandler {
  private final Logger log = LoggerFactory.getLogger(AuthenticationFailureHandler.class);

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    log.error("Authentication Failed: {}", exception.getMessage());
  }

  private void sendErrorResponse(HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    // Custom 하게 만든 Error Response 인스턴스 작성
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
  }

}
