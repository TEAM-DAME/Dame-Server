package com.diarymate.dame.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {

  @Id
  @Column(name = "member_id")
//  @GeneratedValue(strategy = GenerationType.IDENTITY) // 지금은 kakao_id로 특정을 하고 있어 AutoIncrease 사용 안함
  private Long id;
  @Column(name = "username")
  private String username;
  @Column(name = "email")
  private String email;
  @Enumerated(EnumType.STRING)
  private Role role;

  public static Member of(OAuth2User oAuth2User) {
    Map<String, Object> attributes = oAuth2User.getAttributes();
    return Member.builder()
        .id((long) attributes.get("id"))
        .email((String)attributes.get("email"))
        .username((String)attributes.get("username"))
        .build();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  public enum Role {
    ROLE_USER,
    ROLE_ADMIN
  }
}