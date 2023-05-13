package com.diarymate.dame.common.config;

import com.diarymate.dame.member.config.JwtAccessDeniedHandler;
import com.diarymate.dame.member.config.JwtAuthenticationEntryPoint;
import com.diarymate.dame.member.config.JwtAuthenticationFilter;
import com.diarymate.dame.member.service.CustomOAuth2UserService;
import com.diarymate.dame.member.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig {

  private final SuccessHandler successHandler;
  private final CustomOAuth2UserService oAuth2UserService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final TokenService tokenService;

  public SecurityConfig(SuccessHandler successHandler,
      CustomOAuth2UserService oAuth2UserService,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtAccessDeniedHandler jwtAccessDeniedHandler,
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
      TokenService tokenService) {
    this.successHandler = successHandler;
    this.oAuth2UserService = oAuth2UserService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.tokenService = tokenService;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .addFilterBefore(
            jwtAuthenticationFilter,
            BasicAuthenticationFilter.class)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
          .accessDeniedHandler(jwtAccessDeniedHandler)
          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()
        .authorizeHttpRequests()
          .requestMatchers(
              "/login/oauth2/code/**", // 코드 받아오는 redirect_uri
              "/oauth2/**")
          .permitAll()
          .anyRequest().authenticated()
          .and()
        .oauth2Login()
          .userInfoEndpoint().userService(oAuth2UserService)
          .and()
          .successHandler(successHandler)
          .and()
    ;
    return http.build();
  }
}
