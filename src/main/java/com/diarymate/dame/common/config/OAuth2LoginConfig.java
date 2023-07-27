package com.diarymate.dame.common.config;

import com.diarymate.dame.common.filter.JwtAuthenticationFilter;
import com.diarymate.dame.common.filter.JwtVerificationFilter;
import com.diarymate.dame.common.handler.AuthenticationFailureHandler;
import com.diarymate.dame.common.handler.AuthenticationSuccessHandler;
import com.diarymate.dame.common.jwt.TokenProvider;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuth2LoginConfig {
  private final TokenProvider tokenProvider;

  @Value("${jwt.key}")
  private String jwtKey;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity
        .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .csrf(CsrfConfigurer::disable)
        .cors(Customizer.withDefaults())
        .httpBasic(HttpBasicConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
//        .apply(new CustomFilter())
//        .and()
        .authorizeHttpRequests(authorize -> {
              authorize
                  .requestMatchers("/admin/**").hasRole("ADMIN")
                  .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                  .anyRequest().authenticated();
            }
        )
        .oauth2Login(Customizer.withDefaults())
    ;

    return httpSecurity.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));   // 모든 Origin에 대해 HTTP 통신 허용
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "UPDATE", "DELETE"));  // 허용할 HTTP Method

    // CorsConfigurationSource 인터페이스의 구현 객체 생성
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    // 모든 URL에 CORS 정책 적용
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey.getBytes()));
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] bytes = jwtKey.getBytes();
    SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "RSA");
    return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(MacAlgorithm.HS512).build();
  }

  // Custom Configurer, JwtAuthenticationFilter 등록 - AbstractHttpConfigure를 상속해서 구현할 수 있다.
  public class CustomFilter extends AbstractHttpConfigurer<CustomFilter, HttpSecurity> {
    @Override
    public void configure(HttpSecurity builder) throws Exception {
      // getSharedObject를 통해 AuthenticationManager 객체를 얻을 수 있음
      AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

      // JwtAuthenticationFilter을 생성하면서 이 Filter 에서 사용되는 Manager,Tokenizer를 넣어줌
      // Success/Failure Handler 추가
      JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, tokenProvider);
//      jwtAuthenticationFilter.setFilterProcessesUrl("/auth/oauth2/login"); // Spring Security의 Default Request URL인 /login을 Custom한 API로 변경
      jwtAuthenticationFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler());
      jwtAuthenticationFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler());

      // JWT Verifycation Filter 추가
      JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(tokenProvider);

      // Security Filter에 추가
      builder.addFilter(jwtAuthenticationFilter);
      builder.addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
    }
  }

}
