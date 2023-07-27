package com.diarymate.dame.common.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class AuthenticationSuccessHandler implements
    org.springframework.security.web.authentication.AuthenticationSuccessHandler {
  private final Logger log = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    log.info("Authentication Success");
  }

}
